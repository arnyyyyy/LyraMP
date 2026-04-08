package com.arno.lyramp.feature.music_streaming.domain

import com.arno.lyramp.feature.authorization.repository.YandexAuthRepository
import com.arno.lyramp.feature.music_streaming.api.YandexStreamingApi
import com.arno.lyramp.feature.music_streaming.model.StreamingTrackInfo
import com.arno.lyramp.util.Log

 class YandexStreamingService(
        private val yandexStreamingApi: YandexStreamingApi,
        private val yandexAuthRepository: YandexAuthRepository
) : StreamingService {

        override val serviceName: String = "Yandex.Music"

        override suspend fun getTrackStreamingInfo(
                trackId: String
        ): StreamingTrackInfo? {
                val token = yandexAuthRepository.provideValidAccessToken() ?: return null

                return runCatching {
                        val downloadInfo = yandexStreamingApi.getTrackDownloadInfo(token, trackId)
                        StreamingTrackInfo(
                                url = downloadInfo.url ?: error("Download URL is null"),
                                encryptionKey = downloadInfo.key,
                                transport = downloadInfo.transport
                        )
                }
                        .onFailure { Log.logger.e(it) { "YandexStreamingService: Failed to get streaming info" } }
                        .getOrNull()
        }
}
