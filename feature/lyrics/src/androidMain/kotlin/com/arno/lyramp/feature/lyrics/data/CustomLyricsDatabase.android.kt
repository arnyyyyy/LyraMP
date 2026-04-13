package com.arno.lyramp.feature.lyrics.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getCustomLyricsDatabaseBuilder(context: Context): RoomDatabase.Builder<CustomLyricsDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("lyra_custom_lyrics.db")
        return Room.databaseBuilder<CustomLyricsDatabase>(
                context = appContext,
                name = dbFile.absolutePath
        )
}
