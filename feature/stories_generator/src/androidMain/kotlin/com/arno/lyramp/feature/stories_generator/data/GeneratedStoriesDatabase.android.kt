package com.arno.lyramp.feature.stories_generator.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

internal fun getGeneratedStoriesDatabaseBuilder(context: Context): RoomDatabase.Builder<GeneratedStoriesDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("lyra_generated_stories.db")
        return Room.databaseBuilder<GeneratedStoriesDatabase>(
                context = appContext,
                name = dbFile.absolutePath
        )
}
