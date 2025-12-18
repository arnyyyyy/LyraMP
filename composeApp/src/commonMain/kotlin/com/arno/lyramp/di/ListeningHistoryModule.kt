package com.arno.lyramp.di

import com.arno.lyramp.feature.listening_history.api.SpotifyMusicApi
import com.arno.lyramp.feature.listening_history.domain.SpotifyMusicService
import com.arno.lyramp.feature.listening_history.domain.MusicService
import com.arno.lyramp.feature.listening_history.domain.AppleMusicService
import com.arno.lyramp.feature.authorization.repository.AuthSelectionStorage
import com.arno.lyramp.feature.listening_history.domain.YandexMusicService
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryScreenModel
import org.koin.dsl.module

val listeningHistoryModule = module {
        single { SpotifyMusicApi(get()) }
        single<MusicService> {
                val last = AuthSelectionStorage.lastAuthorizedService
                return@single when (last) {
                        "SPOTIFY" -> SpotifyMusicService(
                                authRepo = get(),
                                api = get()
                        )

                        "YANDEX" -> YandexMusicService(
                                authRepo = get(),
                                httpClient = get()
                        )

                        "APPLE" -> AppleMusicService(
                                authRepo = get(),
                                httpClient = get()
                        )

                        else -> throw IllegalStateException("No authorized music service found")
                }
        }

        factory { ListeningHistoryScreenModel(get()) }
}
