package com.arno.lyramp.feature.music_streaming.domain

import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.util.Log

class StreamingServiceFactory(
        private val yandexStreamingService: YandexStreamingService,
        private val getLastAuthorizedService: GetLastAuthorizedServiceUseCase,
) {
        fun getService(): StreamingService {
                val authorizedService = getLastAuthorizedService()

                if (authorizedService == null) {
                        Log.logger.w { "No authorized streaming service found, defaulting to Yandex" }
                        return yandexStreamingService
                }

                val type = MusicServiceType.valueOf(authorizedService)

                return when (type) {
                        MusicServiceType.YANDEX -> yandexStreamingService
                        MusicServiceType.NONE -> {
                                Log.logger.w { "No authorized streaming service found, defaulting to Yandex" }
                                yandexStreamingService // TODO?
                        }
                }
        }
}