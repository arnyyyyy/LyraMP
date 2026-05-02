package com.arno.lyramp.feature.listening_history.di

import com.arno.lyramp.core.background.BackgroundTaskRegistry
import com.arno.lyramp.core.data.PlaylistSourcesRepository
import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.listening_history.api.ExternalPlaylistApi
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.listening_history.background.LyricsPrefetchBackgroundTask
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryDatabase
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.feature.listening_history.data.getListeningHistoryDatabase
import com.arno.lyramp.feature.listening_history.domain.service.DynamicMusicService
import com.arno.lyramp.feature.listening_history.domain.usecase.AddManualTrackUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetAlbumWithTracksUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetSuggestedAlbumsUseCase
import com.arno.lyramp.feature.listening_history.domain.service.MusicService
import com.arno.lyramp.feature.listening_history.domain.usecase.GetListeningHistoryUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetLocalListeningHistoryUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetPlaylistSourcesUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetRecentTracksUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.HideTrackUseCase
import com.arno.lyramp.feature.listening_history.domain.service.buildMusicService
import com.arno.lyramp.feature.listening_history.domain.usecase.PrefetchLyricsForRecentTracksUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.PrefillListeningHistoryUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.RemovePlaylistSourceUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.ResolveRemainingsByYandexUseCase
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
        single { ExternalPlaylistApi(get()) }
        single { YandexMusicApi(get()) }

        single<DynamicMusicService> {
                val authToken = get<ProvideAuthTokenUseCase>()
                val getPlaylistSources = get<GetPlaylistSourcesUseCase>()
                val getLastService = get<GetLastAuthorizedServiceUseCase>()
                val yandexApi = get<YandexMusicApi>()
                val externalPlaylistApi = get<ExternalPlaylistApi>()

                DynamicMusicService(
                        factory = {
                                buildMusicService(
                                        getLastService = getLastService,
                                        authToken = authToken,
                                        getPlaylistSources = getPlaylistSources,
                                        yandexApi = yandexApi,
                                        externalPlaylistApi = externalPlaylistApi,
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
        single { PrefetchLyricsForRecentTracksUseCase(repository = get(), checkSyncedLyrics = get()) }

        single { GetListeningHistoryUseCase(repository = get()) }
        single { GetLocalListeningHistoryUseCase(repository = get()) }
        single { HideTrackUseCase(repository = get()) }
        single { UpdateTrackLanguageUseCase(repository = get()) }
        single { SaveTrackLanguageUseCase(repository = get()) }
        single { AddManualTrackUseCase(repository = get()) }
        single { GetPlaylistSourcesUseCase(repository = get()) }
        single { GetAlbumWithTracksUseCase(api = get(), provideAuthToken = get()) }
        single { GetSuggestedAlbumsUseCase(repository = get()) }
        single { ResolveRemainingsByYandexUseCase(repository = get(), api = get(), provideAuthToken = get()) }
        single {
                SavePlaylistUrlUseCase(
                        repository = get(),
                )
        }
        single {
                RemovePlaylistSourceUseCase(
                        listeningHistoryRepository = get(),
                        playlistSourcesRepository = get(),
                )
        }

        factory {
                ListeningHistoryScreenModel(
                        getListeningHistory = get(),
                        getLocalListeningHistory = get(),
                        hideTrackUseCase = get(),
                        updateTrackLanguage = get(),
                        addManualTrack = get(),
                        savePlaylistUrl = get(),
                        getPlaylistSources = get(),
                        removePlaylistSource = get(),
                        observeSelectedLanguage = get<ObserveSelectedLanguageUseCase>(),
                        saveSelectedLanguage = get<SaveSelectedLanguageUseCase>(),
                        getLearningLanguages = get<GetLearningLanguagesUseCase>(),
                        getLastAuthorizedService = get<GetLastAuthorizedServiceUseCase>(),
                        completeYandexLogin = get(),
                        resolveRemainingsByYandex = get(),
                        prefetchLyrics = get(),
                )
        }

        BackgroundTaskRegistry.register(LyricsPrefetchBackgroundTask.TASK_ID) { koin ->
                LyricsPrefetchBackgroundTask(
                        prefetchLyrics = koin.get<PrefetchLyricsForRecentTracksUseCase>(),
                )
        }
}
