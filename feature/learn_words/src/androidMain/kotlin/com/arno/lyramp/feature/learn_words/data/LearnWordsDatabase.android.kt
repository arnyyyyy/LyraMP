package com.arno.lyramp.feature.learn_words.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

internal fun getLearnWordsDatabaseBuilder(context: Context): RoomDatabase.Builder<LearnWordsDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("lyra_learn_words.db")
        return Room.databaseBuilder<LearnWordsDatabase>(
                context = appContext,
                name = dbFile.absolutePath
        )
}
