package com.arno.lyramp.feature.authorization.di

import com.arno.lyramp.feature.authorization.domain.AppStartUseCase
import com.arno.lyramp.feature.authorization.domain.GetAuthPlaylistUseCase
import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.authorization.domain.SaveAuthPlaylistUrlUseCase
import com.arno.lyramp.feature.authorization.domain.SkipAuthorizationUseCase
import com.arno.lyramp.feature.authorization.presentation.AuthUpdateHandler
import com.arno.lyramp.feature.authorization.presentation.AuthorizationScreenModel
import com.arno.lyramp.feature.authorization.data.YandexAuthRepository
import com.arno.lyramp.feature.authorization.data.AppleAuthRepository
import com.arno.lyramp.feature.authorization.data.OptionalPlaylistRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
        single { YandexAuthRepository(get(named("auth_storage"))) }
        single { AppleAuthRepository() }
        single { OptionalPlaylistRepository() }

        factory { AppStartUseCase(get(), get()) }
        factory { GetAuthPlaylistUseCase(get(), get()) }
        factory { SaveAuthPlaylistUrlUseCase(get(), get()) }
        factory { SkipAuthorizationUseCase() }
        single { ProvideAuthTokenUseCase(get()) }
        factory { GetLastAuthorizedServiceUseCase() }

        factory { AuthUpdateHandler() }
        factory { AuthorizationScreenModel(get(), get()) }
}

