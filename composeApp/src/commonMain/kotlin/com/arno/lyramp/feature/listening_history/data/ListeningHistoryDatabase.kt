package com.arno.lyramp.feature.listening_history.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [MusicTrackEntity::class], version = 1)
@ConstructedBy(ListeningHistoryDatabaseConstructor::class)
abstract class ListeningHistoryDatabase : RoomDatabase() {
        abstract fun musicTrackDao(): MusicTrackDao
}

@Suppress("KotlinNoActualForExpect")
expect object ListeningHistoryDatabaseConstructor : RoomDatabaseConstructor<ListeningHistoryDatabase> {
        override fun initialize(): ListeningHistoryDatabase
}
