package com.arno.lyramp.feature.extraction.domain.model

internal data class ExtractionResult(
        val processedTracks: Int,
        val totalWords: Int,
        val newWords: Int,
        val words: List<ExtractedWord> = emptyList()
)
