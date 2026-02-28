package com.arno.lyramp.feature.music_streaming.domain

import com.arno.lyramp.feature.authorization.repository.AuthSelectionStorage
import com.arno.lyramp.util.Log

internal class StreamingServiceFactory(
        private val yandexStreamingService: YandexStreamingService,
        private val spotifyStreamingService: SpotifyStreamingService,
        private val appleStreamingService: AppleStreamingService
) {
        fun getService(): StreamingService {
                val authorizedService = AuthSelectionStorage.lastAuthorizedService

                return when (authorizedService) {
                        "YANDEX" -> yandexStreamingService

                        "SPOTIFY" -> spotifyStreamingService

                        "APPLE" -> appleStreamingService

                        else -> {
                                Log.logger.w { "No authorized streaming service found, defaulting to Yandex" }
                                yandexStreamingService
                        }
                }
        }
}
