package com.arno.lyramp.feature.stats.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface TrackCefrWordDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAll(rows: List<TrackCefrWordEntity>)

        @Query("SELECT * FROM track_word_cefr WHERE language = :language")
        suspend fun getForLanguage(language: String): List<TrackCefrWordEntity>

        @Query("SELECT * FROM track_word_cefr WHERE trackId = :trackId")
        suspend fun getForTrack(trackId: String): List<TrackCefrWordEntity>

        @Query("DELETE FROM track_word_cefr WHERE trackId = :trackId")
        suspend fun deleteForTrack(trackId: String)

        @Query("SELECT COUNT(DISTINCT trackId) FROM track_word_cefr WHERE language = :language")
        suspend fun countProcessedTracks(language: String): Int
}
