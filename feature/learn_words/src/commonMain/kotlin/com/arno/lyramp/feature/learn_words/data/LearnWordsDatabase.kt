package com.arno.lyramp.feature.learn_words.data

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

@Database(entities = [LearnWordEntity::class], version = 2)
@ConstructedBy(LearnWordsDatabaseConstructor::class)
internal abstract class LearnWordsDatabase : RoomDatabase() {
        abstract fun learnWordDao(): LearnWordDao
}

internal val LEARN_WORDS_MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE learn_words ADD COLUMN progress REAL NOT NULL DEFAULT 0.0")
                connection.execSQL("ALTER TABLE learn_words ADD COLUMN albumId TEXT")
                connection.execSQL("ALTER TABLE learn_words ADD COLUMN trackIndex INTEGER")
        }
}

internal fun getLearnWordsDatabase(builder: RoomDatabase.Builder<LearnWordsDatabase>): LearnWordsDatabase {
        return builder
                .addMigrations(LEARN_WORDS_MIGRATION_1_2)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
}

@Suppress("KotlinNoActualForExpect")
internal expect object LearnWordsDatabaseConstructor : RoomDatabaseConstructor<LearnWordsDatabase> {
        override fun initialize(): LearnWordsDatabase
}
