package com.arno.lyramp.feature.authorization.di

import com.arno.lyramp.feature.authorization.domain.GetAppStartDestinationUseCase
import com.arno.lyramp.feature.authorization.domain.CompleteNonYandexLoginUseCase
import com.arno.lyramp.feature.authorization.domain.CompleteYandexLoginUseCase
import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.authorization.presentation.AuthorizationScreenModel
import com.arno.lyramp.feature.authorization.data.AuthSelectionStorage
import com.arno.lyramp.feature.authorization.data.YandexAuthRepository
import com.arno.lyramp.feature.authorization.presentation.AuthReducer
import com.arno.lyramp.feature.authorization.presentation.yandex.YandexAuthBus
import org.koin.dsl.module

val authModule = module {
        single { AuthSelectionStorage(get(plainSettingsQualifier)) }
        single { YandexAuthRepository(get(secureSettingsQualifier), get()) }

        single { YandexAuthBus() }

        single { ProvideAuthTokenUseCase(get()) }
        factory { GetAppStartDestinationUseCase(get(), get()) }
        factory { CompleteNonYandexLoginUseCase(get(), get()) }
        factory { CompleteYandexLoginUseCase(get()) }
        factory { GetLastAuthorizedServiceUseCase(get()) }
        factory { AuthReducer() }
        factory { AuthorizationScreenModel(get(), get(), get()) }
}
