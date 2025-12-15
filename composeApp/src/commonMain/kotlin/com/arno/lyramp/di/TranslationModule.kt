package com.arno.lyramp.di

import com.arno.lyramp.feature.translation.api.GoogleTranslationApi
import com.arno.lyramp.feature.translation.repository.TranslationRepository
import com.arno.lyramp.util.HttpClientFactory
import org.koin.dsl.module

val translationModule = module {
        single { GoogleTranslationApi(get()) }
        single { TranslationRepository(get()) }
}

