package com.arno.lyramp.feature.onboarding.di

import com.arno.lyramp.feature.listening_history.domain.usecase.SaveTrackLanguagesUseCase
import com.arno.lyramp.feature.onboarding.domain.AnalyzeLanguagesUseCase
import com.arno.lyramp.feature.onboarding.presentation.OnboardingScreenModel
import com.arno.lyramp.feature.translation.domain.TranslateWordWithStateUseCase
import org.koin.dsl.module

val onboardingModule = module {
        single { AnalyzeLanguagesUseCase(get<TranslateWordWithStateUseCase>()) }
        factory { OnboardingScreenModel(get(), get(), get<SaveTrackLanguagesUseCase>()) }
}
