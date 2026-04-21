package com.arno.lyramp.feature.stories_generator.presentation

import com.arno.lyramp.feature.stories_generator.model.GeneratedStory

internal sealed interface StoryDetailUiState {
        data object Loading : StoryDetailUiState
        data class Loaded(val story: GeneratedStory) : StoryDetailUiState
        data object NotFound : StoryDetailUiState
}
