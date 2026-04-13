package com.arno.lyramp.feature.lyrics.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomLyricsDao {
        @Query("SELECT lyrics FROM custom_lyrics WHERE id = :id")
        suspend fun getLyrics(id: String): String?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun saveLyrics(entity: CustomLyricsEntity)
}
