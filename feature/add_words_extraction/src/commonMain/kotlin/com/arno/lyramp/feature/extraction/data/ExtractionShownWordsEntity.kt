package com.arno.lyramp.feature.extraction.data

import androidx.room.Entity

internal const val LEGACY_GLOBAL_SHOWN_LANGUAGE = "__legacy_global__"

@Entity(
        tableName = "extraction_shown_words", primaryKeys = ["word", "language"],
)
internal data class ExtractionShownWordsEntity(
        val word: String,
        val language: String,
)
