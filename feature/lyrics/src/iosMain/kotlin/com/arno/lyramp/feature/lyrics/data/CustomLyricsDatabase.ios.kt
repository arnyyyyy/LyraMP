package com.arno.lyramp.feature.lyrics.data

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getCustomLyricsDatabaseBuilder(): RoomDatabase.Builder<CustomLyricsDatabase> {
        val documentDirectory = documentDirectory()
        val dbFilePath = "$documentDirectory/lyra_custom_lyrics.db"
        return Room.databaseBuilder<CustomLyricsDatabase>(
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
