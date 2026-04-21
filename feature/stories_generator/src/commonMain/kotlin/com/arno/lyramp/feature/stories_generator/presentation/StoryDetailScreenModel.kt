package com.arno.lyramp.feature.stories_generator.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.stories_generator.data.GeneratedStoryRepository
import com.arno.lyramp.feature.stories_generator.presentation.StoryDetailUiState.Loaded
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class StoryDetailScreenModel(
        private val storyId: Long,
        private val repository: GeneratedStoryRepository
) : ScreenModel {

        private val _uiState = MutableStateFlow<StoryDetailUiState>(StoryDetailUiState.Loading)
        val uiState: StateFlow<StoryDetailUiState> = _uiState.asStateFlow()

        init {
                screenModelScope.launch {
                        val story = repository.getById(storyId)
                        _uiState.value = if (story != null) {
                                if (!story.isRead) repository.markAsRead(storyId)
                                Loaded(story)
                        } else {
                                StoryDetailUiState.NotFound
                        }
                }
        }

}
