package com.arno.lyramp.feature.stats.data

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "track_word_cefr", primaryKeys = ["trackId", "word"], indices = [Index(value = ["language"])])
internal data class StatsTrackCefrWordEntity(
        val trackId: String,
        val word: String,
        val cefrLevel: String,
        val language: String,
)
