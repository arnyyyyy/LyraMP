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

        @Query(
                """SELECT EXISTS(
                        SELECT 1 FROM listening_history_tracks
                        WHERE localId != :localId
                        AND isShowing = 1
                        AND trackId = :trackId
                )"""
        )
        suspend fun hasShowingTrackIdExceptLocalId(trackId: String, localId: Long): Boolean

        @Query(
                """SELECT * FROM listening_history_tracks
                WHERE isShowing = 1
                AND (language IS NULL OR language = '')
                ORDER BY localId DESC"""
        )
        suspend fun getTracksWithoutLanguage(): List<ListeningHistoryTrackEntity>

        @Query(
                """SELECT * FROM listening_history_tracks
                WHERE isShowing = 1
                AND yandexResolveAttempted = 0
                AND (
                        trackId IS NULL
                        OR trackId = ''
                        OR trackId LIKE '%||%'
                )"""
        )
        suspend fun getTracksMissingYandexIds(): List<ListeningHistoryTrackEntity>

        @Query("UPDATE listening_history_tracks SET yandexResolveAttempted = 1 WHERE localId = :localId")
        suspend fun markYandexResolveAttempted(localId: Long)

        @Query("SELECT COUNT(*) FROM listening_history_tracks")
        suspend fun count(): Int

        @Query(
                """DELETE FROM listening_history_tracks
                WHERE isShowing = 1
                AND CASE
                        WHEN trackId IS NOT NULL AND trackId != '' THEN trackId
                        ELSE name || '||' || artists
                END NOT IN (:keys)"""
        )
        suspend fun deleteShowingNotIn(keys: List<String>)

        @Query("DELETE FROM listening_history_tracks WHERE isShowing = 1")
        suspend fun deleteAllShowing()

        @Query("DELETE FROM listening_history_tracks WHERE localId = :localId")
        suspend fun deleteByLocalId(localId: Long)

        @Query("UPDATE listening_history_tracks SET language = :language WHERE trackId = :trackId")
        suspend fun updateLanguage(trackId: String, language: String)

        @Query("UPDATE listening_history_tracks SET language = :language WHERE localId = :localId")
        suspend fun updateLanguageByLocalId(localId: Long, language: String)

        @Query(
                """UPDATE listening_history_tracks
                SET trackId = :newTrackId,
                        albumId = :albumId,
                        albumName = :albumName,
                        artists = :artists
                WHERE trackId = :oldTrackId"""
        )
        suspend fun resolveManualTrack(
                oldTrackId: String,
                newTrackId: String,
                albumId: String?,
                albumName: String?,
                artists: String,
        )

        @Query(
                """UPDATE listening_history_tracks
                SET trackId = :newTrackId,
                        albumId = :albumId,
                        albumName = :albumName,
                        artists = :artists
                WHERE localId = :localId"""
        )
        suspend fun resolveTrackByLocalId(
                localId: Long,
                newTrackId: String,
                albumId: String?,
                albumName: String?,
                artists: String,
        )

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

        @Query(
                """UPDATE listening_history_tracks
                SET isShowing = 0
                WHERE CASE
                        WHEN trackId IS NOT NULL AND trackId != '' THEN trackId
                        ELSE name || '||' || artists
                END = :key"""
        )
        suspend fun hideTrackByKey(key: String)

        @Query("DELETE FROM listening_history_tracks WHERE sourceId = :sourceId")
        suspend fun deleteBySourceId(sourceId: String)

        @Query(
                """SELECT CASE
                        WHEN trackId IS NOT NULL AND trackId != '' THEN trackId
                        ELSE name || '||' || artists
                END
                FROM listening_history_tracks
                WHERE isShowing = 0"""
        )
        suspend fun getHiddenTrackKeys(): List<String>

        @Query("DELETE FROM listening_history_tracks")
        suspend fun deleteAll()
}
