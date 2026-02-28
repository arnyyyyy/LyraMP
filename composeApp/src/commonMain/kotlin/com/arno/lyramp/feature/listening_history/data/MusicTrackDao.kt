package com.arno.lyramp.feature.listening_history.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicTrackDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAll(tracks: List<MusicTrackEntity>)

        @Query("SELECT * FROM music_tracks")
        fun getAllAsFlow(): Flow<List<MusicTrackEntity>>

        @Query("SELECT * FROM music_tracks")
        suspend fun getAll(): List<MusicTrackEntity>

        @Query("SELECT COUNT(*) FROM music_tracks")
        suspend fun count(): Int

        @Query("DELETE FROM music_tracks")
        suspend fun deleteAll()
}
