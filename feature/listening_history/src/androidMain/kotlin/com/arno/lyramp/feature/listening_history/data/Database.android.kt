package com.arno.lyramp.feature.listening_history.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

internal fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<ListeningHistoryDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("lyra_listening_history.db")
        return Room.databaseBuilder<ListeningHistoryDatabase>(
                context = appContext,
                name = dbFile.absolutePath
        )
}
