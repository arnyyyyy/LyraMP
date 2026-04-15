package com.arno.lyramp.feature.music_streaming.domain

import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.util.Log

class StreamingServiceFactory(
        private val yandexStreamingService: YandexStreamingService,
        private val appleStreamingService: AppleStreamingService,
        private val getLastAuthorizedService: GetLastAuthorizedServiceUseCase,
) {
        fun getService(): StreamingService {
                val authorizedService = getLastAuthorizedService()

                return when (authorizedService) {
                        "YANDEX" -> yandexStreamingService

                        "APPLE" -> appleStreamingService

                        else -> {
                                Log.logger.w { "No authorized streaming service found, defaulting to Yandex" }
                                yandexStreamingService
                        }
                }
        }
}
