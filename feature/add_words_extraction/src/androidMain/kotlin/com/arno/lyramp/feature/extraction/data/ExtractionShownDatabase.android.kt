package com.arno.lyramp.feature.extraction.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

internal fun getExtractionDatabaseBuilder(context: Context): RoomDatabase.Builder<ExtractionShownDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("lyra_extraction.db")
        return Room.databaseBuilder<ExtractionShownDatabase>(
                context = appContext,
                name = dbFile.absolutePath
        )
}
