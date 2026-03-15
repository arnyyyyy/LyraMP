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

@Database(entities = [ListeningHistoryTrackEntity::class], version = 2)
@ConstructedBy(ListeningHistoryDatabaseConstructor::class)
abstract class ListeningHistoryDatabase : RoomDatabase() {
        abstract fun listeningHistoryDao(): ListeningHistoryDao
}

@Suppress("KotlinNoActualForExpect")
expect object ListeningHistoryDatabaseConstructor : RoomDatabaseConstructor<ListeningHistoryDatabase> {
        override fun initialize(): ListeningHistoryDatabase
}

fun getListeningHistoryDatabase(builder: RoomDatabase.Builder<ListeningHistoryDatabase>): ListeningHistoryDatabase {
        return builder
                .addMigrations(MIGRATION_1_2)
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
}

val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE listening_history_tracks ADD COLUMN language TEXT")
        }
}
