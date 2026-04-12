package com.arno.lyramp.feature.stories_generator.presentation

import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.stories_generator.model.GeneratedStory

internal sealed interface StoryUiState {
        data object Idle : StoryUiState
        data object Generating : StoryUiState

        data class Ready(
                val words: List<LearnWordEntity>,
                val selectedWords: Set<Long> = emptySet()
        ) : StoryUiState

        data class StoryGenerated(
                val story: GeneratedStory,
                val streamedText: String = story.text
        ) : StoryUiState

        data class Error(val message: String) : StoryUiState
}
