package com.arno.lyramp.feature.listening_practice.presentation

import com.arno.lyramp.feature.listening_practice.model.LyricLine
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack

internal sealed interface AuditionUiState {
        data object Loading : AuditionUiState

        data class Ready(
                val track: PracticeTrack,
                val currentLine: LyricLine,
                val userInput: String,
                val correctCount: Int,
                val incorrectCount: Int,
                val roundIndex: Int,
                val roundSize: Int,
                val currentLineIsPlaying: Boolean = false,
                val isSlowMode: Boolean = false,
                val lastAnsweredLine: LyricLine? = null,
                val isPlayerReady: Boolean = false,
        ) : AuditionUiState

        data class Completed(
                val answeredLines: List<LyricLine>,
                val correctCount: Int,
                val incorrectCount: Int,
        ) : AuditionUiState

        data object Empty : AuditionUiState

        data class Error(val message: String) : AuditionUiState
}
