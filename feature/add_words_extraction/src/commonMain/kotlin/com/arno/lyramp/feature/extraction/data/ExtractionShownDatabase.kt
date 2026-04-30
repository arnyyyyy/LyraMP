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
        entities = [
                ExtractionShownWordsEntity::class,
                ExtractionTrackStatusEntity::class,
                ExtractionPendingWordEntity::class,
        ],
        version = 4
)
@ConstructedBy(ExtractionDatabaseConstructor::class)
internal abstract class ExtractionShownDatabase : RoomDatabase() {
        abstract fun extractionShownWordsDao(): ExtractionShownWordsDao
        abstract fun extractionTrackStatusDao(): ExtractionTrackStatusDao
        abstract fun extractionPendingWordsDao(): ExtractionPendingWordsDao
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

internal val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(connection: SQLiteConnection) {
                val legacy = LEGACY_GLOBAL_SHOWN_LANGUAGE
                connection.execSQL(
                        """CREATE TABLE IF NOT EXISTS extraction_shown_words_new (
                                word TEXT NOT NULL,
                                language TEXT NOT NULL,
                                PRIMARY KEY(word, language)
                        )"""
                )
                connection.execSQL(
                        "INSERT OR IGNORE INTO extraction_shown_words_new (word, language) " +
                            "SELECT word, '$legacy' FROM extraction_shown_words"
                )
                connection.execSQL("DROP TABLE extraction_shown_words")
                connection.execSQL("ALTER TABLE extraction_shown_words_new RENAME TO extraction_shown_words")
        }
}

internal val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                        """CREATE TABLE IF NOT EXISTS extraction_pending_words (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                word TEXT NOT NULL,
                                cefrLevel TEXT NOT NULL,
                                lyricLine TEXT NOT NULL,
                                trackName TEXT NOT NULL,
                                artists TEXT NOT NULL,
                                language TEXT NOT NULL
                        )"""
                )
        }
}

internal fun getExtractionDatabase(builder: RoomDatabase.Builder<ExtractionShownDatabase>): ExtractionShownDatabase {
        return builder
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
}
