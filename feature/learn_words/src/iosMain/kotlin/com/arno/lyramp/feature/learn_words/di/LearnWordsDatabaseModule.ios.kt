package com.arno.lyramp.feature.learn_words.di

import androidx.room.RoomDatabase
import com.arno.lyramp.feature.learn_words.data.LearnWordsDatabase
import com.arno.lyramp.feature.learn_words.data.getLearnWordsDatabaseBuilder
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val learnWordsDatabaseModule: Module = module {
        single<RoomDatabase.Builder<LearnWordsDatabase>>(named("learn_words")) {
                getLearnWordsDatabaseBuilder()
        }
}
