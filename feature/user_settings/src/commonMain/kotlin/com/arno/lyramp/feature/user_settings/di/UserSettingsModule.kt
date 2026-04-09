package com.arno.lyramp.feature.user_settings.di

import com.arno.lyramp.feature.user_settings.data.UserSettingsRepository
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLearningLanguagesUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.SaveSelectedLanguageUseCase
import org.koin.dsl.module

val userSettingsModule = module {
        single { UserSettingsRepository() }

        single { GetSelectedLanguageUseCase(repository = get()) }
        single { SaveSelectedLanguageUseCase(repository = get()) }
        single { GetLearningLanguagesUseCase(repository = get()) }
}
