package com.arno.lyramp.di

import com.arno.lyramp.feature.listening_history.api.AppleMusicApi
import com.arno.lyramp.feature.listening_history.api.SpotifyMusicApi
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryDatabase
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.feature.listening_history.data.getListeningHistoryDatabase
import com.arno.lyramp.feature.listening_history.domain.SpotifyMusicService
import com.arno.lyramp.feature.listening_history.domain.MusicService
import com.arno.lyramp.feature.listening_history.domain.AppleMusicService
import com.arno.lyramp.feature.authorization.repository.AuthSelectionStorage
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.listening_history.domain.YandexMusicService
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryScreenModel
import org.koin.dsl.module

val listeningHistoryModule = module {
        single { SpotifyMusicApi(get()) }
        single { AppleMusicApi(get()) }
        single { YandexMusicApi(get()) }

        single<MusicService> {
                val last = AuthSelectionStorage.lastAuthorizedService
                return@single when (last) {
                        "SPOTIFY" -> SpotifyMusicService(
                                authRepo = get(),
                                api = get()
                        )

                        "YANDEX" -> YandexMusicService(
                                authRepo = get(),
                                api = get()
                        )

                        "APPLE" -> AppleMusicService(
                                authRepo = get(),
                                api = get(),
                        )

                        else -> throw IllegalStateException("No authorized music service found")
                }
        }

        single<ListeningHistoryDatabase> { getListeningHistoryDatabase(get()) }
        single { get<ListeningHistoryDatabase>().musicTrackDao() }

        single { ListeningHistoryRepository(musicService = get(), dao = get()) }

        factory { ListeningHistoryScreenModel(repository = get()) }
}
