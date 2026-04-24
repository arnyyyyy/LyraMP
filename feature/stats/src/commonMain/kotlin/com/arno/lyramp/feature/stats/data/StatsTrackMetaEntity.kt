package com.arno.lyramp.feature.stats.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "track_stats_meta", indices = [Index(value = ["language"])])
internal data class StatsTrackMetaEntity(
        @PrimaryKey val trackId: String,
        val trackName: String,
        val artists: String,
        val language: String,
        val totalWordsInLyrics: Int,
        val uniqueCefrWordsCount: Int,
        val processedAt: Long,
)
