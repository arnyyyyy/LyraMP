package com.arno.lyramp.feature.listening_history.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ListeningHistoryDao {
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insertAll(tracks: List<ListeningHistoryTrackEntity>)

        @Query("SELECT * FROM listening_history_tracks WHERE isShowing = 1 ORDER BY localId DESC")
        fun getAllAsFlow(): Flow<List<ListeningHistoryTrackEntity>>

        @Query("SELECT * FROM listening_history_tracks WHERE isShowing = 1 ORDER BY localId DESC")
        suspend fun getAll(): List<ListeningHistoryTrackEntity>

        @Query("SELECT COUNT(*) FROM listening_history_tracks")
        suspend fun count(): Int

        @Query("DELETE FROM listening_history_tracks WHERE trackId NOT IN (:ids)")
        suspend fun deleteNotIn(ids: List<String>)

        @Query("UPDATE listening_history_tracks SET language = :language WHERE trackId = :trackId")
        suspend fun updateLanguage(trackId: String, language: String)

        @Query(
                """UPDATE listening_history_tracks
                SET sourceId = :sourceId
                WHERE (sourceId IS NULL OR sourceId = '')
                AND trackId = :trackId"""
        )
        suspend fun backfillSourceIdByTrackId(trackId: String, sourceId: String)

        @Query(
                """UPDATE listening_history_tracks
                SET sourceId = :sourceId
                WHERE (sourceId IS NULL OR sourceId = '')
                AND (trackId IS NULL OR trackId = '')
                AND name = :name
                AND artists = :artists"""
        )
        suspend fun backfillSourceIdByTitleAndArtists(name: String, artists: String, sourceId: String)

        @Query("UPDATE listening_history_tracks SET isShowing = 0 WHERE trackId = :trackId")
        suspend fun hideTrack(trackId: String)

        @Query("DELETE FROM listening_history_tracks WHERE sourceId = :sourceId")
        suspend fun deleteBySourceId(sourceId: String)

        @Query("SELECT trackId FROM listening_history_tracks WHERE isShowing = 0")
        suspend fun getHiddenTrackIds(): List<String?>

        @Query("DELETE FROM listening_history_tracks")
        suspend fun deleteAll()
}
