package com.arno.lyramp.di

import com.arno.lyramp.feature.learn_words.data.LanguagePreferencesRepository
import com.arno.lyramp.feature.learn_words.data.LearnWordsDatabase
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import com.arno.lyramp.feature.learn_words.data.getLearnWordsDatabase
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsScreenModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val learnWordsModule = module {
        single<LearnWordsDatabase> { getLearnWordsDatabase(get(named("learn_words"))) }
        single { get<LearnWordsDatabase>().learnWordDao() }
        single { LearnWordsRepository(dao = get()) }

        single { (LanguagePreferencesRepository()) }

        factory { LearnWordsScreenModel(repository = get(), translationRepository = get(), languagePreferencesRepository = get()) }
}

expect val learnWordsDatabaseModule: Module
