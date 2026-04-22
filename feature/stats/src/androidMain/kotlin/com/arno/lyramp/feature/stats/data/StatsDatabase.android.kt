package com.arno.lyramp.feature.stats.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

internal fun getStatsDatabaseBuilder(context: Context): RoomDatabase.Builder<StatsDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("lyra_stats.db")
        return Room.databaseBuilder<StatsDatabase>(
                context = appContext,
                name = dbFile.absolutePath
        )
}
