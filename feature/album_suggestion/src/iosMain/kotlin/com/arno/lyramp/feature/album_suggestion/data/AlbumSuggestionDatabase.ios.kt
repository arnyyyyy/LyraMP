package com.arno.lyramp.feature.album_suggestion.data

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal fun getAlbumSuggestionDatabaseBuilder(): RoomDatabase.Builder<AlbumSuggestionDatabase> {
        val documentDirectory = documentDirectory()
        val dbFilePath = "$documentDirectory/lyra_album_suggestion.db"
        return Room.databaseBuilder<AlbumSuggestionDatabase>(
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

