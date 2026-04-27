package com.arno.lyramp.feature.stats.di

import com.arno.lyramp.core.background.BackgroundTaskRegistry
import com.arno.lyramp.feature.stats.background.CefrStatsBackgroundTask
import com.arno.lyramp.feature.stats.data.StatsDatabase
import com.arno.lyramp.feature.stats.data.StatsTrackCefrWordRepository
import com.arno.lyramp.feature.stats.data.StatsTrackMetaRepository
import com.arno.lyramp.feature.stats.data.getStatsDatabase
import com.arno.lyramp.feature.stats.domain.usecase.GetLanguageStatsUseCase
import com.arno.lyramp.feature.stats.domain.usecase.ProcessTracksCefrUseCase
import com.arno.lyramp.feature.stats.presentation.StatsScreenModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val statsModule = module {
        single<StatsDatabase> { getStatsDatabase(get(named("stats"))) }
        single { get<StatsDatabase>().trackCefrWordDao() }
        single { get<StatsDatabase>().trackStatsMetaDao() }

        single<StatsTrackCefrWordRepository> { StatsTrackCefrWordRepository(dao = get()) }
        single<StatsTrackMetaRepository> { StatsTrackMetaRepository(dao = get()) }

        single {
                ProcessTracksCefrUseCase(
                        getRecentTracks = get(),
                        getLyrics = get(),
                        wordDifficulty = get(),
                        cefrWordRepository = get(),
                        metaRepository = get(),
                )
        }

        single {
                GetLanguageStatsUseCase(
                        getAllLearnWords = get(),
                        getShownWords = get(),
                        getRecentTracks = get(),
                        cefrWordRepository = get(),
                        metaRepository = get(),
                )
        }

        factory { (language: String?) ->
                StatsScreenModel(
                        languageOverride = language,
                        observeSelectedLanguage = get(),
                        getLanguageStats = get(),
                        processTracks = get(),
                )
        }

        BackgroundTaskRegistry.register(CefrStatsBackgroundTask.TASK_ID) { koin ->
                CefrStatsBackgroundTask(
                        processTracks = koin.get<ProcessTracksCefrUseCase>(),
                )
        }
}

expect val statsDatabaseModule: Module
