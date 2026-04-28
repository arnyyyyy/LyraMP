package com.arno.lyramp.feature.lyrics.di

import com.arno.lyramp.core.model.MusicTrack
import com.arno.lyramp.feature.lyrics.api.GeniusLyricsApi
import com.arno.lyramp.feature.lyrics.api.LyricsOvhApi
import com.arno.lyramp.feature.lyrics.api.YandexLyricsApi
import com.arno.lyramp.feature.lyrics.data.CustomLyricsDatabase
import com.arno.lyramp.feature.lyrics.data.CustomLyricsRepository
import com.arno.lyramp.feature.lyrics.data.getCustomLyricsDatabase
import com.arno.lyramp.feature.lyrics.domain.CheckSyncedLyricsUseCase
import com.arno.lyramp.feature.lyrics.domain.GeniusHtmlParser
import com.arno.lyramp.feature.lyrics.domain.GeniusLyricsService
import com.arno.lyramp.feature.lyrics.domain.GetLyricsUseCase
import com.arno.lyramp.feature.lyrics.domain.GetTimestampedLyricsUseCase
import com.arno.lyramp.feature.lyrics.domain.LyricsOvhService
import com.arno.lyramp.feature.lyrics.domain.LyricsServiceFactory
import com.arno.lyramp.feature.lyrics.domain.LyricsTextParser
import com.arno.lyramp.feature.lyrics.domain.YandexLyricsService
import com.arno.lyramp.feature.lyrics.presentation.LyricsScreenModel
import com.arno.lyramp.feature.lyrics.presentation.PopupAudioManager
import org.koin.core.qualifier.named
import org.koin.dsl.module

val lyricsModule = module {
        single<CustomLyricsDatabase> { getCustomLyricsDatabase(get(named("custom_lyrics"))) }
        single { get<CustomLyricsDatabase>().customLyricsDao() }
        single { CustomLyricsRepository(get()) }

        single { LyricsOvhApi(get()) }
        single { YandexLyricsApi(get()) }
        single { GeniusLyricsApi(get()) }

        single { YandexLyricsService(get(), get()) }
        single { LyricsOvhService(get()) }
        single { GeniusHtmlParser() }
        single { GeniusLyricsService(get(), get()) }
        single { LyricsServiceFactory(get(), get(), get(), get()) }

        single { GetLyricsUseCase(get()) }
        single { GetTimestampedLyricsUseCase(get()) }
        single { CheckSyncedLyricsUseCase(get()) }
        single { LyricsTextParser() }

        factory { (track: MusicTrack) ->
                LyricsScreenModel(
                        track = track,
                        getLyrics = get(),
                        customLyricsRepository = get(),
                        translateWord = get(),
                        audioManager = PopupAudioManager(
                                getSpeechFilePath = get(),
                                speechController = get(),
                        ),
                        saveWordToLearn = get(),
                        lyricsTextParser = get(),
                        wordDifficultyProvider = get(),
                        getSelectedLanguage = get(),
                )
        }
}

expect val lyricsPlatformModule: org.koin.core.module.Module
