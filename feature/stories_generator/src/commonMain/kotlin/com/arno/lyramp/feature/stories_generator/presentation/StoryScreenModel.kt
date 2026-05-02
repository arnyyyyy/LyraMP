package com.arno.lyramp.feature.stories_generator.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllLearnWordsUseCase
import com.arno.lyramp.feature.stories_generator.domain.ModelDownloadService
import com.arno.lyramp.feature.stories_generator.domain.StoryGenerationService
import com.arno.lyramp.feature.stories_generator.model.DownloadableModel
import com.arno.lyramp.feature.stories_generator.model.ModelDownloadState
import com.arno.lyramp.feature.stories_generator.model.StoryGenre
import com.arno.lyramp.feature.stories_generator.model.StoryWord
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class StoryScreenModel(
        private val getAllLearnWords: GetAllLearnWordsUseCase,
        private val downloadCoordinator: ModelDownloadService,
        private val getSelectedLanguageUseCase: GetSelectedLanguageUseCase,
        private val generationService: StoryGenerationService,
) : ScreenModel {

        private val _uiState = MutableStateFlow<StoryUiState>(StoryUiState.Idle)
        val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

        val modelState: StateFlow<ModelDownloadState> = downloadCoordinator.state
        val activeModel: StateFlow<DownloadableModel?> = downloadCoordinator.activeModel

        private var allWords: List<LearnWordEntity> = emptyList()
        private var currentLanguage: String? = null

        init {
                currentLanguage = getSelectedLanguageUseCase()
                loadWords()
                screenModelScope.launch {
                        generationService.cancelBackgroundGeneration()
                }
        }

        private fun loadWords() {
                screenModelScope.launch {
                        getAllLearnWords().collect { words ->
                                allWords = words
                                val current = _uiState.value
                                if (current is StoryUiState.Idle || current is StoryUiState.Ready) {
                                        rebuildReadyState()
                                }
                        }
                }
        }

        fun downloadModel(model: DownloadableModel) {
                this.downloadCoordinator.startDownload(model)
        }

        fun pauseDownload() {
                this.downloadCoordinator.pauseDownload()
        }

        fun resumeDownload() {
                this.downloadCoordinator.resumeDownload()
        }

        fun deleteModel() {
                this.downloadCoordinator.deleteAll()
        }

        private fun rebuildReadyState() {
                currentLanguage = getSelectedLanguageUseCase()

                val filtered = if (currentLanguage != null) {
                        allWords.filter { it.sourceLang == currentLanguage || it.sourceLang == null }
                } else {
                        allWords
                }

                _uiState.value = if (filtered.isEmpty()) StoryUiState.Idle
                else StoryUiState.Ready(words = filtered, selectedWords = filtered.map { it.id }.toSet())
        }

        fun toggleSelectWord(wordId: Long) {
                val state = _uiState.value
                if (state is StoryUiState.Ready) {
                        val newSelection = if (wordId in state.selectedWords) {
                                state.selectedWords - wordId
                        } else {
                                state.selectedWords + wordId
                        }
                        _uiState.value = state.copy(selectedWords = newSelection)
                }
        }

        fun toggleSelectAll() {
                val state = _uiState.value
                if (state is StoryUiState.Ready) {
                        if (state.selectedWords.isEmpty()) {
                                _uiState.value = state.copy(selectedWords = state.words.map { it.id }.toSet())
                        } else {
                                _uiState.value = state.copy(selectedWords = emptySet())
                        }
                }
        }

        fun generateStory() {
                val state = _uiState.value
                if (state !is StoryUiState.Ready) return

                if (modelState.value !is ModelDownloadState.Downloaded) {
                        _uiState.value = StoryUiState.Error(
                                "Модель ещё не готова. Дождитесь окончания загрузки."
                        )
                        return
                }

                val selectedWords = state.words
                        .filter { it.id in state.selectedWords }
                        .map { entity ->
                                StoryWord(
                                        word = entity.word,
                                        translation = entity.translation,
                                )
                        }

                if (selectedWords.isEmpty()) {
                        _uiState.value = StoryUiState.Error("Выберите хотя бы одно слово")
                        return
                }

                _uiState.value = StoryUiState.Generating

                screenModelScope.launch {
                        try {
                                val genre = StoryGenre.random()
                                val story = generationService.generateManualAndSave(
                                        words = selectedWords,
                                        language = currentLanguage ?: "en",
                                        genre = genre
                                )
                                if (story == null) {
                                        _uiState.value = StoryUiState.Error(
                                                "Не удалось сгенерировать историю. Проверьте, что модель готова."
                                        )
                                } else {
                                        _uiState.value = StoryUiState.StoryGenerated(story)
                                }
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (e: Exception) {
                                _uiState.value = StoryUiState.Error(
                                        e.message ?: "Ошибка генерации"
                                )
                        }
                }
        }

        fun backToWords() {
                rebuildReadyState()
        }

        override fun onDispose() {}
}
