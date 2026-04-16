package com.arno.lyramp.feature.user_settings.domain.model

import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.feature.user_settings.model.RecommendedWordLevel

data class LanguageSettings(
        val lang: String,
        val wordLevel: RecommendedWordLevel,
        val levelsKey: String = wordLevel.name,
        val cefrFilter: Set<CefrLevel> = wordLevel.levels,
        val levelLabel: String = wordLevel.label
)
