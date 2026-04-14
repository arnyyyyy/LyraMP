package com.arno.lyramp.feature.lyrics.domain

import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.lyrics.api.YandexLyricsApi
import com.arno.lyramp.feature.lyrics.api.LyricsType
import com.arno.lyramp.util.Log

internal class YandexLyricsService(
        private val yandexLyricsApi: YandexLyricsApi,
        private val provideAuthToken: ProvideAuthTokenUseCase,
) : LyricsService {
        override val serviceName: String = "Yandex.Music"

        override suspend fun getLyrics(artist: String, song: String, trackId: String?): String? {
                val id = trackId ?: return null
                val token = provideAuthToken(MusicServiceType.YANDEX) ?: return null

                return runCatching { yandexLyricsApi.getLyrics(token, id, LyricsType.PLAIN) }
                        .onFailure { Log.logger.e(it) { "YandexLyricsService: Failed to fetch plain lyrics" } }
                        .getOrNull()
        }

        override suspend fun getTimestampedLyrics(artist: String, song: String, trackId: String?): String? {
                val id = trackId ?: return null
                val token = provideAuthToken(MusicServiceType.YANDEX) ?: return null

                return runCatching { yandexLyricsApi.getLyrics(token, id, LyricsType.TIMESTAMPED) }
                        .onFailure { Log.logger.e(it) { "YandexLyricsService: Failed to fetch timestamped lyrics" } }
                        .getOrNull()
        }
}
