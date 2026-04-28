package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.api.AppleMusicApi
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.listening_history.domain.usecase.GetPlaylistSourcesUseCase

internal fun buildMusicService(
        getLastService: GetLastAuthorizedServiceUseCase,
        authToken: ProvideAuthTokenUseCase,
        getPlaylistSources: GetPlaylistSourcesUseCase,
        yandexApi: YandexMusicApi,
        appleMusicApi: AppleMusicApi,
): MusicService {
        val lastService = getLastService()
        val playlistServices = getPlaylistSources()
                .mapNotNull { source ->
                        val url = source.url
                        when {
                                url.contains("music.apple.com") -> SourceTaggedMusicService(
                                        sourceId = source.id,
                                        delegate = AppleMusicService(
                                                playlistUrlProvider = { url },
                                                api = appleMusicApi,
                                        ),
                                )

                                url.contains("music.yandex") -> SourceTaggedMusicService(
                                        sourceId = source.id,
                                        delegate = YandexPlaylistMusicService(
                                                playlistUrlProvider = { url },
                                                htmlApi = appleMusicApi,
                                                yandexApi = yandexApi,
                                        ),
                                )

                                else -> null
                        }
                }

        val authService: MusicService? = if (lastService == null) {
                null
        } else {
                when (MusicServiceType.valueOf(lastService)) {
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
