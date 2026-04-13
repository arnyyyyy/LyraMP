package com.arno.lyramp.feature.lyrics.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [CustomLyricsEntity::class], version = 2)
@ConstructedBy(CustomLyricsDatabaseConstructor::class)
abstract class CustomLyricsDatabase : RoomDatabase() {
        abstract fun customLyricsDao(): CustomLyricsDao
}

@Suppress("KotlinNoActualForExpect")
expect object CustomLyricsDatabaseConstructor : RoomDatabaseConstructor<CustomLyricsDatabase> {
        override fun initialize(): CustomLyricsDatabase
}

fun getCustomLyricsDatabase(builder: RoomDatabase.Builder<CustomLyricsDatabase>): CustomLyricsDatabase {
        return builder
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .fallbackToDestructiveMigration(true)
                .build()
}
