package com.arno.lyramp.feature.listening_history.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ListeningHistoryDao {

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insertAll(tracks: List<ListeningHistoryTrackEntity>)

        @Query("SELECT * FROM listening_history_tracks ORDER BY localId DESC")
        fun getAllAsFlow(): Flow<List<ListeningHistoryTrackEntity>>

        @Query("SELECT * FROM listening_history_tracks ORDER BY localId DESC")
        suspend fun getAll(): List<ListeningHistoryTrackEntity>

        @Query("SELECT COUNT(*) FROM listening_history_tracks")
        suspend fun count(): Int

        @Query("DELETE FROM listening_history_tracks WHERE trackId NOT IN (:ids)")
        suspend fun deleteNotIn(ids: List<String>)

        @Query("UPDATE listening_history_tracks SET language = :language WHERE trackId = :trackId")
        suspend fun updateLanguage(trackId: String, language: String)

        @Query("DELETE FROM listening_history_tracks")
        suspend fun deleteAll()
}
