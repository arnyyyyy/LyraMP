package com.arno.lyramp.feature.stats.data

import androidx.room.Entity

@Entity(tableName = "track_word_cefr", primaryKeys = ["trackId", "word"])
internal data class TrackCefrWordEntity(
        val trackId: String,
        val word: String,
        val cefrLevel: String,
        val language: String,
)
