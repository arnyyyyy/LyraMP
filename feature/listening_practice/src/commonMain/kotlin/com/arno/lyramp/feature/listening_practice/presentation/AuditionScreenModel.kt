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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class AuditionScreenModel(
        private val prefetcher: AuditionLinePrefetcher,
        private val playback: LinePlaybackController,
        private val checkAnswer: CheckAnswerUseCase,
        private val language: String?,
        private val roundSize: Int = DEFAULT_ROUND_SIZE,
) : ScreenModel {

        private val _uiState = MutableStateFlow<AuditionUiState>(AuditionUiState.Loading)
        val uiState: StateFlow<AuditionUiState> = _uiState.asStateFlow()

        private enum class ContentStatus {
                Loading,
                Ready,
                Completed,
                Empty,
                Error,
        }

        private data class AuditionPracticeState(
                val status: ContentStatus = ContentStatus.Loading,
                val current: AuditionLine? = null,
                val userInput: String = "",
                val roundIndex: Int = 0,
                val roundSize: Int,
                val correctCount: Int = 0,
                val incorrectCount: Int = 0,
                val answeredLines: List<LyricLine> = emptyList(),
                val lastAnsweredLine: LyricLine? = null,
                val isPreparingPlayer: Boolean = false,
                val errorMessage: String? = null,
        )

        private val practiceState = MutableStateFlow(AuditionPracticeState(roundSize = roundSize))
        private val advanceMutex = Mutex()
        private var advanceJob: Job? = null
        private var preparePlayerJob: Job? = null

        private var startExpansionMs: Long = 0
        private var endExpansionMs: Long = 0

        init {
                observeState()
                startNewRound()
        }

        private fun startNewRound() {
                advanceJob?.cancel()
                preparePlayerJob?.cancel()
                startExpansionMs = 0
                endExpansionMs = 0
                playback.stopSegment()
                practiceState.value = AuditionPracticeState(roundSize = roundSize)
                prefetcher.start(screenModelScope, language)
                launchAdvanceToNext()
        }

        fun onPlayAgain() = startNewRound()

        private fun observeState() {
                screenModelScope.launch {
                        combine(
                                practiceState,
                                playback.isReady,
                                playback.currentLineIsPlaying,
                                playback.isSlowMode,
                        ) { state, isPlayerReady, currentLineIsPlaying, isSlowMode ->
                                state.toUiState(
                                        isPlayerReady = isPlayerReady,
                                        currentLineIsPlaying = currentLineIsPlaying,
                                        isSlowMode = isSlowMode,
                                )
                        }.collect { state ->
                                _uiState.value = state
                        }
                }
        }

        private fun launchAdvanceToNext() {
                if (advanceJob?.isActive == true) return
                advanceJob = screenModelScope.launch { advanceToNext() }
        }

        private suspend fun advanceToNext() {
                advanceMutex.withLock {
                        startExpansionMs = 0
                        endExpansionMs = 0
                        playback.stopSegment()
                        val state = practiceState.value
                        if (state.roundIndex >= state.roundSize) {
                                prefetcher.close()
                                practiceState.update { it.copy(status = ContentStatus.Completed) }
                                return@withLock
                        }

                        val received = prefetcher.next()
                        val next = received.getOrNull()
                        if (next == null) {
                                if (state.answeredLines.isNotEmpty()) {
                                        practiceState.update { it.copy(status = ContentStatus.Completed) }
                                } else {
                                        val errorMessage = received.exceptionOrNull()?.message
                                        practiceState.update {
                                                if (errorMessage == null) it.copy(status = ContentStatus.Empty)
                                                else it.copy(status = ContentStatus.Error, errorMessage = errorMessage)
                                        }
                                }
                                return@withLock
                        }

                        preparePlayerJob?.cancel()
                        practiceState.update {
                                it.copy(
                                        status = ContentStatus.Ready,
                                        current = next,
                                        userInput = "",
                                        lastAnsweredLine = null,
                                        isPreparingPlayer = true,
                                        errorMessage = null,
                                )
                        }

                        preparePlayerJob = screenModelScope.launch {
                                try {
                                        playback.prepare(next.downloadInfo.url)
                                } catch (ce: CancellationException) {
                                        throw ce
                                } catch (e: Exception) {
                                        practiceState.update {
                                                if (it.current == next) {
                                                        it.copy(
                                                                status = ContentStatus.Error,
                                                                errorMessage = e.message ?: "Не удалось подготовить аудио",
                                                        )
                                                } else {
                                                        it
                                                }
                                        }
                                } finally {
                                        practiceState.update {
                                                if (it.current == next) it.copy(isPreparingPlayer = false) else it
                                        }
                                }
                        }
                }
        }

        fun onUserInputChange(input: String) {
                practiceState.update {
                        if (it.status == ContentStatus.Ready && it.lastAnsweredLine == null) it.copy(userInput = input)
                        else it
                }
        }

        fun onPlayCurrentLineClick() {
                val state = practiceState.value
                val line = state.current?.line ?: return
                if (state.status != ContentStatus.Ready || !playback.isReady.value) return
                val startMs = line.startMs ?: return
                val endMs = line.endMs ?: return
                playback.toggleSegment(
                        screenModelScope,
                        (startMs - startExpansionMs).coerceAtLeast(0),
                        endMs + endExpansionMs,
                )
        }

        fun onExpandStart() {
                val state = practiceState.value
                val line = state.current?.line ?: return
                if (state.status != ContentStatus.Ready || !playback.isReady.value) return
                val startMs = line.startMs ?: return
                val endMs = line.endMs ?: return
                startExpansionMs += EXPAND_STEP_MS
                playback.playSegment(
                        screenModelScope,
                        (startMs - startExpansionMs).coerceAtLeast(0),
                        endMs + endExpansionMs,
                )
        }

        fun onExpandEnd() {
                val state = practiceState.value
                val line = state.current?.line ?: return
                if (state.status != ContentStatus.Ready || !playback.isReady.value) return
                val startMs = line.startMs ?: return
                val endMs = line.endMs ?: return
                endExpansionMs += EXPAND_STEP_MS
                playback.playSegment(
                        screenModelScope,
                        (startMs - startExpansionMs).coerceAtLeast(0),
                        endMs + endExpansionMs,
                )
        }

        fun onToggleSlowMode() = playback.toggleSlowMode()
        fun onAppBackground() = playback.stopSegment()

        fun onCheckLine() {
                val state = practiceState.value
                val currentLine = state.current?.line ?: return
                if (state.status != ContentStatus.Ready || state.lastAnsweredLine != null) return
                val isCorrect = checkAnswer(state.userInput, currentLine.text)
                advance(userInput = state.userInput, isCorrect = isCorrect)
        }

        fun onSkipLine() {
                val state = practiceState.value
                if (state.status != ContentStatus.Ready || state.lastAnsweredLine != null) return
                advance(userInput = "", isCorrect = false)
        }

        fun onNextLine() {
                val state = practiceState.value
                if (state.status != ContentStatus.Ready || state.lastAnsweredLine == null) return
                practiceState.update { it.copy(status = ContentStatus.Loading, lastAnsweredLine = null, userInput = "") }
                launchAdvanceToNext()
        }

        private fun advance(userInput: String, isCorrect: Boolean) {
                val state = practiceState.value
                val currentLine = state.current?.line ?: return
                val answered = currentLine.copy(
                        userInput = userInput,
                        checkResult = if (isCorrect) LineCheckResult.CORRECT else LineCheckResult.INCORRECT,
                )
                practiceState.update {
                        if (it.status != ContentStatus.Ready || it.lastAnsweredLine != null) {
                                it
                        } else {
                                it.copy(
                                        userInput = userInput,
                                        correctCount = it.correctCount + if (isCorrect) 1 else 0,
                                        incorrectCount = it.incorrectCount + if (isCorrect) 0 else 1,
                                        roundIndex = it.roundIndex + 1,
                                        answeredLines = it.answeredLines + answered,
                                        lastAnsweredLine = answered,
                                )
                        }
                }
        }

        override fun onDispose() {
                advanceJob?.cancel()
                preparePlayerJob?.cancel()
                prefetcher.close()
                playback.release()
        }

        private fun AuditionPracticeState.toUiState(
                isPlayerReady: Boolean,
                currentLineIsPlaying: Boolean,
                isSlowMode: Boolean,
        ): AuditionUiState {
                return when (status) {
                        ContentStatus.Loading -> AuditionUiState.Loading
                        ContentStatus.Ready -> current?.let { line ->
                                AuditionUiState.Ready(
                                        track = line.track,
                                        currentLine = line.line,
                                        userInput = userInput,
                                        correctCount = correctCount,
                                        incorrectCount = incorrectCount,
                                        roundIndex = roundIndex,
                                        roundSize = roundSize,
                                        currentLineIsPlaying = currentLineIsPlaying,
                                        isSlowMode = isSlowMode,
                                        lastAnsweredLine = lastAnsweredLine,
                                        isPlayerReady = isPlayerReady && !isPreparingPlayer,
                                )
                        } ?: AuditionUiState.Loading

                        ContentStatus.Completed -> AuditionUiState.Completed(
                                answeredLines = answeredLines,
                                correctCount = correctCount,
                                incorrectCount = incorrectCount,
                        )

                        ContentStatus.Empty -> AuditionUiState.Empty
                        ContentStatus.Error -> AuditionUiState.Error(errorMessage ?: "Ошибка")
                }
        }

        private companion object {
                const val DEFAULT_ROUND_SIZE = 10
                const val EXPAND_STEP_MS = 1_000L
        }
}
