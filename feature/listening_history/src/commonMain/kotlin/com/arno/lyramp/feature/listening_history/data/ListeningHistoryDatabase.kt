package com.arno.lyramp.feature.listening_history.data

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

@Database(entities = [ListeningHistoryTrackEntity::class], version = 3)
@ConstructedBy(ListeningHistoryDatabaseConstructor::class)
internal abstract class ListeningHistoryDatabase : RoomDatabase() {
        abstract fun listeningHistoryDao(): ListeningHistoryDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object ListeningHistoryDatabaseConstructor : RoomDatabaseConstructor<ListeningHistoryDatabase> {
        override fun initialize(): ListeningHistoryDatabase
}

internal fun getListeningHistoryDatabase(builder: RoomDatabase.Builder<ListeningHistoryDatabase>): ListeningHistoryDatabase {
        return builder
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
}

val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE listening_history_tracks ADD COLUMN language TEXT")
        }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE listening_history_tracks ADD COLUMN isShowing INTEGER NOT NULL DEFAULT 1")
        }
}

