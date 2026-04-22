package com.arno.lyramp.feature.stats.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface TrackStatsMetaDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun upsert(row: TrackStatsMetaEntity)

        @Query("SELECT * FROM track_stats_meta WHERE language = :language")
        suspend fun getForLanguage(language: String): List<TrackStatsMetaEntity>

        @Query("SELECT trackId FROM track_stats_meta")
        suspend fun getAllProcessedTrackIds(): List<String>

        @Query("SELECT COUNT(*) FROM track_stats_meta WHERE language = :language")
        suspend fun countForLanguage(language: String): Int
}
