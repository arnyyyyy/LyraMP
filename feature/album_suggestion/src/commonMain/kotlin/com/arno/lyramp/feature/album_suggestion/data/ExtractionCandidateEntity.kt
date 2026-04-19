package com.arno.lyramp.feature.album_suggestion.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "extraction_candidates", indices = [Index(value = ["albumId", "trackIndex", "word"], unique = true)])
data class ExtractionCandidateEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val word: String,
        val sourceLang: String?,
        val lyricLine: String,
        val trackName: String,
        val artists: String,
        val albumId: String,
        val trackIndex: Int,
        val cefrLevel: String? = null
)
