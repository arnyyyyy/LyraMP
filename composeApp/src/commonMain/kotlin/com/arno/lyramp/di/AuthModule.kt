package com.arno.lyramp.di

import com.arno.lyramp.feature.authorization.api.SpotifyAuthApi
import com.arno.lyramp.feature.authorization.domain.AuthService
import com.arno.lyramp.feature.authorization.domain.AuthServiceImpl
import com.arno.lyramp.feature.authorization.domain.AppStartUseCase
import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.arno.lyramp.feature.authorization.model.SpotifyAuthConfig
import com.arno.lyramp.feature.authorization.presentation.AuthUpdateHandler
import com.arno.lyramp.feature.authorization.repository.AuthApiRepository
import com.arno.lyramp.feature.authorization.repository.SpotifyAuthRepository
import com.arno.lyramp.feature.authorization.repository.YandexAuthRepository
import com.arno.lyramp.feature.authorization.repository.AppleAuthRepository
import com.arno.lyramp.feature.authorization.presentation.AuthorizationScreenModel
import org.koin.dsl.module

val authModule = module {
        single<AuthApiRepository> { get<SpotifyAuthRepository>() }
        single { SpotifyAuthApi(get()) }

        single { SpotifyAuthRepository(get(), SpotifyAuthConfig) }
        single { YandexAuthRepository() }
        single { AppleAuthRepository() }

        single<AuthService> {
                AuthServiceImpl(
                        mapOf(
                                MusicServiceType.SPOTIFY to get<SpotifyAuthRepository>(),
                                MusicServiceType.YANDEX to get<YandexAuthRepository>()
                        )
                )
        }
        factory { AppStartUseCase(get<SpotifyAuthRepository>(), get<YandexAuthRepository>(), get<AppleAuthRepository>()) }

        factory { AuthUpdateHandler() }
        factory { AuthorizationScreenModel(get(), get()) }
}
