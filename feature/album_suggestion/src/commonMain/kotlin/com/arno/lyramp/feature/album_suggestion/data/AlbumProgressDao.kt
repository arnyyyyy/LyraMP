package com.arno.lyramp.feature.album_suggestion.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface AlbumProgressDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun upsert(entity: AlbumProgressEntity)

        @Query("SELECT * FROM album_progress WHERE albumId = :albumId LIMIT 1")
        suspend fun getByAlbumId(albumId: String): AlbumProgressEntity?

        @Query("SELECT * FROM album_progress WHERE albumId IN (:albumIds)")
        suspend fun getByAlbumIds(albumIds: List<String>): List<AlbumProgressEntity>
}
