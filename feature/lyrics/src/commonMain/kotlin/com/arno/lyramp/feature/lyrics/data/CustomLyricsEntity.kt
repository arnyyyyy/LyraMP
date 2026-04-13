package com.arno.lyramp.feature.lyrics.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_lyrics")
data class CustomLyricsEntity(
        @PrimaryKey val id: String,
        val lyrics: String
)
