package com.arno.lyramp.di

import com.arno.lyramp.feature.listening_history.api.SpotifyMusicApi
import com.arno.lyramp.feature.listening_history.domain.SpotifyMusicService
import com.arno.lyramp.feature.listening_history.domain.MusicService
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryScreenModel
import org.koin.dsl.module

val listeningHistoryModule = module {
        single { SpotifyMusicApi(get()) }
        single<MusicService> {
                SpotifyMusicService(
                        authRepo = get(),
                        api = get()
                )
        }

        factory { ListeningHistoryScreenModel(get()) }
}
