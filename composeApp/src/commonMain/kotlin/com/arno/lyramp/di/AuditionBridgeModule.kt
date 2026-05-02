package com.arno.lyramp.di

import com.arno.lyramp.feature.listening_history.domain.usecase.GetLocalListeningHistoryUseCase
import com.arno.lyramp.feature.listening_history.domain.service.MusicService
import com.arno.lyramp.feature.listening_history.model.hasResolvedYandexTrackId
import com.arno.lyramp.feature.listening_practice.domain.AuditionLibraryProvider
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import org.koin.dsl.module

val auditionBridgeModule = module {
        single<AuditionLibraryProvider> {
                val getLocalHistory = get<GetLocalListeningHistoryUseCase>()
                val musicService = get<MusicService>()
                object : AuditionLibraryProvider {
                        override suspend fun getTracks(language: String?): List<PracticeTrack> {
                                val local = getLocalHistory()
                                val source = local.ifEmpty { musicService.getListeningHistory(limit = null) }
                                return source
                                        .filter { language == null || it.language == language }
                                        .mapNotNull { track ->
                                                 if (!track.hasResolvedYandexTrackId()) return@mapNotNull null
                                                 val id = requireNotNull(track.id)
                                                PracticeTrack(
                                                        id = id,
                                                        albumId = track.albumId,
                                                        name = track.name,
                                                        artists = track.artists,
                                                        albumName = track.albumName,
                                                        imageUrl = track.imageUrl,
                                                )
                                        }
                        }
                }
        }
}
