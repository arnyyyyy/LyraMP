package com.arno.lyramp.feature.extraction.di

import com.arno.lyramp.core.background.BackgroundTaskRegistry
import com.arno.lyramp.core.model.WordDifficultyProvider
import com.arno.lyramp.feature.extraction.background.ExtractionBackgroundTask
import com.arno.lyramp.feature.extraction.data.CefrRepository
import com.arno.lyramp.feature.extraction.data.ExtractionShownDatabase
import com.arno.lyramp.feature.extraction.data.ExtractionShownWordsMapper
import com.arno.lyramp.feature.extraction.data.getExtractionDatabase
import com.arno.lyramp.feature.extraction.domain.Extractor
import com.arno.lyramp.feature.extraction.domain.WordSaver
import com.arno.lyramp.feature.extraction.domain.usecase.ClassifyWordsByCefrUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.GetCefrVocabularyUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.GetExhaustedTrackIdsUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.GetShownWordsUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.MarkTrackExhaustedUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.MarkWordsAsShownUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.MarkWordStringsAsShownUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.SaveWordUseCase
import com.arno.lyramp.feature.extraction.presentation.ExtractionScreenModel
import com.arno.lyramp.feature.listening_history.domain.usecase.GetRecentTracksUseCase
import com.arno.lyramp.feature.translation.domain.TranslateWordUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLanguageSettingsUseCase
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val extractionModule = module {
        single<ExtractionShownDatabase> { getExtractionDatabase(get(named("extraction"))) }
        single { get<ExtractionShownDatabase>().extractionShownWordsDao() }
        single { get<ExtractionShownDatabase>().extractionTrackStatusDao() }
        single { ExtractionShownWordsMapper() }
        single { CefrRepository() }
        single<WordDifficultyProvider> { get<CefrRepository>() }

        single { GetCefrVocabularyUseCase(cefrRepository = get()) }
        single { ClassifyWordsByCefrUseCase(cefrRepository = get()) }
        single { GetShownWordsUseCase(shownWordsDao = get()) }
        single { MarkWordsAsShownUseCase(shownWordsDao = get(), mapper = get()) }
        single { MarkWordStringsAsShownUseCase(shownWordsDao = get()) }
        single { GetExhaustedTrackIdsUseCase(dao = get()) }
        single { MarkTrackExhaustedUseCase(dao = get()) }

        single {
                Extractor(
                        getRecentTracks = get<GetRecentTracksUseCase>(),
                        getLyrics = get(),
                        getCefrVocabulary = get(),
                        getShownWords = get(),
                        markAsShown = get(),
                        getExhaustedTrackIds = get(),
                        markTrackExhausted = get(),
                )
        }

        single {
                WordSaver(
                        translateWord = get<TranslateWordUseCase>(),
                        saveWord = get<SaveWordUseCase>(),
                )
        }

        factory {
                ExtractionScreenModel(
                        extractor = get(),
                        wordSaver = get(),
                        getLanguageSettings = get<GetLanguageSettingsUseCase>(),
                )
        }

        BackgroundTaskRegistry.register(ExtractionBackgroundTask.TASK_ID) { koin ->
                ExtractionBackgroundTask(
                        extractor = koin.get<Extractor>(),
                        getLanguageSettings = koin.get<GetLanguageSettingsUseCase>(),
                )
        }
}

expect val extractionPlatformModule: Module
