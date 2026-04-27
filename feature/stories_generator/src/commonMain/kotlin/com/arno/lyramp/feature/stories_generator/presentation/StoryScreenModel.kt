package com.arno.lyramp.feature.stories_generator.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllLearnWordsUseCase
import com.arno.lyramp.feature.stories_generator.domain.LlamatikStoryGenerator
import com.arno.lyramp.feature.stories_generator.domain.ModelDownloadRepository
import com.arno.lyramp.feature.stories_generator.data.GeneratedStoryRepository
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
        private val modelDownloadRepository: ModelDownloadRepository,
        private val getSelectedLanguageUseCase: GetSelectedLanguageUseCase,
        private val repository: GeneratedStoryRepository,
        private val generator: LlamatikStoryGenerator,
) : ScreenModel {

        private val _uiState = MutableStateFlow<StoryUiState>(StoryUiState.Idle)
        val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

        private val _modelState = MutableStateFlow<ModelDownloadState>(ModelDownloadState.NotDownloaded)
        val modelState: StateFlow<ModelDownloadState> = _modelState.asStateFlow()

        private val _activeModel = MutableStateFlow<DownloadableModel?>(null)
        val activeModel: StateFlow<DownloadableModel?> = _activeModel.asStateFlow()

        private var modelInitStarted = false

        private var allWords: List<LearnWordEntity> = emptyList()
        private var currentLanguage: String? = null

        init {
                currentLanguage = getSelectedLanguageUseCase()
                loadWords()
                checkModelState()
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

        private fun checkModelState() {
                val downloaded = modelDownloadRepository.findDownloadedModel()
                if (downloaded != null) {
                        _activeModel.value = downloaded
                        _modelState.value = ModelDownloadState.Downloaded
                        tryInitModel(downloaded)
                } else {
                        _modelState.value = ModelDownloadState.NotDownloaded
                }
        }

        private fun tryInitModel(model: DownloadableModel) {
                if (modelInitStarted) return
                modelInitStarted = true
                _modelState.value = ModelDownloadState.Checking

                screenModelScope.launch {
                        try {
                                val modelPath = modelDownloadRepository.getModelFilePath(model)
                                val loaded = generator.loadModelFromPath(modelPath)
                                if (loaded) {
                                        _activeModel.value = model
                                        _modelState.value = ModelDownloadState.Downloaded
                                } else {
                                        modelInitStarted = false
                                        _modelState.value = ModelDownloadState.Error(
                                                "Не удалось загрузить модель. " +
                                                    "Возможно, не хватает памяти — попробуйте модель поменьше."
                                        )
                                }
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (e: Exception) {
                                modelInitStarted = false
                                _modelState.value = ModelDownloadState.Error(
                                        "AI-движок не доступен: ${e.message ?: "неизвестная ошибка"}"
                                )
                        }
                }
        }

        fun downloadModel(model: DownloadableModel) {
                screenModelScope.launch {
                        val current = _activeModel.value
                        if (current != null && current != model) {
                                generator.release()
                                modelInitStarted = false
                                modelDownloadRepository.deleteModel(current)
                        }

                        _activeModel.value = model
                        modelDownloadRepository.downloadModel(model).collect { state ->
                                _modelState.value = state
                                if (state is ModelDownloadState.Downloaded) {
                                        tryInitModel(model)
                                }
                        }
                }
        }

        fun deleteModel() {
                screenModelScope.launch {
                        generator.release()
                        modelInitStarted = false
                        modelDownloadRepository.deleteAllModels()
                        _activeModel.value = null
                        _modelState.value = ModelDownloadState.NotDownloaded
                }
        }

        private fun rebuildReadyState() {
                currentLanguage = getSelectedLanguageUseCase()

                val filtered = if (currentLanguage != null) {
                        allWords.filter { it.sourceLang == currentLanguage }
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
                                val story = generator.generateStory(
                                        selectedWords,
                                        language = currentLanguage ?: "en",
                                        genre = genre
                                )
                                val newId = repository.save(story, isManual = true)
                                val saved = if (newId > 0L) story.copy(id = newId) else story
                                _uiState.value = StoryUiState.StoryGenerated(saved)
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
