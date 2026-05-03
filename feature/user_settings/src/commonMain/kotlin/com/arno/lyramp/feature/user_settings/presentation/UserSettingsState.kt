package com.arno.lyramp.feature.user_settings.presentation

import com.arno.lyramp.feature.user_settings.model.RecommendedWordLevel

data class UserSettingsState(
        val isLoading: Boolean = true,
        val selectedLanguages: Set<String> = emptySet(),
        val wordLevels: Map<String, RecommendedWordLevel> = emptyMap(),
)
