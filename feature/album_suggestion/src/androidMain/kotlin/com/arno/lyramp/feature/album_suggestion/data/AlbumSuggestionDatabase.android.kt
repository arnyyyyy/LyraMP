package com.arno.lyramp.feature.album_suggestion.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

internal fun getAlbumSuggestionDatabaseBuilder(context: Context): RoomDatabase.Builder<AlbumSuggestionDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("lyra_album_suggestion.db")
        return Room.databaseBuilder<AlbumSuggestionDatabase>(
                context = appContext,
                name = dbFile.absolutePath
        )
}
