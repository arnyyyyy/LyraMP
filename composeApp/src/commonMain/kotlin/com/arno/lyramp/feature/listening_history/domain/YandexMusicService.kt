package com.arno.lyramp.feature.listening_history.domain

import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.authorization.repository.YandexAuthRepository
import com.arno.lyramp.feature.listening_history.model.MusicTrack
import com.arno.lyramp.util.Log
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class YandexMusicService(
        private val authRepo: YandexAuthRepository,
        private val httpClient: HttpClient
) : MusicService {

        private val yandexMusicApi by lazy { YandexMusicApi(httpClient) }

        override suspend fun getListeningHistory(limit: Int): List<MusicTrack> {
                return withContext(Dispatchers.Default) {
                        val token = authRepo.provideValidAccessToken()

                        if (token == null) {
                                return@withContext emptyList()
                        }

                        runCatching {
                                val response = yandexMusicApi.getLikedTracks(token)
                                Log.logger.d { "YandexMusicService: API response received, tracks count: ${response.result?.library?.tracks?.size ?: 0}" }

                                response.result?.library?.tracks.orEmpty()
                                        .mapNotNull { trackItem ->
                                                trackItem.track?.let { track ->
                                                        MusicTrack(
                                                                name = track.title,
                                                                artists = track.artists?.mapNotNull { it.name }.orEmpty(),
                                                                albumName = null,
                                                                imageUrl = null
                                                        )
                                                }
                                        }
                                        .take(limit)
                        }
                                .onFailure {
                                        Log.logger.e(it) { "YandexMusicService: failed to load liked tracks" }
                                }
                                .getOrDefault(emptyList())
                }
        }
}
