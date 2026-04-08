package com.arno.lyramp.feature.listening_history.domain

import com.arno.lyramp.feature.authorization.repository.SpotifyAuthRepository
import com.arno.lyramp.feature.listening_history.api.SpotifyMusicApi
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log

class SpotifyMusicService(
        private val authRepo: SpotifyAuthRepository,
        private val api: SpotifyMusicApi
) : MusicService {
        override suspend fun getListeningHistory(limit: Int): List<ListeningHistoryMusicTrack> {
                val token = authRepo.provideValidAccessToken() ?: error("AAA")
                val result = runCatching { api.getLikedTracks(token, limit) }

                if (result.isSuccess) {
                        return result.getOrThrow().items.map {
                                ListeningHistoryMusicTrack(name = it.track.name, artists = it.track.artists.map { a -> a.name })
                        }.take(limit)
                }

                val refreshedToken = runCatching { authRepo.refreshAccessToken() }
                        .getOrNull() ?: error("SpotifyMusicService: failed to refresh token")

                return runCatching { api.getLikedTracks(refreshedToken, limit) }
                        .map { response ->
                                response.items.map {
                                        ListeningHistoryMusicTrack(
                                                name = it.track.name,
                                                artists = it.track.artists.map { a -> a.name }
                                        )
                                }.take(limit)
                        }
                        .onFailure { Log.logger.e(it) { "SpotifyMusicService: failed after token refresh" } }
                        .getOrThrow()
        }
}