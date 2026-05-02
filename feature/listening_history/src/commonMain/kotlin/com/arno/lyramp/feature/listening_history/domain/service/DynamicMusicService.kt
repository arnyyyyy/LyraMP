package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.api.ExternalPlaylistApi
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.listening_history.domain.usecase.GetPlaylistSourcesUseCase

internal class DynamicMusicService(private val factory: () -> MusicService) : MusicService {
        override suspend fun getListeningHistory(limit: Int?) = factory().getListeningHistory(limit)
}

internal fun buildMusicService(
        getLastService: GetLastAuthorizedServiceUseCase,
        authToken: ProvideAuthTokenUseCase,
        getPlaylistSources: GetPlaylistSourcesUseCase,
        yandexApi: YandexMusicApi,
        externalPlaylistApi: ExternalPlaylistApi,
): MusicService {
        val playlistServices = getPlaylistSources().mapNotNull { source ->
                val url = source.url
                when {
                        url.contains("music.apple.com") -> SourceTaggedMusicService(
                                sourceId = source.id,
                                delegate = AppleMusicService(
                                        url = url,
                                        api = externalPlaylistApi,
                                ),
                        )

                        url.contains("music.yandex") -> SourceTaggedMusicService(
                                sourceId = source.id,
                                delegate = YandexPlaylistMusicService(
                                        url = url,
                                        htmlApi = externalPlaylistApi,
                                        yandexApi = yandexApi,
                                ),
                        )

                        else -> null
                }
        }

        val authService = getLastService()?.let {
                runCatching { MusicServiceType.valueOf(it) }.getOrNull()
        }?.let { serviceType ->
                when (serviceType) {
                        MusicServiceType.YANDEX -> SourceTaggedMusicService(
                                sourceId = SOURCE_YANDEX_LIKES,
                                delegate = YandexMusicService(authToken = authToken, api = yandexApi),
                        )

                        MusicServiceType.NONE -> null
                }
        }

        val services = listOfNotNull(authService) + playlistServices
        return if (services.isEmpty()) EmptyMusicService() else CompositeMusicService(services)
}

const val SOURCE_YANDEX_LIKES = "yandex_likes"
