package com.arno.lyramp.feature.user_settings.data

import com.arno.lyramp.feature.user_settings.model.RecommendedWordLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSettingsRepository {

        private val _selectedLanguage = MutableStateFlow(UserSettingsStorage.selectedLanguage)
        val selectedLanguageFlow: StateFlow<String?> = _selectedLanguage.asStateFlow()

        fun getSelectedLanguage(): String? = _selectedLanguage.value

        fun saveSelectedLanguage(language: String?) {
                UserSettingsStorage.selectedLanguage = language
                _selectedLanguage.value = language
        }

        fun getLearningLanguages(): Set<String> = UserSettingsStorage.learningLanguages

        fun saveLearningLanguages(languages: Set<String>) {
                UserSettingsStorage.learningLanguages = languages
        }

        fun getWordLevelForLanguage(language: String): RecommendedWordLevel =
                UserSettingsStorage.getWordLevelForLanguage(language)

        fun saveWordLevelForLanguage(language: String, level: RecommendedWordLevel) {
                UserSettingsStorage.setWordLevelForLanguage(language, level)
        }
}
