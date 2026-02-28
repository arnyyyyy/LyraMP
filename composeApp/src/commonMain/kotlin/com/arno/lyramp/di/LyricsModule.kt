package com.arno.lyramp.di

import com.arno.lyramp.feature.lyrics.api.LyricsOvhApi
import com.arno.lyramp.feature.lyrics.api.YandexLyricsApi
import com.arno.lyramp.feature.lyrics.domain.LyricsOvhService
import com.arno.lyramp.feature.lyrics.domain.LyricsServiceFactory
import com.arno.lyramp.feature.lyrics.domain.YandexLyricsService
import com.arno.lyramp.feature.lyrics.domain.LyricsUseCase
import org.koin.dsl.module

val lyricsModule = module {
        single { LyricsOvhApi(get()) }
        single { YandexLyricsApi(get()) }

        single { YandexLyricsService(get(), get()) }
        single { LyricsOvhService(get()) }

        single { LyricsServiceFactory(get(), get()) }

        single { LyricsUseCase(get()) }
}
