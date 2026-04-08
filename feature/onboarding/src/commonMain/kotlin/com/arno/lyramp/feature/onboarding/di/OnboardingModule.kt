package com.arno.lyramp.feature.onboarding.di

import com.arno.lyramp.feature.onboarding.domain.AnalyzeLanguagesUseCase
import com.arno.lyramp.feature.onboarding.presentation.OnboardingScreenModel
import org.koin.dsl.module

val onboardingModule = module {
        single { AnalyzeLanguagesUseCase(get()) }
        factory { OnboardingScreenModel(get(), get(), get()) }
}
