package com.arno.lyramp.feature.extraction.presentation

import com.arno.lyramp.feature.extraction.domain.model.ExtractionResult

internal sealed interface ExtractionUiState {
        data object Idle : ExtractionUiState

        data class Running(
                val progress: Float = 0f,
                val currentTrack: String = ""
        ) : ExtractionUiState

        data class WordSelection(
                val result: ExtractionResult,
                val selectedWords: Set<String> = emptySet()
        ) : ExtractionUiState

        data object Saving : ExtractionUiState

        data class Done(val savedCount: Int) : ExtractionUiState
        data class Error(val message: String) : ExtractionUiState
}

