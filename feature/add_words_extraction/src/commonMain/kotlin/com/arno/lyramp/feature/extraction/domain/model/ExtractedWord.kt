package com.arno.lyramp.feature.extraction.domain.model

import com.arno.lyramp.core.model.CefrLevel

internal data class ExtractedWord(
        val word: String,
        val cefrLevel: CefrLevel,
        val lyricLine: String,
        val trackName: String,
        val artists: List<String>,
        val language: String
)
