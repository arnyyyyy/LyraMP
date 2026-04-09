package com.arno.lyramp.feature.extraction.data

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal fun getExtractionDatabaseBuilder(): RoomDatabase.Builder<ExtractionShownDatabase> {
        val documentDirectory = documentDirectory()
        val dbFilePath = "$documentDirectory/lyra_extraction.db"
        return Room.databaseBuilder<ExtractionShownDatabase>(
                name = dbFilePath,
        )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
        )
        return requireNotNull(documentDirectory?.path)
}
