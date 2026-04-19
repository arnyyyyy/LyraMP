package com.arno.lyramp.feature.extraction.data

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

@Database(
        entities = [ExtractionShownWordsEntity::class, ExtractionTrackStatusEntity::class], version = 2
)
@ConstructedBy(ExtractionDatabaseConstructor::class)
internal abstract class ExtractionShownDatabase : RoomDatabase() {
        abstract fun extractionShownWordsDao(): ExtractionShownWordsDao
        abstract fun extractionTrackStatusDao(): ExtractionTrackStatusDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object ExtractionDatabaseConstructor : RoomDatabaseConstructor<ExtractionShownDatabase> {
        override fun initialize(): ExtractionShownDatabase
}

internal val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                        """CREATE TABLE IF NOT EXISTS extraction_track_status (
                                trackId TEXT NOT NULL PRIMARY KEY,
                                trackName TEXT NOT NULL DEFAULT '',
                                exhaustedLevels TEXT NOT NULL DEFAULT ''
                        )"""
                )
        }
}

internal fun getExtractionDatabase(builder: RoomDatabase.Builder<ExtractionShownDatabase>): ExtractionShownDatabase {
        return builder
                .addMigrations(MIGRATION_1_2)
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
}
