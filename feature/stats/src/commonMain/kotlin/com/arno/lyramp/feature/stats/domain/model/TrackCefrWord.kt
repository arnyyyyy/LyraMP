package com.arno.lyramp.feature.stats.domain.model

internal data class TrackCefrWord(
        val trackId: String,
        val word: String,
        val cefrLevel: String,
        val language: String,
)
