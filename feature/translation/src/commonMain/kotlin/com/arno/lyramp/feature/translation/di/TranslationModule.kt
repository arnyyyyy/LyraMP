package com.arno.lyramp.feature.translation.di

import com.arno.lyramp.feature.translation.domain.TranslationRepository
import org.koin.dsl.module

val translationModule = module {
        single { TranslationRepository(get()) }
}
