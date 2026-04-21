package com.arno.lyramp.feature.stories_generator.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllLearnWordsUseCase
import com.arno.lyramp.feature.stories_generator.data.GeneratedStoryRepository
import com.arno.lyramp.feature.stories_generator.domain.StoryGenerationService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class StoriesCatalogScreenModel(
        private val repository: GeneratedStoryRepository,
        private val generationService: StoryGenerationService,
        getAllLearnWords: GetAllLearnWordsUseCase
) : ScreenModel {

        private val _isGenerating = MutableStateFlow(false)

        val uiState: StateFlow<CatalogUiState> = combine(
                repository.observeAll(),
                getAllLearnWords(),
                _isGenerating
        ) { stories, learnWords, isGenerating ->
                when {
                        stories.isEmpty() && learnWords.size < StoryGenerationService.MIN_WORDS ->
                                CatalogUiState.NotEnoughWords(
                                        current = learnWords.size,
                                        needed = StoryGenerationService.MIN_WORDS
                                )

                        stories.isEmpty() -> CatalogUiState.Empty(isGenerating = isGenerating)

                        else -> CatalogUiState.Items(stories = stories, isGenerating = isGenerating)
                }
        }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5_000), CatalogUiState.Loading)

        init {
                triggerBackfillIfNeeded()
        }

        fun triggerBackfillIfNeeded() {
                screenModelScope.launch {
                        val unread = repository.countUnread()
                        if (unread >= StoryGenerationService.MIN_UNREAD_TARGET) return@launch

                        _isGenerating.value = true
                        try {
                                generationService.generateAndSaveOne()
                        } finally {
                                _isGenerating.value = false
                        }
                }
        }

        fun onStoryOpened(storyId: Long) {
                screenModelScope.launch {
                        repository.markAsRead(storyId)
                        triggerBackfillIfNeeded()
                }
        }
}

