package com.arno.lyramp.feature.extraction.presentation

import com.arno.lyramp.feature.extraction.domain.model.ExtractionResult

internal sealed interface ExtractionUiState {
        data object Idle : ExtractionUiState
        data object Running : ExtractionUiState

        data class WordSelection(
                val result: ExtractionResult,
                val selectedWords: Set<String> = emptySet()
        ) : ExtractionUiState

        data object Saving : ExtractionUiState

        data class Done(val savedCount: Int) : ExtractionUiState
        data class Error(val message: String) : ExtractionUiState
}

