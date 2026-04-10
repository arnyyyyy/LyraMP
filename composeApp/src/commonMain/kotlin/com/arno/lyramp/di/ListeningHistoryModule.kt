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
import com.arno.lyramp.feature.listening_history.domain.GetRecentTracksUseCase
import com.arno.lyramp.feature.listening_history.domain.SaveTrackLanguageUseCase
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryScreenModel
import com.arno.lyramp.feature.translation.domain.DetectLanguageUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLearningLanguagesUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.SaveSelectedLanguageUseCase
import org.koin.core.qualifier.named
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

        single<ListeningHistoryDatabase> { getListeningHistoryDatabase(get(named("listening_history"))) }
        single { get<ListeningHistoryDatabase>().listeningHistoryDao() }
        single { GetRecentTracksUseCase(historyDao = get()) }
        single { SaveTrackLanguageUseCase(dao = get()) }

        single { ListeningHistoryRepository(musicService = get(), dao = get(), detectLanguage = get<DetectLanguageUseCase>()) }
        // TODO: упростить репозиторий

        factory {
                ListeningHistoryScreenModel(
                        repository = get(),
                        getSelectedLanguage = get<GetSelectedLanguageUseCase>(),
                        saveSelectedLanguage = get<SaveSelectedLanguageUseCase>(),
                        getLearningLanguages = get<GetLearningLanguagesUseCase>(),
                )
        }
}
