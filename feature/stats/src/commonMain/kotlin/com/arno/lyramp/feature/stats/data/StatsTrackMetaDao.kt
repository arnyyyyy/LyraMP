package com.arno.lyramp.feature.stats.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
internal interface StatsTrackMetaDao {
        @Upsert
        suspend fun upsert(row: StatsTrackMetaEntity)

        @Query("SELECT * FROM track_stats_meta WHERE language = :language")
        suspend fun getForLanguage(language: String): List<StatsTrackMetaEntity>

        @Query("SELECT trackId FROM track_stats_meta")
        suspend fun getAllProcessedTrackIds(): List<String>
}
