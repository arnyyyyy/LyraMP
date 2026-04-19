package com.arno.lyramp.feature.learn_words.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.ClassifyWordsByCefrUseCase
import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLearningLanguagesUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.ObserveSelectedLanguageUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.SaveSelectedLanguageUseCase
import com.arno.lyramp.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ChooseModeScreenModel(
        private val repository: LearnWordsRepository,
        private val classifyWordsByCefr: ClassifyWordsByCefrUseCase,
        observeSelectedLanguage: ObserveSelectedLanguageUseCase,
        private val saveSelectedLanguage: SaveSelectedLanguageUseCase,
        private val getLearningLanguages: GetLearningLanguagesUseCase,
        getLastAuthorizedService: GetLastAuthorizedServiceUseCase,
) : ScreenModel {
        private val _allWords = MutableStateFlow<List<LearnWordEntity>>(emptyList())
        private val _uiState = MutableStateFlow<ChooseModeUiState>(ChooseModeUiState.Loading)
        val uiState: StateFlow<ChooseModeUiState> = _uiState.asStateFlow()

        val selectedLanguage: StateFlow<String?> = observeSelectedLanguage()

        private val _availableLanguages = MutableStateFlow<List<String>>(emptyList())
        val availableLanguages: StateFlow<List<String>> = _availableLanguages.asStateFlow()

        val showSuggestions: StateFlow<Boolean> = MutableStateFlow(getLastAuthorizedService() == "YANDEX")

        init {
                screenModelScope.launch {
                        repository.getAllWords().collect { words ->
                                _allWords.value = words
                                refreshLanguagesInternal()
                        }
                }
                screenModelScope.launch {
                        selectedLanguage.collect { updateModeSelectionIfNeeded() }
                }
        }

        fun refreshLanguages() {
                refreshLanguagesInternal()
        }

        fun selectLanguage(language: String) {
                saveSelectedLanguage(language)
        }

        private fun refreshLanguagesInternal() {
                val dbLanguages = _allWords.value.mapNotNull { it.sourceLang }.distinct().sorted()
                val learningLanguages = getLearningLanguages()
                val languages = if (learningLanguages.isNotEmpty()) {
                        learningLanguages.sorted().toList()
                } else {
                        dbLanguages
                }
                _availableLanguages.value = languages

                val current = selectedLanguage.value
                if (current == null || current !in languages) {
                        saveSelectedLanguage(languages.firstOrNull())
                }

                updateModeSelectionIfNeeded()
        }

        private fun updateModeSelectionIfNeeded() {
                val current = _uiState.value
                if (current is ChooseModeUiState.Loading || current is ChooseModeUiState.ModeSelection || current is ChooseModeUiState.Empty) {
                        val filtered = getFilteredWords()
                        if (filtered.isEmpty()) {
                                _uiState.value = ChooseModeUiState.Empty
                        } else {
                                _uiState.value = ChooseModeUiState.ModeSelection(filtered.map { it.toDomain() })
                                loadCefrGroupsIfEnglish(filtered)
                        }
                }
        }

        private fun loadCefrGroupsIfEnglish(filtered: List<LearnWordEntity>) {
                if (selectedLanguage.value != "en") return
                screenModelScope.launch {
                        try {
                                val groups = withContext(Dispatchers.IO) {
                                        classifyWordsByCefr(filtered.map { it.word }, "en")
                                }
                                val current = _uiState.value
                                if (current is ChooseModeUiState.ModeSelection) {
                                        _uiState.value = current.copy(cefrGroups = groups.mapValues { it.value.size })
                                }
                        } catch (e: Exception) {
                                Log.logger.e(e) { "CEFR loading failed" }
                        }
                }
        }

        private fun getFilteredWords(): List<LearnWordEntity> {
                val allWords = _allWords.value
                val lang = selectedLanguage.value ?: return allWords
                return allWords.filter { it.sourceLang == lang }
        }
}