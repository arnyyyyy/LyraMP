package com.arno.lyramp.feature.user_settings.data

import com.arno.lyramp.feature.user_settings.model.RecommendedWordLevel
import com.russhwolf.settings.Settings

internal object UserSettingsStorage {
        private val settings = Settings()

        var selectedLanguage: String?
                get() = settings.getStringOrNull(SELECTED_LANGUAGE_KEY)
                set(value) {
                        if (value == null) settings.remove(SELECTED_LANGUAGE_KEY)
                        else settings.putString(SELECTED_LANGUAGE_KEY, value)
                }

        var learningLanguages: Set<String>
                get() = settings.getStringOrNull(LEARNING_LANGUAGES_KEY)
                        ?.split(",")
                        ?.filter { it.isNotBlank() }
                        ?.toSet()
                        ?: emptySet()
                set(value) {
                        if (value.isEmpty()) settings.remove(LEARNING_LANGUAGES_KEY)
                        else settings.putString(LEARNING_LANGUAGES_KEY, value.joinToString(","))
                }

        fun getWordLevelForLanguage(language: String): RecommendedWordLevel {
                val name = settings.getStringOrNull(levelKey(language)) ?: return RecommendedWordLevel.ALL
                return runCatching { RecommendedWordLevel.valueOf(name) }.getOrDefault(RecommendedWordLevel.ALL)
        }

        fun setWordLevelForLanguage(language: String, level: RecommendedWordLevel) {
                settings.putString(levelKey(language), level.name)
        }

        private fun levelKey(language: String) = "${RECOMMENDED_LEVEL_PREFIX}_$language"

        private const val SELECTED_LANGUAGE_KEY = "lyra_selected_language"
        private const val LEARNING_LANGUAGES_KEY = "lyra_learning_languages"
        private const val RECOMMENDED_LEVEL_PREFIX = "lyra_word_level"
}
