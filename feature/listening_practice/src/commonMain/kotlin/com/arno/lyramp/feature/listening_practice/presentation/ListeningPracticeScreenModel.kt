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

        private enum class ContentStatus {
                Loading,
                Ready,
                Completed,
                Error,
        }

        private data class PracticeState(
                val status: ContentStatus = ContentStatus.Loading,
                val lines: List<LyricLine> = emptyList(),
                val currentLineIndex: Int = 0,
                val userInput: String = "",
                val correctCount: Int = 0,
                val incorrectCount: Int = 0,
                val practiceMode: PracticeMode = PracticeMode.FULL_SONG,
                val lastAnsweredLine: LyricLine? = null,
                val errorMessage: String? = null,
        )

        private val practiceState = MutableStateFlow(PracticeState())

        private var lineStartExpansionMs: Long = 0
        private var lineEndExpansionMs: Long = 0

        init {
                loadPractice()
                observeState()
        }

        private fun loadPractice() {
                screenModelScope.launch {
                        practiceState.value = PracticeState()
                        try {
                                when (val result = loadPracticeData(track)) {
                                        is PracticeDataResult.NoLyrics ->
                                                practiceState.update {
                                                        it.copy(
                                                                status = ContentStatus.Error,
                                                                errorMessage = "Не удалось получить текст песни",
                                                        )
                                                }

                                        is PracticeDataResult.NoStreaming ->
                                                practiceState.update {
                                                        it.copy(
                                                                status = ContentStatus.Error,
                                                                errorMessage = "Не удалось получить аудио трека",
                                                        )
                                                }

                                        is PracticeDataResult.Success -> {
                                                val lines = result.lyricLines
                                                playback.prepare(result.downloadInfo.url)
                                                val hasTimecodes = lines.any { it.hasTimecode }
                                                practiceState.value = PracticeState(
                                                        status = ContentStatus.Ready,
                                                        lines = lines,
                                                        practiceMode = PracticeMode.FULL_SONG,
                                                )
                                                if (hasTimecodes) autoPlayCurrentLineIfPossible()
                                        }
                                }
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (e: Exception) {
                                practiceState.update {
                                        it.copy(
                                                status = ContentStatus.Error,
                                                errorMessage = e.message ?: "Неизвестная ошибка",
                                        )
                                }
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
                                playback.currentLineIsPlaying,
                                playback.isSlowMode,
                        ) { values ->
                                Snapshot(
                                        track = track,
                                        practice = values[0] as PracticeState,
                                        isPlaying = values[1] as Boolean,
                                        positionMs = values[2] as Long,
                                        durationMs = values[3] as Long,
                                        currentLineIsPlaying = values[4] as Boolean,
                                        isSlowMode = values[5] as Boolean,
                                )
                        }.collect { snapshot ->
                                _uiState.value = snapshot.toUiState()
                        }
                }
        }

        fun onSwitchMode(mode: PracticeMode) {
                playback.stopSegment()
                playback.setSlowMode(false)
                practiceState.update { it.copy(practiceMode = mode, userInput = "", lastAnsweredLine = null) }
                if (mode == PracticeMode.RANDOM_LINE) pickRandomLine()
                if (mode == PracticeMode.FULL_SONG) autoPlayCurrentLineIfPossible()
        }

        fun onPlayCurrentLineClick() {
                val state = practiceState.value
                val line = state.lines.getOrNull(state.currentLineIndex) ?: return
                val startMs = line.startMs ?: return
                val endMs = line.endMs ?: return
                playback.toggleSegment(
                        screenModelScope,
                        (startMs - lineStartExpansionMs).coerceAtLeast(0),
                        endMs + lineEndExpansionMs,
                )
        }

        fun onExpandLineStart() {
                val state = practiceState.value
                val line = state.lines.getOrNull(state.currentLineIndex) ?: return
                val startMs = line.startMs ?: return
                val endMs = line.endMs ?: return
                lineStartExpansionMs += EXPAND_STEP_MS
                playback.playSegment(
                        screenModelScope,
                        (startMs - lineStartExpansionMs).coerceAtLeast(0),
                        endMs + lineEndExpansionMs,
                )
        }

        fun onExpandLineEnd() {
                val state = practiceState.value
                val line = state.lines.getOrNull(state.currentLineIndex) ?: return
                val startMs = line.startMs ?: return
                val endMs = line.endMs ?: return
                lineEndExpansionMs += EXPAND_STEP_MS
                playback.playSegment(
                        screenModelScope,
                        (startMs - lineStartExpansionMs).coerceAtLeast(0),
                        endMs + lineEndExpansionMs,
                )
        }

        fun onPlayPauseClick() {
                val state = practiceState.value
                val hasTimecodes = state.lines.any { it.hasTimecode }
                if (state.practiceMode == PracticeMode.FULL_SONG && hasTimecodes) {
                        if (playback.currentLineIsPlaying.value) playback.stopSegment()
                        else autoPlayCurrentLineIfPossible()
                        return
                }
                if (playback.currentLineIsPlaying.value) playback.stopSegment()
                else playback.playPause()
        }

        fun onToggleSlowMode() = playback.toggleSlowMode()
        fun onAppBackground() = playback.stopSegment()
        fun onMoveBackClick() {
                if (playback.currentLineIsPlaying.value) playback.stopSegment()
                playback.rewind(SEEK_STEP_MS)
        }

        fun onMoveForwardClick() {
                if (playback.currentLineIsPlaying.value) playback.stopSegment()
                playback.forward(SEEK_STEP_MS)
        }

        fun onUserInputChange(input: String) = practiceState.update { it.copy(userInput = input) }

        fun onCheckLine() {
                val state = practiceState.value
                if (state.lastAnsweredLine != null) return
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
                if (state.lastAnsweredLine != null) return
                if (state.currentLineIndex >= state.lines.size) return
                advanceLine(
                        state.lines[state.currentLineIndex].copy(userInput = "", checkResult = LineCheckResult.INCORRECT),
                        isCorrect = false,
                )
        }

        fun onNextLine() {
                val state = practiceState.value
                if (state.practiceMode != PracticeMode.RANDOM_LINE || state.lastAnsweredLine == null) return
                pickRandomLine(clearFeedback = true)
        }

        fun onRestart() {
                playback.stopSegment()
                playback.setSlowMode(false)
                practiceState.update {
                        it.copy(
                                status = ContentStatus.Ready,
                                lines = it.lines.map { line -> line.copy(userInput = "", checkResult = LineCheckResult.PENDING) },
                                currentLineIndex = 0, userInput = "",
                                correctCount = 0, incorrectCount = 0, lastAnsweredLine = null,
                        )
                }
                if (practiceState.value.practiceMode == PracticeMode.RANDOM_LINE) pickRandomLine()
                else playback.seekTo(0)
                autoPlayCurrentLineIfPossible()
        }

        private fun pickRandomLine(clearFeedback: Boolean = false) {
                lineStartExpansionMs = 0
                lineEndExpansionMs = 0
                val lines = practiceState.value.lines
                val withTimecodes = lines.indices.filter { lines[it].hasTimecode }
                if (withTimecodes.isEmpty()) return
                practiceState.update {
                        it.copy(
                                currentLineIndex = withTimecodes.random(),
                                userInput = "",
                                lastAnsweredLine = if (clearFeedback) null else it.lastAnsweredLine,
                        )
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
                        practiceState.update { it.copy(status = ContentStatus.Completed) }
                } else {
                        autoPlayCurrentLineIfPossible()
                }
        }

        private fun autoPlayCurrentLineIfPossible() {
                val state = practiceState.value
                if (state.practiceMode != PracticeMode.FULL_SONG) return
                val line = state.lines.getOrNull(state.currentLineIndex) ?: return
                val startMs = line.startMs ?: return
                val endMs = line.endMs ?: return
                playback.playSegment(screenModelScope, startMs, endMs)
        }

        override fun onDispose() = playback.release()

        private data class Snapshot(
                val track: PracticeTrack,
                val practice: PracticeState,
                val isPlaying: Boolean,
                val positionMs: Long,
                val durationMs: Long,
                val currentLineIsPlaying: Boolean,
                val isSlowMode: Boolean,
        ) {
                fun toUiState(): ListeningPracticeUiState {
                        return when (practice.status) {
                                ContentStatus.Loading -> ListeningPracticeUiState.Loading
                                ContentStatus.Error -> ListeningPracticeUiState.Error(
                                        practice.errorMessage ?: "Неизвестная ошибка"
                                )

                                ContentStatus.Completed -> ListeningPracticeUiState.Completed(
                                        track = track,
                                        lines = practice.lines,
                                        correctCount = practice.correctCount,
                                        incorrectCount = practice.incorrectCount,
                                )

                                ContentStatus.Ready -> ListeningPracticeUiState.Ready(
                                        track = track,
                                        lines = practice.lines,
                                        currentLineIndex = practice.currentLineIndex,
                                        isPlaying = isPlaying,
                                        currentPositionMs = positionMs,
                                        durationMs = durationMs,
                                        userInput = practice.userInput,
                                        correctCount = practice.correctCount,
                                        incorrectCount = practice.incorrectCount,
                                        practiceMode = practice.practiceMode,
                                        hasTimecodes = practice.lines.any { it.hasTimecode },
                                        currentLineIsPlaying = currentLineIsPlaying,
                                        isSlowMode = isSlowMode,
                                        lastAnsweredLine = practice.lastAnsweredLine,
                                )
                        }
                }
        }

        private companion object {
                const val SEEK_STEP_MS = 5_000L
                const val EXPAND_STEP_MS = 1_000L
        }
}
