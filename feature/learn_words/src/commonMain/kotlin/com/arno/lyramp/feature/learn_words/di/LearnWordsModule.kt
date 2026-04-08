package com.arno.lyramp.feature.learn_words.di

import com.arno.lyramp.feature.learn_words.data.CefrDifficultyGroup
import com.arno.lyramp.feature.learn_words.data.CefrRepository
import com.arno.lyramp.feature.learn_words.data.LanguagePreferencesRepository
import com.arno.lyramp.feature.learn_words.data.LearnWordsDatabase
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import com.arno.lyramp.feature.learn_words.data.getLearnWordsDatabase
import com.arno.lyramp.feature.learn_words.presentation.ChooseModeScreenModel
import com.arno.lyramp.feature.learn_words.presentation.LearningMode
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsScreenModel
import com.arno.lyramp.feature.translation.domain.TranslationRepository
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val learnWordsModule = module {
        single<LearnWordsDatabase> { getLearnWordsDatabase(get(named("learn_words"))) }
        single { get<LearnWordsDatabase>().learnWordDao() }
        single { get<LearnWordsDatabase>().albumProgressDao() }
        single { LearnWordsRepository(dao = get()) }

        single { (LanguagePreferencesRepository()) }

        single { CefrRepository() }

        factory {
                ChooseModeScreenModel(
                        repository = get(),
                        languagePreferencesRepository = get(),
                        cefrRepository = get()
                )
        }

        factory { (mode: LearningMode, language: String?, cefrGroup: CefrDifficultyGroup?) ->
                LearnWordsScreenModel(
                        mode = mode,
                        language = language,
                        cefrGroup = cefrGroup,
                        repository = get(),
                        translationRepository = get<TranslationRepository>(),
                        cefrRepository = get()
                )
        }
}

expect val learnWordsDatabaseModule: Module
