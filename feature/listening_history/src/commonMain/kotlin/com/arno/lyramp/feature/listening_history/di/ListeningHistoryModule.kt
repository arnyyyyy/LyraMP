package com.arno.lyramp.feature.listening_history.di

import com.arno.lyramp.feature.authorization.domain.GetAuthPlaylistUseCase
import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.listening_history.api.AppleMusicApi
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryDatabase
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.feature.listening_history.data.getListeningHistoryDatabase
import com.arno.lyramp.feature.listening_history.domain.service.DynamicMusicService
import com.arno.lyramp.feature.listening_history.domain.usecase.AddManualTrackUseCase
import com.arno.lyramp.feature.listening_history.domain.service.MusicService
import com.arno.lyramp.feature.listening_history.domain.usecase.GetListeningHistoryUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetPlaylistUrlUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetRecentTracksUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.HideTrackUseCase
import com.arno.lyramp.feature.listening_history.domain.service.buildMusicService
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
                val getPlaylistUrl = get<GetAuthPlaylistUseCase>()
                val getLastService = get<GetLastAuthorizedServiceUseCase>()
                val yandexApi = get<YandexMusicApi>()
                val appleMusicApi = get<AppleMusicApi>()

                val initial = buildMusicService(getLastService, authToken, getPlaylistUrl, yandexApi, appleMusicApi)
                DynamicMusicService(initial)
        }
        single<MusicService> { get<DynamicMusicService>() }

        single<ListeningHistoryDatabase> { getListeningHistoryDatabase(get(named("listening_history"))) }
        single { get<ListeningHistoryDatabase>().listeningHistoryDao() }
        single { ListeningHistoryRepository(musicService = get(), dao = get(), detectLanguage = get<DetectLanguageUseCase>()) }

        single { GetRecentTracksUseCase(repository = get()) }
        single { SaveTrackLanguagesUseCase(repository = get()) }

        single { GetListeningHistoryUseCase(repository = get()) }
        single { HideTrackUseCase(repository = get()) }
        single { UpdateTrackLanguageUseCase(repository = get()) }
        single { SaveTrackLanguageUseCase(repository = get()) }
        single { AddManualTrackUseCase(repository = get()) }
        single { GetPlaylistUrlUseCase(getAuthPlaylistUrl = get()) }
        single {
                val authToken = get<ProvideAuthTokenUseCase>()
                val getPlaylistUrl = get<GetAuthPlaylistUseCase>()
                val getLastService = get<GetLastAuthorizedServiceUseCase>()
                val yandexApi = get<YandexMusicApi>()
                val appleMusicApi = get<AppleMusicApi>()

                SavePlaylistUrlUseCase(
                        saveAuthPlaylistUrl = get(),
                        dynamicMusicService = get(),
                        musicServiceFactory = {
                                buildMusicService(getLastService, authToken, getPlaylistUrl, yandexApi, appleMusicApi)
                        },
                )
        }

        factory {
                ListeningHistoryScreenModel(
                        getListeningHistory = get(),
                        hideTrack = get(),
                        updateTrackLanguage = get(),
                        addManualTrack = get(),
                        getPlaylistUrl = get(),
                        savePlaylistUrl = get(),
                        observeSelectedLanguage = get<ObserveSelectedLanguageUseCase>(),
                        saveSelectedLanguage = get<SaveSelectedLanguageUseCase>(),
                        getLearningLanguages = get<GetLearningLanguagesUseCase>(),
                        getLastAuthorizedService = get<GetLastAuthorizedServiceUseCase>(),
                )
        }
}
