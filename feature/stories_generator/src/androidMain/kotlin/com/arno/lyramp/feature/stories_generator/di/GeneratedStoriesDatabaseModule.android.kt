package com.arno.lyramp.feature.stories_generator.di

import com.arno.lyramp.feature.stories_generator.data.GeneratedStoriesDatabase
import com.arno.lyramp.feature.stories_generator.data.getGeneratedStoriesDatabaseBuilder
import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val generatedStoriesDatabaseModule: Module = module {
        single<RoomDatabase.Builder<GeneratedStoriesDatabase>>(named("generated_stories")) {
                getGeneratedStoriesDatabaseBuilder(androidContext())
        }
}
