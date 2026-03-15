package com.arno.lyramp.feature.learn_words.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [LearnWordEntity::class], version = 1)
@ConstructedBy(LearnWordsDatabaseConstructor::class)
abstract class LearnWordsDatabase : RoomDatabase() {
        abstract fun learnWordDao(): LearnWordDao
}

fun getLearnWordsDatabase(builder: RoomDatabase.Builder<LearnWordsDatabase>): LearnWordsDatabase {
        return builder
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
}

expect object LearnWordsDatabaseConstructor : RoomDatabaseConstructor<LearnWordsDatabase> {
        override fun initialize(): LearnWordsDatabase
}
