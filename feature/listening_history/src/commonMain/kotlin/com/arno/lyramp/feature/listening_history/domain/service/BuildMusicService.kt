package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.authorization.domain.GetAuthPlaylistUseCase
import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.api.AppleMusicApi
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi

internal fun buildMusicService(
        getLastService: GetLastAuthorizedServiceUseCase,
        authToken: ProvideAuthTokenUseCase,
        getPlaylistUrl: GetAuthPlaylistUseCase,
        yandexApi: YandexMusicApi,
        appleMusicApi: AppleMusicApi,
): MusicService {
        val playlistUrl = getPlaylistUrl(MusicServiceType.NONE)?.takeIf { it.isNotBlank() }

        val playlistService: MusicService? = playlistUrl?.let { url ->
                when {
                        url.contains("music.apple.com") -> AppleMusicService(
                                getPlaylistUrl = getPlaylistUrl,
                                playlistSource = MusicServiceType.NONE,
                                api = appleMusicApi,
                        )

                        url.contains("music.yandex") -> YandexPlaylistMusicService(
                                getPlaylistUrl = getPlaylistUrl,
                                htmlApi = appleMusicApi,
                                yandexApi = yandexApi,
                        )

                        else -> null
                }
        }

        val lastService = getLastService()
        val authService: MusicService? = when (lastService) {
                "YANDEX" -> YandexMusicService(authToken = authToken, api = yandexApi)
                "APPLE" -> AppleMusicService(getPlaylistUrl = getPlaylistUrl, playlistSource = MusicServiceType.APPLE, api = appleMusicApi)
                else -> null
        }

        return when {
                authService != null && playlistService != null ->
                        CompositeMusicService(listOf(authService, playlistService))

                authService != null -> authService
                playlistService != null -> playlistService
                else -> EmptyMusicService()
        }
}
