package com.arno.lyramp.feature.stats.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface StatsTrackCefrWordDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAll(rows: List<StatsTrackCefrWordEntity>)

        @Query("SELECT * FROM track_word_cefr WHERE language = :language")
        suspend fun getForLanguage(language: String): List<StatsTrackCefrWordEntity>

        @Query("DELETE FROM track_word_cefr WHERE trackId = :trackId")
        suspend fun deleteForTrack(trackId: String)
}
