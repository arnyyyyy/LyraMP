package com.arno.lyramp.feature.listening_history.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_tracks")
data class MusicTrackEntity(
        @PrimaryKey(autoGenerate = true) val localId: Long = 0,
        val trackId: String?,
        val albumId: String?,
        val name: String,
        val artists: String,
        val albumName: String?,
        val imageUrl: String?
)
