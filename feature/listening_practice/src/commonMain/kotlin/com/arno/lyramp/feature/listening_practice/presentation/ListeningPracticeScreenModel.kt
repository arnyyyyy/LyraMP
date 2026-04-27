package com.arno.lyramp.feature.listening_practice.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.listening_practice.domain.CheckAnswerUseCase
import com.arno.lyramp.feature.listening_practice.domain.LoadPracticeDataUseCase
import com.arno.lyramp.feature.listening_practice.domain.PracticeDataResult
import com.arno.lyramp.feature.listening_practice.model.LineCheckResult
import com.arno.lyramp.feature.listening_practice.model.LyricLine
import com.arno.lyramp.feature.listening_practice.model.PracticeMode
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import com.arno.lyramp.feature.listening_practice.playback.LinePlaybackController
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ListeningPracticeScreenModel(
        private val track: PracticeTrack,
        private val loadPracticeData: LoadPracticeDataUseCase,
        private val playback: LinePlaybackController,
        private val checkAnswer: CheckAnswerUseCase,
) : ScreenModel {

        private val _uiState = MutableStateFlow<ListeningPracticeUiState>(ListeningPracticeUiState.Loading)
        val uiState: StateFlow<ListeningPracticeUiState> = _uiState.asStateFlow()

        private data class PracticeState(
                val lines: List<LyricLine> = emptyList(),
                val currentLineIndex: Int = 0,
                val userInput: String = "",
                val correctCount: Int = 0,
                val incorrectCount: Int = 0,
                val practiceMode: PracticeMode = PracticeMode.FULL_SONG,
                val lastAnsweredLine: LyricLine? = null,
        )

        private val practiceState = MutableStateFlow(PracticeState())

        init {
                loadPractice()
                observeState()
        }

        private fun loadPractice() {
                screenModelScope.launch {
                        _uiState.value = ListeningPracticeUiState.Loading
                        try {
                                when (val result = loadPracticeData(track)) {
                                        is PracticeDataResult.NoLyrics ->
                                                _uiState.value = ListeningPracticeUiState.Error("Не удалось получить текст песни")

                                        is PracticeDataResult.NoStreaming ->
                                                _uiState.value = ListeningPracticeUiState.Error("Не удалось получить аудио трека")

                                        is PracticeDataResult.Success -> {
                                                val lines = result.lyricLines
                                                playback.prepare(result.downloadInfo.url)
                                                val hasTimecodes = lines.any { it.hasTimecode }
                                                val initialMode = if (hasTimecodes) PracticeMode.RANDOM_LINE else PracticeMode.FULL_SONG
                                                practiceState.value = PracticeState(
                                                        lines = lines,
                                                        practiceMode = initialMode,
                                                )
                                                if (hasTimecodes && initialMode == PracticeMode.RANDOM_LINE) pickRandomLine()
                                                publishReady()
                                        }
                                }
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (e: Exception) {
                                _uiState.value = ListeningPracticeUiState.Error(e.message ?: "Неизвестная ошибка")
                        }
                }
        }

        private fun observeState() {
                screenModelScope.launch {
                        combine(
                                practiceState,
                                playback.isPlaying,
                                playback.currentPositionMs,
                                playback.durationMs,
                                playback.isReady,
                                playback.currentLineIsPlaying,
                                playback.isSlowMode,
                        ) { values ->
                                Snapshot(
                                        practice = values[0] as PracticeState,
                                        isPlaying = values[1] as Boolean,
                                        positionMs = values[2] as Long,
                                        durationMs = values[3] as Long,
                                        isReady = values[4] as Boolean,
                                        currentLineIsPlaying = values[5] as Boolean,
                                        isSlowMode = values[6] as Boolean,
                                )
                        }.collect { snapshot ->
                                val current = _uiState.value
                                when {
                                        current is ListeningPracticeUiState.Ready -> _uiState.value = current.copy(
                                                lines = snapshot.practice.lines,
                                                currentLineIndex = snapshot.practice.currentLineIndex,
                                                isPlaying = snapshot.isPlaying,
                                                currentPositionMs = snapshot.positionMs,
                                                durationMs = snapshot.durationMs,
                                                userInput = snapshot.practice.userInput,
                                                correctCount = snapshot.practice.correctCount,
                                                incorrectCount = snapshot.practice.incorrectCount,
                                                practiceMode = snapshot.practice.practiceMode,
                                                currentLineIsPlaying = snapshot.currentLineIsPlaying,
                                                isSlowMode = snapshot.isSlowMode,
                                                lastAnsweredLine = snapshot.practice.lastAnsweredLine,
                                        )

                                        current is ListeningPracticeUiState.Loading &&
                                            snapshot.isReady &&
                                            snapshot.practice.lines.isNotEmpty() -> publishReady()

                                        else -> Unit
                                }
                        }
                }
        }

        private fun publishReady() {
                val state = practiceState.value
                _uiState.value = ListeningPracticeUiState.Ready(
                        track = track,
                        lines = state.lines,
                        currentLineIndex = state.currentLineIndex,
                        isPlaying = playback.isPlaying.value,
                        currentPositionMs = playback.currentPositionMs.value,
                        durationMs = playback.durationMs.value,
                        userInput = state.userInput,
                        correctCount = state.correctCount,
                        incorrectCount = state.incorrectCount,
                        practiceMode = state.practiceMode,
                        hasTimecodes = state.lines.any { it.hasTimecode },
                        currentLineIsPlaying = playback.currentLineIsPlaying.value,
                        isSlowMode = playback.isSlowMode.value,
                        lastAnsweredLine = state.lastAnsweredLine,
                )
        }

        fun onSwitchMode(mode: PracticeMode) {
                playback.stopSegment()
                playback.setSlowMode(false)
                practiceState.update { it.copy(practiceMode = mode, userInput = "", lastAnsweredLine = null) }
                if (mode == PracticeMode.RANDOM_LINE) pickRandomLine()
                publishReady()
        }

        fun onPlayCurrentLineClick() {
                val state = practiceState.value
                val line = state.lines.getOrNull(state.currentLineIndex) ?: return
                val startMs = line.startMs ?: return
                val endMs = line.endMs ?: return
                playback.toggleSegment(screenModelScope, startMs, endMs)
        }

        fun onPlayPauseClick() = playback.playPause()
        fun onToggleSlowMode() = playback.toggleSlowMode()
        fun onMoveBackClick() = playback.rewind(SEEK_STEP_MS)
        fun onMoveForwardClick() = playback.forward(SEEK_STEP_MS)

        fun onUserInputChange(input: String) = practiceState.update { it.copy(userInput = input) }

        fun onCheckLine() {
                val state = practiceState.value
                if (state.currentLineIndex >= state.lines.size) return
                val currentLine = state.lines[state.currentLineIndex]
                val isCorrect = checkAnswer(state.userInput, currentLine.text)
                advanceLine(
                        currentLine.copy(
                                userInput = state.userInput,
                                checkResult = if (isCorrect) LineCheckResult.CORRECT else LineCheckResult.INCORRECT,
                        ),
                        isCorrect,
                )
        }

        fun onSkipLine() {
                val state = practiceState.value
                if (state.currentLineIndex >= state.lines.size) return
                advanceLine(
                        state.lines[state.currentLineIndex].copy(userInput = "", checkResult = LineCheckResult.INCORRECT),
                        isCorrect = false,
                )
        }

        fun onRestart() {
                playback.stopSegment()
                playback.setSlowMode(false)
                practiceState.update {
                        it.copy(
                                lines = it.lines.map { line -> line.copy(userInput = "", checkResult = LineCheckResult.PENDING) },
                                currentLineIndex = 0, userInput = "",
                                correctCount = 0, incorrectCount = 0, lastAnsweredLine = null,
                        )
                }
                if (practiceState.value.practiceMode == PracticeMode.RANDOM_LINE) pickRandomLine()
                else playback.seekTo(0)
                publishReady()
        }

        private fun pickRandomLine() {
                val lines = practiceState.value.lines
                val withTimecodes = lines.indices.filter { lines[it].hasTimecode }
                if (withTimecodes.isEmpty()) return
                practiceState.update {
                        it.copy(currentLineIndex = withTimecodes.random(), userInput = "")
                }
        }

        private fun advanceLine(updatedLine: LyricLine, isCorrect: Boolean) {
                val state = practiceState.value
                val updatedLines = state.lines.toMutableList().apply {
                        set(state.currentLineIndex, updatedLine)
                }
                val newCorrect = state.correctCount + if (isCorrect) 1 else 0
                val newIncorrect = state.incorrectCount + if (!isCorrect) 1 else 0

                if (state.practiceMode == PracticeMode.RANDOM_LINE) {
                        playback.stopSegment()
                        practiceState.update {
                                it.copy(
                                        lines = updatedLines,
                                        userInput = "",
                                        correctCount = newCorrect,
                                        incorrectCount = newIncorrect,
                                        lastAnsweredLine = updatedLine,
                                )
                        }
                        pickRandomLine()
                        publishReady()
                        return
                }

                val nextIndex = state.currentLineIndex + 1
                practiceState.update {
                        it.copy(
                                lines = updatedLines,
                                currentLineIndex = nextIndex,
                                userInput = "",
                                correctCount = newCorrect,
                                incorrectCount = newIncorrect,
                        )
                }
                if (nextIndex >= updatedLines.size) {
                        playback.pause()
                        _uiState.value = ListeningPracticeUiState.Completed(
                                track = track,
                                lines = updatedLines,
                                correctCount = newCorrect,
                                incorrectCount = newIncorrect,
                        )
                } else publishReady()
        }

        override fun onDispose() = playback.release()

        private data class Snapshot(
                val practice: PracticeState,
                val isPlaying: Boolean,
                val positionMs: Long,
                val durationMs: Long,
                val isReady: Boolean,
                val currentLineIsPlaying: Boolean,
                val isSlowMode: Boolean,
        )

        private companion object {
                const val SEEK_STEP_MS = 5_000L
        }
}
