package com.arno.lyramp.feature.user_settings.data

import com.arno.lyramp.feature.user_settings.model.RecommendedWordLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

internal class UserSettingsRepository {
        private val _selectedLanguage = MutableStateFlow(UserSettingsStorage.selectedLanguage)
        val selectedLanguageFlow: StateFlow<String?> = _selectedLanguage.asStateFlow()

        fun getSelectedLanguage() = _selectedLanguage.value

        fun saveSelectedLanguage(language: String?) {
                UserSettingsStorage.selectedLanguage = language
                _selectedLanguage.value = language
        }

        suspend fun getLearningLanguages() = withContext(Dispatchers.IO) {
                UserSettingsStorage.learningLanguages
        }

        suspend fun saveLearningLanguages(languages: Set<String>) = withContext(Dispatchers.IO) {
                UserSettingsStorage.learningLanguages = languages
        }

        suspend fun getWordLevelForLanguage(language: String) = withContext(Dispatchers.IO) {
                UserSettingsStorage.getWordLevelForLanguage(language)
        }

        suspend fun saveWordLevelForLanguage(language: String, level: RecommendedWordLevel) = withContext(Dispatchers.IO) {
                UserSettingsStorage.setWordLevelForLanguage(language, level)
        }
}
