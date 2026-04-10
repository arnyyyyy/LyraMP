package com.arno.lyramp.feature.user_settings.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import com.arno.lyramp.feature.user_settings.data.UserSettingsRepository
import com.arno.lyramp.feature.user_settings.model.RecommendedWordLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserSettingsScreenModel(
        private val repository: UserSettingsRepository,
) : ScreenModel {

        private val _state = MutableStateFlow(UserSettingsState())
        val state: StateFlow<UserSettingsState> = _state.asStateFlow()

        init {
                _state.value = UserSettingsState(
                        selectedLanguages = repository.getLearningLanguages(),
                        wordLevels = AVAILABLE_LANGUAGES.associateWith { repository.getWordLevelForLanguage(it) },
                )
        }

        fun toggleLanguage(language: String) {
                _state.update { current ->
                        val updated = current.selectedLanguages.toMutableSet()
                        if (language in updated) updated.remove(language) else updated.add(language)
                        current.copy(selectedLanguages = updated)
                }
        }

        fun selectLevel(language: String, level: RecommendedWordLevel) {
                _state.update { current -> current.copy(wordLevels = current.wordLevels + (language to level)) }
        }

        fun saveAndClose() {
                val currentState = _state.value
                repository.saveLearningLanguages(currentState.selectedLanguages)
                currentState.selectedLanguages.forEach { lang ->
                        currentState.wordLevels[lang]?.let {
                                repository.saveWordLevelForLanguage(lang, it)
                        }
                }
        }

        companion object {
                val AVAILABLE_LANGUAGES = listOf("en", "fr", "de", "es", "it", "hu", "ja", "zh", "he", "ar")
        }
}
