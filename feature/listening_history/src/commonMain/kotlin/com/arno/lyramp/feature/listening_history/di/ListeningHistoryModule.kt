package com.arno.lyramp.feature.listening_history.di

import com.arno.lyramp.core.data.PlaylistSourcesRepository
import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.listening_history.api.AppleMusicApi
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryDatabase
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.feature.listening_history.data.getListeningHistoryDatabase
import com.arno.lyramp.feature.listening_history.domain.service.DynamicMusicService
import com.arno.lyramp.feature.listening_history.domain.usecase.AddManualTrackUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetAlbumWithTracksUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetSuggestedAlbumsUseCase
import com.arno.lyramp.feature.listening_history.domain.service.MusicService
import com.arno.lyramp.feature.listening_history.domain.usecase.GetListeningHistoryUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetPlaylistSourcesUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetRecentTracksUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.HideTrackUseCase
import com.arno.lyramp.feature.listening_history.domain.service.buildMusicService
import com.arno.lyramp.feature.listening_history.domain.usecase.PrefillListeningHistoryUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.RemovePlaylistSourceUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.SavePlaylistUrlUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.SaveTrackLanguageUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.SaveTrackLanguagesUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.UpdateTrackLanguageUseCase
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryScreenModel
import com.arno.lyramp.feature.translation.domain.DetectLanguageUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLearningLanguagesUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.ObserveSelectedLanguageUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.SaveSelectedLanguageUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val listeningHistoryModule = module {
        single { AppleMusicApi(get()) }
        single { YandexMusicApi(get()) }

        single<DynamicMusicService> {
                val authToken = get<ProvideAuthTokenUseCase>()
                val getPlaylistSources = get<GetPlaylistSourcesUseCase>()
                val getLastService = get<GetLastAuthorizedServiceUseCase>()
                val yandexApi = get<YandexMusicApi>()
                val appleMusicApi = get<AppleMusicApi>()

                DynamicMusicService(
                        factory = {
                                buildMusicService(
                                        getLastService = getLastService,
                                        authToken = authToken,
                                        getPlaylistSources = getPlaylistSources,
                                        yandexApi = yandexApi,
                                        appleMusicApi = appleMusicApi,
                                )
                        }
                )
        }
        single<MusicService> { get<DynamicMusicService>() }

        single { PlaylistSourcesRepository() }
        single<ListeningHistoryDatabase> { getListeningHistoryDatabase(get(named("listening_history"))) }
        single { get<ListeningHistoryDatabase>().listeningHistoryDao() }
        single { ListeningHistoryRepository(musicService = get(), dao = get(), detectLanguage = get<DetectLanguageUseCase>()) }

        single { GetRecentTracksUseCase(repository = get()) }
        single { SaveTrackLanguagesUseCase(repository = get()) }
        single { PrefillListeningHistoryUseCase(repository = get()) }

        single { GetListeningHistoryUseCase(repository = get()) }
        single { HideTrackUseCase(repository = get()) }
        single { UpdateTrackLanguageUseCase(repository = get()) }
        single { SaveTrackLanguageUseCase(repository = get()) }
        single { AddManualTrackUseCase(repository = get()) }
        single { GetPlaylistSourcesUseCase(repository = get()) }
        single { GetAlbumWithTracksUseCase(api = get(), provideAuthToken = get()) }
        single { GetSuggestedAlbumsUseCase(repository = get()) }
        single {
                SavePlaylistUrlUseCase(
                        repository = get(),
                )
        }
        single {
                RemovePlaylistSourceUseCase(
                        listeningHistoryRepository = get(),
                        repository = get(),
                )
        }

        factory {
                ListeningHistoryScreenModel(
                        getListeningHistory = get(),
                        hideTrack = get(),
                        updateTrackLanguage = get(),
                        addManualTrack = get(),
                        savePlaylistUrl = get(),
                        getPlaylistSources = get(),
                        removePlaylistSource = get(),
                        observeSelectedLanguage = get<ObserveSelectedLanguageUseCase>(),
                        saveSelectedLanguage = get<SaveSelectedLanguageUseCase>(),
                        getLearningLanguages = get<GetLearningLanguagesUseCase>(),
                        getLastAuthorizedService = get<GetLastAuthorizedServiceUseCase>(),
                )
        }
}
