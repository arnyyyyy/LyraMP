package com.arno.lyramp.feature.learn_words.di

import com.arno.lyramp.core.model.CefrDifficultyGroup
import com.arno.lyramp.feature.extraction.domain.usecase.ClassifyWordsByCefrUseCase
import com.arno.lyramp.feature.learn_words.data.LearnWordsDatabase
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import com.arno.lyramp.feature.learn_words.data.getLearnWordsDatabase
import com.arno.lyramp.feature.learn_words.presentation.ChooseModeScreenModel
import com.arno.lyramp.feature.learn_words.presentation.LearningMode
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsScreenModel
import com.arno.lyramp.feature.translation.domain.GetSpeechFilePathUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLearningLanguagesUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.ObserveSelectedLanguageUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.SaveSelectedLanguageUseCase
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val learnWordsModule = module {
        single<LearnWordsDatabase> { getLearnWordsDatabase(get(named("learn_words"))) }
        single { get<LearnWordsDatabase>().learnWordDao() }
        single { get<LearnWordsDatabase>().albumProgressDao() }
        single { LearnWordsRepository(dao = get()) }

        factory {
                ChooseModeScreenModel(
                        repository = get(),
                        classifyWordsByCefr = get<ClassifyWordsByCefrUseCase>(),
                        observeSelectedLanguage = get<ObserveSelectedLanguageUseCase>(),
                        saveSelectedLanguage = get<SaveSelectedLanguageUseCase>(),
                        getLearningLanguages = get<GetLearningLanguagesUseCase>(),
                )
        }

        factory { (mode: LearningMode, language: String?, cefrGroup: CefrDifficultyGroup?) ->
                LearnWordsScreenModel(
                        mode = mode,
                        language = language,
                        cefrGroup = cefrGroup,
                        repository = get(),
                        getSpeechFilePath = get<GetSpeechFilePathUseCase>(),
                        classifyWordsByCefr = get<ClassifyWordsByCefrUseCase>(),
                )
        }
}

expect val learnWordsDatabaseModule: Module
