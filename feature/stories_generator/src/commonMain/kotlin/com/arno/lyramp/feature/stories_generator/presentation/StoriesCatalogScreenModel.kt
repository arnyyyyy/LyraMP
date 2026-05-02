package com.arno.lyramp.feature.stories_generator.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllLearnWordsUseCase
import com.arno.lyramp.feature.stories_generator.data.GeneratedStoryRepository
import com.arno.lyramp.feature.stories_generator.domain.ModelDownloadService
import com.arno.lyramp.feature.stories_generator.domain.StoryGenerationState
import com.arno.lyramp.feature.stories_generator.domain.StoryGenerationService
import com.arno.lyramp.feature.stories_generator.model.DownloadableModel
import com.arno.lyramp.feature.stories_generator.model.ModelDownloadState
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.ObserveSelectedLanguageUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class StoriesCatalogScreenModel(
        private val repository: GeneratedStoryRepository,
        private val generationService: StoryGenerationService,
        private val downloadCoordinator: ModelDownloadService,
        getAllLearnWords: GetAllLearnWordsUseCase,
        getSelectedLanguage: GetSelectedLanguageUseCase,
        observeSelectedLanguage: ObserveSelectedLanguageUseCase,
) : ScreenModel {

        private val selectedLanguage: StateFlow<String> = observeSelectedLanguage().map {
                it ?: DEFAULT_LANGUAGE
        }.distinctUntilChanged().stateIn(
                screenModelScope,
                SharingStarted.Eagerly,
                getSelectedLanguage() ?: DEFAULT_LANGUAGE
        )

        private val visibleStories = selectedLanguage.flatMapLatest { language ->
                repository.observeByLanguage(language)
        }

        val modelState: StateFlow<ModelDownloadState> = downloadCoordinator.state
        val activeModel: StateFlow<DownloadableModel?> = downloadCoordinator.activeModel

        val uiState: StateFlow<CatalogUiState> = combine(
                visibleStories,
                getAllLearnWords(),
                generationService.generationState,
                downloadCoordinator.state,
                selectedLanguage,
        ) { stories, learnWords, generationState, modelState, selectedLanguage ->
                val modelMissing = modelState is ModelDownloadState.NotDownloaded ||
                    modelState is ModelDownloadState.Downloading ||
                    modelState is ModelDownloadState.Paused ||
                    modelState is ModelDownloadState.Error
                val isGenerating = generationState == StoryGenerationState.Background
                val availableWords = learnWords.filter { it.sourceLang == selectedLanguage || it.sourceLang == null }
                when {
                        stories.isEmpty() && modelMissing -> CatalogUiState.NoModel

                        stories.isEmpty() && availableWords.size < StoryGenerationService.MIN_WORDS ->
                                CatalogUiState.NotEnoughWords(
                                        current = availableWords.size,
                                        needed = StoryGenerationService.MIN_WORDS
                                )

                        stories.isEmpty() -> CatalogUiState.Empty(isGenerating = isGenerating)

                        else -> CatalogUiState.Items(stories = stories, isGenerating = isGenerating)
                }
        }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5_000), CatalogUiState.Loading)

        init {
                screenModelScope.launch {
                        selectedLanguage.collect { triggerBackfillIfNeeded() }
                }
        }

        fun triggerBackfillIfNeeded() {
                screenModelScope.launch {
                        val language = selectedLanguage.value
                        val unread = repository.countUnreadByLanguage(language)
                        if (unread >= StoryGenerationService.MIN_UNREAD_TARGET) return@launch

                        generationService.generateBackgroundUntilTarget(language = language)
                }
        }

        fun onStoryOpened(storyId: Long) = screenModelScope.launch {
                repository.markAsRead(storyId)
                triggerBackfillIfNeeded()
        }

        fun deleteStory(storyId: Long) = screenModelScope.launch {
                repository.deleteStory(storyId)
        }

        fun downloadModel(model: DownloadableModel) = downloadCoordinator.startDownload(model)
        fun pauseDownload() = downloadCoordinator.pauseDownload()
        fun resumeDownload() = downloadCoordinator.resumeDownload()
        fun deleteModel() = downloadCoordinator.deleteAll()

        private companion object {
                const val DEFAULT_LANGUAGE = "en"
        }
}
