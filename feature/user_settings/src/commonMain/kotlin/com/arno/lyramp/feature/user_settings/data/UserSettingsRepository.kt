package com.arno.lyramp.feature.user_settings.data

import com.arno.lyramp.feature.user_settings.model.RecommendedWordLevel

class UserSettingsRepository {

        fun getSelectedLanguage(): String? = UserSettingsStorage.selectedLanguage

        fun saveSelectedLanguage(language: String?) {
                UserSettingsStorage.selectedLanguage = language
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
