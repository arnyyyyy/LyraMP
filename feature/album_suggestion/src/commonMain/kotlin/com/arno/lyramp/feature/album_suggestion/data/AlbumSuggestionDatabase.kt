package com.arno.lyramp.feature.album_suggestion.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [AlbumProgressEntity::class, ExtractionCandidateEntity::class], version = 1)
@ConstructedBy(AlbumSuggestionDatabaseConstructor::class)
internal abstract class AlbumSuggestionDatabase : RoomDatabase() {
        abstract fun albumProgressDao(): AlbumProgressDao
        abstract fun extractionCandidateDao(): ExtractionCandidateDao
}

internal fun getAlbumSuggestionDatabase(builder: RoomDatabase.Builder<AlbumSuggestionDatabase>): AlbumSuggestionDatabase {
        return builder
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
}

@Suppress("KotlinNoActualForExpect")
internal expect object AlbumSuggestionDatabaseConstructor : RoomDatabaseConstructor<AlbumSuggestionDatabase> {
        override fun initialize(): AlbumSuggestionDatabase
}
