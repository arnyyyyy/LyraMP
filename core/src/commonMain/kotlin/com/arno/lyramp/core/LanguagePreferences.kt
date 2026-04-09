package com.arno.lyramp.core

import com.russhwolf.settings.Settings

internal object LanguagePreferences {
        private val settings = Settings()

        var languagePreferences: String?
                get() = settings.getStringOrNull(LANGUAGE_PREF_KEY)
                set(value) {
                        if (value == null) settings.remove(LANGUAGE_PREF_KEY)
                        else settings.putString(LANGUAGE_PREF_KEY, value)
                }

        private const val LANGUAGE_PREF_KEY = "language_preferences_key"
}

class LanguagePreferencesRepository {
        fun getSavedLanguage(): String? = LanguagePreferences.languagePreferences
        fun saveLanguage(language: String?) {
                LanguagePreferences.languagePreferences = language
        }
}