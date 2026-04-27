package com.arno.lyramp.feature.stats.domain.model

import com.arno.lyramp.core.model.CefrDifficultyGroup

internal data class LanguageStatsSnapshot(
        val language: String,
        val learningWordsCount: Int,
        val learnedWordsCount: Int,
        val libraryTracksCount: Int,
        val processedTracksCount: Int,
        val fullyLearnedTracksCount: Int,
        val groupStats: List<CefrGroupStats>,
)

internal data class CefrGroupStats(
        val group: CefrDifficultyGroup,
        val known: Int,
        val total: Int,
) {
        val ratio: Float
                get() = if (total == 0) 0f else known.toFloat() / total
}
