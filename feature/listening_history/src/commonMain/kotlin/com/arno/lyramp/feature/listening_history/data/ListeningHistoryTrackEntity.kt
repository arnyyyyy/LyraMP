package com.arno.lyramp.feature.listening_history.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listening_history_tracks")
internal data class ListeningHistoryTrackEntity(
        @PrimaryKey(autoGenerate = true) val localId: Long = 0,
        val trackId: String?,
        val albumId: String?,
        val language: String?,
        val name: String,
        val artists: String,
        val albumName: String?,
        val imageUrl: String?,
        @ColumnInfo(defaultValue = "1")
        val isShowing: Boolean = true
)
