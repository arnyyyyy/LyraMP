package com.arno.lyramp.feature.learn_words.data

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getLearnWordsDatabaseBuilder(): RoomDatabase.Builder<LearnWordsDatabase> {
        val documentDirectory = documentDirectory()
        val dbFilePath = "$documentDirectory/lyra_learn_words.db"
        return Room.databaseBuilder<LearnWordsDatabase>(
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
