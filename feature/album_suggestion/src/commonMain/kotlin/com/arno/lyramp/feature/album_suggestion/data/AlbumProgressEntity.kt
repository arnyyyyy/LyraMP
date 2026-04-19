package com.arno.lyramp.feature.album_suggestion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album_progress")
internal data class AlbumProgressEntity(
        @PrimaryKey val albumId: String,
        val albumTitle: String,
        val artistName: String,
        val coverUri: String? = null,
        val totalTracks: Int,
        val wordsExtracted: Boolean = false,
        val extractedLanguage: String? = null,
        val extractedLevels: String? = null
)
