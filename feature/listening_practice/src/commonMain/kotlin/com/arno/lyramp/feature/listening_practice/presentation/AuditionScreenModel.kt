package com.arno.lyramp.feature.listening_practice.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.listening_practice.domain.AuditionLinePrefetcher
import com.arno.lyramp.feature.listening_practice.domain.CheckAnswerUseCase
import com.arno.lyramp.feature.listening_practice.model.AuditionLine
import com.arno.lyramp.feature.listening_practice.model.LineCheckResult
import com.arno.lyramp.feature.listening_practice.model.LyricLine
import com.arno.lyramp.feature.listening_practice.playback.LinePlaybackController
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class AuditionScreenModel(
        private val prefetcher: AuditionLinePrefetcher,
        private val playback: LinePlaybackController,
        private val checkAnswer: CheckAnswerUseCase,
        private val language: String?,
        private val roundSize: Int = DEFAULT_ROUND_SIZE,
) : ScreenModel {

        private val _uiState = MutableStateFlow<AuditionUiState>(AuditionUiState.Loading)
        val uiState: StateFlow<AuditionUiState> = _uiState.asStateFlow()

        private var current: AuditionLine? = null
        private var preparePlayerJob: Job? = null

        private var startExpansionMs: Long = 0
        private var endExpansionMs: Long = 0

        private var roundIndex = 0
        private var correctCount = 0
        private var incorrectCount = 0
        private val answeredLines = mutableListOf<LyricLine>()
        private var lastAnswered: LyricLine? = null

        init {
                startNewRound()
        }

        private fun startNewRound() {
                roundIndex = 0
                correctCount = 0
                incorrectCount = 0
                answeredLines.clear()
                lastAnswered = null
                prefetcher.start(screenModelScope, language)
                observePlayback()
                screenModelScope.launch { advanceToNext() }
        }

        fun onPlayAgain() = startNewRound()

        private fun observePlayback() {
                screenModelScope.launch {
                        playback.isReady.collect { ready ->
                                val s = _uiState.value as? AuditionUiState.Ready ?: return@collect
                                if (s.isPlayerReady != ready) _uiState.value = s.copy(isPlayerReady = ready)
                        }
                }
                screenModelScope.launch {
                        playback.currentLineIsPlaying.collect { playing ->
                                val s = _uiState.value as? AuditionUiState.Ready ?: return@collect
                                if (s.currentLineIsPlaying != playing) _uiState.value = s.copy(currentLineIsPlaying = playing)
                        }
                }
                screenModelScope.launch {
                        playback.isSlowMode.collect { slow ->
                                val s = _uiState.value as? AuditionUiState.Ready ?: return@collect
                                if (s.isSlowMode != slow) _uiState.value = s.copy(isSlowMode = slow)
                        }
                }
        }

        private suspend fun advanceToNext() {
                startExpansionMs = 0
                endExpansionMs = 0
                playback.stopSegment()
                if (roundIndex >= roundSize) {
                        _uiState.value = AuditionUiState.Completed(
                                answeredLines = answeredLines.toList(),
                                correctCount = correctCount,
                                incorrectCount = incorrectCount,
                        )
                        return
                }

                val received = prefetcher.next()
                val next = received.getOrNull()
                if (next == null) {
                        if (answeredLines.isNotEmpty()) {
                                _uiState.value = AuditionUiState.Completed(
                                        answeredLines = answeredLines.toList(),
                                        correctCount = correctCount,
                                        incorrectCount = incorrectCount,
                                )
                        } else {
                                _uiState.value = received.exceptionOrNull()
                                        ?.let { AuditionUiState.Error(it.message ?: "Ошибка") }
                                        ?: AuditionUiState.Empty
                        }
                        return
                }

                preparePlayerJob?.cancel()
                current = next
                publishReady(isPlayerReady = false)

                preparePlayerJob = screenModelScope.launch {
                        try {
                                playback.prepare(next.downloadInfo.url)
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (_: Exception) {
                        }
                }
        }

        private fun publishReady(isPlayerReady: Boolean = playback.isReady.value) {
                val cur = current ?: return
                _uiState.value = AuditionUiState.Ready(
                        track = cur.track,
                        currentLine = cur.line,
                        userInput = "",
                        correctCount = correctCount,
                        incorrectCount = incorrectCount,
                        roundIndex = roundIndex,
                        roundSize = roundSize,
                        currentLineIsPlaying = playback.currentLineIsPlaying.value,
                        isSlowMode = playback.isSlowMode.value,
                        lastAnsweredLine = lastAnswered,
                        isPlayerReady = isPlayerReady,
                )
        }

        fun onUserInputChange(input: String) {
                val state = _uiState.value as? AuditionUiState.Ready ?: return
                _uiState.value = state.copy(userInput = input)
        }

        fun onPlayCurrentLineClick() {
                val state = _uiState.value as? AuditionUiState.Ready ?: return
                if (!state.isPlayerReady) return
                val startMs = state.currentLine.startMs ?: return
                val endMs = state.currentLine.endMs ?: return
                playback.toggleSegment(
                        screenModelScope,
                        (startMs - startExpansionMs).coerceAtLeast(0),
                        endMs + endExpansionMs,
                )
        }

        fun onExpandStart() {
                val state = _uiState.value as? AuditionUiState.Ready ?: return
                if (!state.isPlayerReady) return
                val startMs = state.currentLine.startMs ?: return
                val endMs = state.currentLine.endMs ?: return
                startExpansionMs += EXPAND_STEP_MS
                playback.playSegment(
                        screenModelScope,
                        (startMs - startExpansionMs).coerceAtLeast(0),
                        endMs + endExpansionMs,
                )
        }

        fun onExpandEnd() {
                val state = _uiState.value as? AuditionUiState.Ready ?: return
                if (!state.isPlayerReady) return
                val startMs = state.currentLine.startMs ?: return
                val endMs = state.currentLine.endMs ?: return
                endExpansionMs += EXPAND_STEP_MS
                playback.playSegment(
                        screenModelScope,
                        (startMs - startExpansionMs).coerceAtLeast(0),
                        endMs + endExpansionMs,
                )
        }

        fun onToggleSlowMode() = playback.toggleSlowMode()

        fun onCheckLine() {
                val state = _uiState.value as? AuditionUiState.Ready ?: return
                if (state.lastAnsweredLine != null) return
                val isCorrect = checkAnswer(state.userInput, state.currentLine.text)
                advance(state.userInput, isCorrect)
        }

        fun onSkipLine() {
                val state = _uiState.value as? AuditionUiState.Ready ?: return
                if (state.lastAnsweredLine != null) return
                advance(userInput = "", isCorrect = false)
        }

        fun onNextLine() {
                val state = _uiState.value as? AuditionUiState.Ready ?: return
                if (state.lastAnsweredLine == null) return
                lastAnswered = null
                screenModelScope.launch { advanceToNext() }
        }

        private fun advance(userInput: String, isCorrect: Boolean) {
                val state = _uiState.value as? AuditionUiState.Ready ?: return
                val answered = state.currentLine.copy(
                        userInput = userInput,
                        checkResult = if (isCorrect) LineCheckResult.CORRECT else LineCheckResult.INCORRECT,
                )
                lastAnswered = answered
                answeredLines.add(answered)
                if (isCorrect) correctCount++ else incorrectCount++
                roundIndex++
                _uiState.value = state.copy(
                        userInput = userInput,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount,
                        roundIndex = roundIndex,
                        lastAnsweredLine = answered,
                )
        }

        override fun onDispose() {
                preparePlayerJob?.cancel()
                prefetcher.close()
                playback.release()
        }

        private companion object {
                const val DEFAULT_ROUND_SIZE = 10
                const val EXPAND_STEP_MS = 1_000L
        }
}
