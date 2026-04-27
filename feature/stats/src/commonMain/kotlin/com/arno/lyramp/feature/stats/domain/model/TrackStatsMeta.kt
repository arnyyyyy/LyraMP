package com.arno.lyramp.feature.stats.domain.model

data class TrackStatsMeta(
        val trackId: String,
        val trackName: String,
        val artists: String,
        val language: String,
        val totalWordsInLyrics: Int,
        val uniqueCefrWordsCount: Int,
        val processedAt: Long,
)
