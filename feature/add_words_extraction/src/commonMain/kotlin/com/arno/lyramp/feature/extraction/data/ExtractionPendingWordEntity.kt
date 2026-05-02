package com.arno.lyramp.feature.extraction.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "extraction_pending_words")
internal data class ExtractionPendingWordEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val word: String,
        val cefrLevel: String,
        val lyricLine: String,
        val trackName: String,
        val artists: String,
        val language: String,
)
