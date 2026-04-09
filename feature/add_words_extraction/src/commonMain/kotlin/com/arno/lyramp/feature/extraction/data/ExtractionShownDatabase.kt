package com.arno.lyramp.feature.extraction.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [ExtractionShownWordsEntity::class], version = 1)
@ConstructedBy(ExtractionDatabaseConstructor::class)
internal abstract class ExtractionShownDatabase : RoomDatabase() {
        abstract fun extractionShownWordsDao(): ExtractionShownWordsDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object ExtractionDatabaseConstructor : RoomDatabaseConstructor<ExtractionShownDatabase> {
        override fun initialize(): ExtractionShownDatabase
}

internal fun getExtractionDatabase(builder: RoomDatabase.Builder<ExtractionShownDatabase>): ExtractionShownDatabase {
        return builder
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
}
