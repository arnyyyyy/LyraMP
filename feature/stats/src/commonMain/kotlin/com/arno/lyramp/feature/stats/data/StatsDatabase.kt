package com.arno.lyramp.feature.stats.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [StatsTrackCefrWordEntity::class, StatsTrackMetaEntity::class], version = 2)
@ConstructedBy(StatsDatabaseConstructor::class)
internal abstract class StatsDatabase : RoomDatabase() {
        abstract fun trackCefrWordDao(): StatsTrackCefrWordDao
        abstract fun trackStatsMetaDao(): StatsTrackMetaDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object StatsDatabaseConstructor : RoomDatabaseConstructor<StatsDatabase> {
        override fun initialize(): StatsDatabase
}

internal val STATS_MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                        "CREATE INDEX IF NOT EXISTS index_track_word_cefr_language ON track_word_cefr(language)"
                )
                connection.execSQL(
                        "CREATE INDEX IF NOT EXISTS index_track_stats_meta_language ON track_stats_meta(language)"
                )
        }
}

internal fun getStatsDatabase(builder: RoomDatabase.Builder<StatsDatabase>): StatsDatabase {
        return builder
                .addMigrations(STATS_MIGRATION_1_2)
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
}
