package com.arno.lyramp.di

import com.arno.lyramp.feature.lyrics.repository.LyricsGetterRepository
import com.arno.lyramp.feature.lyrics.api.LyricsOvhApi
import org.koin.dsl.module

val lyricsModule = module {
        single { LyricsOvhApi(get()) }
        single { LyricsGetterRepository(get()) }
}

