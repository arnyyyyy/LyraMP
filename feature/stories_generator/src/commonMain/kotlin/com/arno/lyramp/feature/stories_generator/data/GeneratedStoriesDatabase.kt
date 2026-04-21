package com.arno.lyramp.feature.stories_generator.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [GeneratedStoryEntity::class], version = 1)
@ConstructedBy(GeneratedStoriesDatabaseConstructor::class)
internal abstract class GeneratedStoriesDatabase : RoomDatabase() {
        abstract fun generatedStoryDao(): GeneratedStoryDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object GeneratedStoriesDatabaseConstructor : RoomDatabaseConstructor<GeneratedStoriesDatabase> {
        override fun initialize(): GeneratedStoriesDatabase
}

internal fun getGeneratedStoriesDatabase(builder: RoomDatabase.Builder<GeneratedStoriesDatabase>): GeneratedStoriesDatabase {
        return builder
                .fallbackToDestructiveMigration(dropAllTables = true)
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
}
