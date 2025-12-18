package com.arno.lyramp.di

import com.arno.lyramp.feature.onboarding.presentation.OnboardingScreenModel
import org.koin.dsl.module

val onboardingModule = module {
        factory { OnboardingScreenModel(get(), get()) }
}

