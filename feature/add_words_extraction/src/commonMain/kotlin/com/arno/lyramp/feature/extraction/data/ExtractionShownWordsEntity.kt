package com.arno.lyramp.feature.extraction.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "extraction_shown_words")
internal data class ExtractionShownWordsEntity(
        @PrimaryKey val word: String
)
