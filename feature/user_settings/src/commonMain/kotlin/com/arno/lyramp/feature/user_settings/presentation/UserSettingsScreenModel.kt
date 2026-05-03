package com.arno.lyramp.feature.user_settings.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.core.model.LyraLang
import com.arno.lyramp.feature.user_settings.data.UserSettingsRepository
import com.arno.lyramp.feature.user_settings.model.RecommendedWordLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserSettingsScreenModel internal constructor(
        private val repository: UserSettingsRepository,
) : ScreenModel {
        private val _state = MutableStateFlow(UserSettingsState())
        val state: StateFlow<UserSettingsState> = _state.asStateFlow()

        init {
                screenModelScope.launch {
                        val languages = repository.getLearningLanguages()
                        val levels = LyraLang.SUPPORTED.associateWith {
                                repository.getWordLevelForLanguage(it)
                        }

                        _state.value = UserSettingsState(
                                isLoading = false,
                                selectedLanguages = languages,
                                wordLevels = levels,
                        )
                }
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
                screenModelScope.launch {
                        repository.saveLearningLanguages(currentState.selectedLanguages)
                        currentState.selectedLanguages.forEach { lang ->
                                currentState.wordLevels[lang]?.let {
                                        repository.saveWordLevelForLanguage(lang, it)
                                }
                        }
                }
        }
}
