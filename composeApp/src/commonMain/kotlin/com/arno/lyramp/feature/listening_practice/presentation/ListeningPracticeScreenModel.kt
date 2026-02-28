package com.arno.lyramp.feature.listening_practice.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.listening_practice.domain.ListeningPracticeUseCase
import com.arno.lyramp.feature.listening_practice.model.LineCheckResult
import com.arno.lyramp.feature.listening_practice.model.LyricLine
import com.arno.lyramp.feature.listening_practice.model.PracticeMode
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import com.arno.lyramp.util.StreamingPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ListeningPracticeScreenModel(
        private val track: PracticeTrack,
        private val repository: ListeningPracticeUseCase
) : ScreenModel {
        private val player = StreamingPlayer()

        private val _uiState = MutableStateFlow<ListeningPracticeUiState>(ListeningPracticeUiState.Loading)
        val uiState: StateFlow<ListeningPracticeUiState> = _uiState.asStateFlow()

        private data class PracticeState(
                val lines: List<LyricLine> = emptyList(),
                val currentLineIndex: Int = 0,
                val userInput: String = "",
                val correctCount: Int = 0,
                val incorrectCount: Int = 0,
                val practiceMode: PracticeMode = PracticeMode.FULL_SONG,
                val currentLineIsPlaying: Boolean = false
        )

        private val practiceState = MutableStateFlow(PracticeState())
        private var linePlaybackJob: Job? = null

        init {
                loadPractice()
                observePlayerState()
                observePracticeState()
        }

        private fun loadPractice() {
                screenModelScope.launch {
                        _uiState.value = ListeningPracticeUiState.Loading
                        try {
                                when (val result = repository.loadPracticeData(track)) {
                                        is PracticeDataResult.NoLyrics -> _uiState.value = ListeningPracticeUiState.Error("Не удалось получить текст песни")
                                        is PracticeDataResult.NoStreaming ->
                                                _uiState.value = ListeningPracticeUiState.Error("Не удалось получить аудио трека")

                                        is PracticeDataResult.Success -> {
                                                val lines = result.lyricLines
                                                val downloadInfo = result.downloadInfo
                                                player.prepare(downloadInfo.url)
                                                val hasTimecodes = lines.any { it.hasTimecode }
                                                val initialMode = if (hasTimecodes) PracticeMode.RANDOM_LINE else PracticeMode.FULL_SONG
                                                practiceState.value = PracticeState(
                                                        lines = lines,
                                                        practiceMode = initialMode
                                                )
                                                if (hasTimecodes && initialMode == PracticeMode.RANDOM_LINE) {
                                                        pickRandomLine()
                                                }
                                                updateReadyState()
                                        }
                                }
                        } catch (e: Exception) {
                                _uiState.value = ListeningPracticeUiState.Error(e.message ?: "Неизвестная ошибка")
                        }
                }
        }

        private fun observePracticeState() {
                screenModelScope.launch {
                        practiceState.collect { state ->
                                val currentState = _uiState.value
                                if (currentState is ListeningPracticeUiState.Ready) {
                                        _uiState.value = currentState.copy(
                                                lines = state.lines,
                                                currentLineIndex = state.currentLineIndex,
                                                userInput = state.userInput,
                                                correctCount = state.correctCount,
                                                incorrectCount = state.incorrectCount,
                                                practiceMode = state.practiceMode,
                                                currentLineIsPlaying = state.currentLineIsPlaying
                                        )
                                }
                        }
                }
        }

        private fun observePlayerState() {
                screenModelScope.launch {
                        combine(
                                player.isPlaying,
                                player.currentPositionMs,
                                player.durationMs,
                                player.isReady
                        ) { isPlaying, position, duration, isReady ->
                                PlayerState(isPlaying, position, duration, isReady)
                        }.collect { playerState ->
                                val currentState = _uiState.value
                                if (currentState is ListeningPracticeUiState.Ready && playerState.isReady) {
                                        _uiState.value = currentState.copy(
                                                isPlaying = playerState.isPlaying,
                                                currentPositionMs = playerState.position,
                                                durationMs = playerState.duration
                                        )
                                } else if (currentState is ListeningPracticeUiState.Loading && playerState.isReady && practiceState.value.lines.isNotEmpty()) {
                                        updateReadyState()
                                }
                        }
                }
        }

        private fun updateReadyState() {
                val state = practiceState.value
                val hasTimecodes = state.lines.any { it.hasTimecode }
                _uiState.value = ListeningPracticeUiState.Ready(
                        track = track,
                        lines = state.lines,
                        currentLineIndex = state.currentLineIndex,
                        isPlaying = player.isPlaying.value,
                        currentPositionMs = player.currentPositionMs.value,
                        durationMs = player.durationMs.value,
                        userInput = state.userInput,
                        correctCount = state.correctCount,
                        incorrectCount = state.incorrectCount,
                        practiceMode = state.practiceMode,
                        hasTimecodes = hasTimecodes,
                        currentLineIsPlaying = state.currentLineIsPlaying
                )
        }

        fun onSwitchMode(mode: PracticeMode) {
                linePlaybackJob?.cancel()
                player.pause()
                practiceState.update { it.copy(practiceMode = mode, currentLineIsPlaying = false, userInput = "") }
                if (mode == PracticeMode.RANDOM_LINE) {
                        pickRandomLine()
                }
                updateReadyState()
        }

        private fun pickRandomLine() {
                val lines = practiceState.value.lines
                val linesWithTimecodes = lines.indices.filter { lines[it].hasTimecode }
                if (linesWithTimecodes.isEmpty()) return
                val randomIndex = linesWithTimecodes.random()
                practiceState.update {
                        it.copy(
                                currentLineIndex = randomIndex,
                                userInput = "",
                                currentLineIsPlaying = false
                        )
                }
        }

        fun onPlayCurrentLineClick() {
                val state = practiceState.value
                val line = state.lines.getOrNull(state.currentLineIndex) ?: return
                val startMs = line.startMs ?: return
                val endMs = line.endMs ?: return

                linePlaybackJob?.cancel()
                val playStartMs = (startMs - 50).coerceAtLeast(0)
                val playEndMs = endMs + 50
                val duration = playEndMs - playStartMs

                player.seekTo(playStartMs)
                player.play()
                practiceState.update { it.copy(currentLineIsPlaying = true) }

                linePlaybackJob = screenModelScope.launch {
                        delay(duration)
                        player.pause()
                        practiceState.update { it.copy(currentLineIsPlaying = false) }
                }
        }

        fun onPlayPauseClick() {
                if (player.isPlaying.value) {
                        player.pause()
                } else {
                        player.play()
                }
        }

        fun onMoveBackClick() = player.rewind(5000)


        fun onMoveForwardClick() {
                val newPosition = (player.currentPositionMs.value + 5000).coerceAtMost(player.durationMs.value)
                player.seekTo(newPosition)
        }

        fun onUserInputChange(input: String) = practiceState.update { it.copy(userInput = input) }

        private fun advanceLine(updatedLine: LyricLine, isCorrect: Boolean) {
                val state = practiceState.value
                val updatedLines = state.lines.toMutableList().apply {
                        set(state.currentLineIndex, updatedLine)
                }
                val newCorrect = if (isCorrect) state.correctCount + 1 else state.correctCount
                val newIncorrect = if (!isCorrect) state.incorrectCount + 1 else state.incorrectCount

                if (state.practiceMode == PracticeMode.RANDOM_LINE) {
                        practiceState.update {
                                it.copy(
                                        lines = updatedLines,
                                        userInput = "",
                                        correctCount = newCorrect,
                                        incorrectCount = newIncorrect,
                                        currentLineIsPlaying = false
                                )
                        }
                        pickRandomLine()
                        updateReadyState()
                        return
                }

                val nextIndex = state.currentLineIndex + 1
                practiceState.update {
                        it.copy(
                                lines = updatedLines,
                                currentLineIndex = nextIndex,
                                userInput = "",
                                correctCount = newCorrect,
                                incorrectCount = newIncorrect
                        )
                }

                if (nextIndex >= updatedLines.size) {
                        player.pause()
                        _uiState.value = ListeningPracticeUiState.Completed(
                                track = track,
                                lines = updatedLines,
                                correctCount = newCorrect,
                                incorrectCount = newIncorrect
                        )
                } else {
                        updateReadyState()
                }
        }

        fun onCheckLine() {
                val state = practiceState.value
                if (state.currentLineIndex >= state.lines.size) return

                val currentLine = state.lines[state.currentLineIndex]
                val isCorrect = checkLineMatch(state.userInput, currentLine.text)

                val updatedLine = currentLine.copy(
                        userInput = state.userInput,
                        checkResult = if (isCorrect) LineCheckResult.CORRECT else LineCheckResult.INCORRECT
                )
                advanceLine(updatedLine, isCorrect)
        }

        private fun checkLineMatch(userInput: String, expectedText: String): Boolean {
                return userInput.trim().equals(expectedText.trim(), ignoreCase = true)
        }

        fun onSkipLine() {
                val state = practiceState.value
                if (state.currentLineIndex >= state.lines.size) return
                val currentLine = state.lines[state.currentLineIndex]
                val updatedLine = currentLine.copy(
                        userInput = "",
                        checkResult = LineCheckResult.INCORRECT
                )
                advanceLine(updatedLine, false)
        }

        fun onRestart() {
                linePlaybackJob?.cancel()
                player.pause()
                practiceState.update {
                        it.copy(
                                lines = it.lines.map { line -> line.copy(userInput = "", checkResult = LineCheckResult.PENDING) },
                                currentLineIndex = 0,
                                userInput = "",
                                correctCount = 0,
                                incorrectCount = 0,
                                currentLineIsPlaying = false
                        )
                }
                if (practiceState.value.practiceMode == PracticeMode.RANDOM_LINE) pickRandomLine()
                else player.seekTo(0)

                updateReadyState()
        }

        override fun onDispose() {
                linePlaybackJob?.cancel()
                player.release()
        }

        private data class PlayerState(
                val isPlaying: Boolean,
                val position: Long,
                val duration: Long,
                val isReady: Boolean
        )
}
