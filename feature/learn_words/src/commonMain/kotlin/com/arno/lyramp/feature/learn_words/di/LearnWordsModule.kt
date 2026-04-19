package com.arno.lyramp.feature.learn_words.di

import com.arno.lyramp.core.model.CefrDifficultyGroup
import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.ClassifyWordsByCefrUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.SaveWordUseCase
import com.arno.lyramp.feature.learn_words.data.LearnWordsDatabase
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import com.arno.lyramp.feature.learn_words.data.getLearnWordsDatabase
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllLearnWordsUseCase
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllUserWordStringsUseCase
import com.arno.lyramp.feature.learn_words.domain.usecase.GetKnownWordsUseCase
import com.arno.lyramp.feature.learn_words.domain.usecase.GetLearnWordsByAlbumUseCase
import com.arno.lyramp.feature.learn_words.domain.usecase.GetLearnWordsByTrackUseCase
import com.arno.lyramp.feature.learn_words.domain.usecase.ObserveLearnWordsByAlbumUseCase
import com.arno.lyramp.feature.learn_words.domain.usecase.SaveLearnWordUseCase
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
        single { LearnWordsRepository(dao = get()) }

        single { SaveLearnWordUseCase(repository = get()) }
        single { GetAllLearnWordsUseCase(repository = get()) }
        single { GetKnownWordsUseCase(dao = get()) }
        single { GetAllUserWordStringsUseCase(dao = get()) }
        single { GetLearnWordsByTrackUseCase(dao = get()) }
        single { GetLearnWordsByAlbumUseCase(repository = get()) }
        single { ObserveLearnWordsByAlbumUseCase(repository = get()) }
        single<SaveWordUseCase> {
                val save = get<SaveLearnWordUseCase>()
                SaveWordUseCase { word, translation, sourceLang, trackName, artists, lyricLine ->
                        save(word, translation, sourceLang, trackName, artists, lyricLine)
                }
        }

        factory {
                ChooseModeScreenModel(
                        repository = get(),
                        classifyWordsByCefr = get<ClassifyWordsByCefrUseCase>(),
                        observeSelectedLanguage = get<ObserveSelectedLanguageUseCase>(),
                        saveSelectedLanguage = get<SaveSelectedLanguageUseCase>(),
                        getLearningLanguages = get<GetLearningLanguagesUseCase>(),
                        getLastAuthorizedService = get<GetLastAuthorizedServiceUseCase>(),
                )
        }

        factory { (mode: LearningMode, language: String?, cefrGroup: CefrDifficultyGroup?, albumId: String?, trackIndex: Int?) ->
                LearnWordsScreenModel(
                        mode = mode,
                        language = language,
                        cefrGroup = cefrGroup,
                        albumId = albumId,
                        trackIndex = trackIndex,
                        repository = get(),
                        getSpeechFilePath = get<GetSpeechFilePathUseCase>(),
                        classifyWordsByCefr = get<ClassifyWordsByCefrUseCase>(),
                )
        }
}

expect val learnWordsDatabaseModule: Module
