package com.arno.lyramp.di

import com.arno.lyramp.feature.authorization.api.SpotifyAuthApi
import com.arno.lyramp.feature.authorization.domain.AuthService
import com.arno.lyramp.feature.authorization.domain.AuthServiceImpl
import com.arno.lyramp.feature.authorization.domain.usecase.AppStartUseCase
import com.arno.lyramp.feature.authorization.domain.usecase.HandleAuthCallbackUseCase
import com.arno.lyramp.feature.authorization.domain.usecase.InitAuthUseCase
import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.arno.lyramp.feature.authorization.model.SpotifyAuthConfig
import com.arno.lyramp.feature.authorization.presentation.AuthUpdateHandler
import com.arno.lyramp.feature.authorization.repository.AuthApiRepository
import com.arno.lyramp.feature.authorization.repository.SpotifyAuthRepository
import com.arno.lyramp.feature.authorization.repository.YandexAuthRepository
import com.arno.lyramp.feature.authorization.presentation.AuthorizationScreenModel
import com.arno.lyramp.feature.authorization.repository.AuthPlaylistRepository
import org.koin.dsl.module

val authModule = module {
        single<AuthApiRepository> { get<SpotifyAuthRepository>() }
        single<AuthService> {
                AuthServiceImpl(mapOf(MusicServiceType.SPOTIFY to get<AuthApiRepository>()))
        }

        single { SpotifyAuthApi(get()) }
        single { SpotifyAuthRepository(get(), SpotifyAuthConfig) }

        single<AuthPlaylistRepository> { YandexAuthRepository() }

        factory { InitAuthUseCase(get()) }
        factory { HandleAuthCallbackUseCase(get()) }

        factory { AppStartUseCase(get(), get()) }

        factory { AuthUpdateHandler() }
        factory { AuthorizationScreenModel(get(), get(), get()) }
}
