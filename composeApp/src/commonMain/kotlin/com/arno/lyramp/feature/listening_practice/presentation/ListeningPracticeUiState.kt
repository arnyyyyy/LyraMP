package com.arno.lyramp.feature.listening_practice.presentation

import com.arno.lyramp.feature.listening_practice.model.LyricLine
import com.arno.lyramp.feature.listening_practice.model.PracticeMode
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack

internal sealed interface ListeningPracticeUiState {
        data object Loading : ListeningPracticeUiState

        data class Ready(
                val track: PracticeTrack,
                val lines: List<LyricLine>,
                val currentLineIndex: Int,
                val isPlaying: Boolean,
                val currentPositionMs: Long,
                val durationMs: Long,
                val userInput: String,
                val correctCount: Int,
                val incorrectCount: Int,
                val practiceMode: PracticeMode,
                val hasTimecodes: Boolean,
                val currentLineIsPlaying: Boolean = false
        ) : ListeningPracticeUiState

        data class Completed(
                val track: PracticeTrack,
                val lines: List<LyricLine>,
                val correctCount: Int,
                val incorrectCount: Int
        ) : ListeningPracticeUiState

        data class Error(val message: String) : ListeningPracticeUiState
}