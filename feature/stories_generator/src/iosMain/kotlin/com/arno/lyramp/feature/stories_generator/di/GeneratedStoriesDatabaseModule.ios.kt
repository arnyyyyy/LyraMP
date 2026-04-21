package com.arno.lyramp.feature.stories_generator.di

import androidx.room.RoomDatabase
import com.arno.lyramp.feature.stories_generator.data.GeneratedStoriesDatabase
import com.arno.lyramp.feature.stories_generator.data.getGeneratedStoriesDatabaseBuilder
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val generatedStoriesDatabaseModule: Module = module {
        single<RoomDatabase.Builder<GeneratedStoriesDatabase>>(named("generated_stories")) {
                getGeneratedStoriesDatabaseBuilder()
        }
}
