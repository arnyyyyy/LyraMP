package com.arno.lyramp.feature.authorization.di

import com.arno.lyramp.feature.authorization.domain.AppStartUseCase
import com.arno.lyramp.feature.authorization.domain.CompleteNoAuthOnboardingUseCase
import com.arno.lyramp.feature.authorization.domain.CompleteYandexLoginUseCase
import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.authorization.domain.SkipAuthorizationUseCase
import com.arno.lyramp.feature.authorization.presentation.AuthorizationScreenModel
import com.arno.lyramp.feature.authorization.data.YandexAuthRepository
import com.arno.lyramp.feature.authorization.presentation.AuthUpdateHandler
import com.arno.lyramp.feature.authorization.presentation.yandex.YandexAuthBus
import com.arno.lyramp.feature.authorization.presentation.yandex.YandexAuthBusProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
        single { YandexAuthRepository(get(named("auth_storage"))) }

        single { YandexAuthBus().also { YandexAuthBusProvider.set(it) } }

        single { ProvideAuthTokenUseCase(get()) }
        factory { AppStartUseCase(get()) }
        factory { CompleteNoAuthOnboardingUseCase(get()) }
        factory { CompleteYandexLoginUseCase(get()) }
        factory { SkipAuthorizationUseCase() }
        factory { GetLastAuthorizedServiceUseCase() }
        factory { AuthUpdateHandler() }
        factory { AuthorizationScreenModel(get(), get()) }
}
