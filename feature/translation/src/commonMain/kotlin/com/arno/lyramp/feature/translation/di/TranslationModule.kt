package com.arno.lyramp.feature.translation.di

import com.arno.lyramp.feature.translation.domain.DetectLanguageUseCase
import com.arno.lyramp.feature.translation.domain.GetSpeechFilePathUseCase
import com.arno.lyramp.feature.translation.domain.TranslateWordUseCase
import com.arno.lyramp.feature.translation.domain.TranslationRepository
import com.arno.lyramp.feature.translation.speech.TranslationSpeechController
import org.koin.dsl.module

val translationModule = module {
        single { TranslationRepository(get()) }
        single { TranslateWordUseCase(translationRepository = get()) }
        single { GetSpeechFilePathUseCase(translationRepository = get()) }
        single { DetectLanguageUseCase(translationRepository = get()) }
        factory { TranslationSpeechController() }
}
