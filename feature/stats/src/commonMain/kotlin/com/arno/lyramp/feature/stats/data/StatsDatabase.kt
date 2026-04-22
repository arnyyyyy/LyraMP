package com.arno.lyramp.feature.stats.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [TrackCefrWordEntity::class, TrackStatsMetaEntity::class], version = 1)
@ConstructedBy(StatsDatabaseConstructor::class)
internal abstract class StatsDatabase : RoomDatabase() {
        abstract fun trackCefrWordDao(): TrackCefrWordDao
        abstract fun trackStatsMetaDao(): TrackStatsMetaDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object StatsDatabaseConstructor : RoomDatabaseConstructor<StatsDatabase> {
        override fun initialize(): StatsDatabase
}

internal fun getStatsDatabase(builder: RoomDatabase.Builder<StatsDatabase>): StatsDatabase {
        return builder
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
}
