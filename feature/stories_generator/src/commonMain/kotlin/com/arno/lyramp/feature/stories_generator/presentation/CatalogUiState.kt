package com.arno.lyramp.feature.stories_generator.presentation

import com.arno.lyramp.feature.stories_generator.model.GeneratedStory

internal sealed interface CatalogUiState {
        data object Loading : CatalogUiState
        data class Empty(val isGenerating: Boolean) : CatalogUiState
        data class NotEnoughWords(val current: Int, val needed: Int) : CatalogUiState
        data class Items(
                val stories: List<GeneratedStory>,
                val isGenerating: Boolean
        ) : CatalogUiState
}
