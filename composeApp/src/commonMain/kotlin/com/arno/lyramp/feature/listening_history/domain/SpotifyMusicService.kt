package com.arno.lyramp.feature.listening_history.domain

import com.arno.lyramp.feature.authorization.repository.AuthApiRepository
import com.arno.lyramp.feature.listening_history.api.SpotifyMusicApi
import com.arno.lyramp.feature.listening_history.model.MusicTrack
import com.arno.lyramp.util.Log

internal class SpotifyMusicService(
        private val authRepo: AuthApiRepository,
        private val api: SpotifyMusicApi
) : MusicService {

        override suspend fun getListeningHistory(limit: Int): List<MusicTrack> {
                var token = authRepo.provideValidAccessToken() ?: return emptyList()

                try {
                        return api.savedTracks(token, limit).items.map { savedItem ->
                                MusicTrack(
                                        name = savedItem.track.name,
                                        artists = savedItem.track.artists.map { it.name }
                                )
                        }
                } catch (_: Throwable) {
                        val refreshedToken = try {
                                authRepo.refreshAccessToken()
                        } catch (e: Throwable) {
                                Log.logger.e(e) { "SpotifyMusicService: refresh failed" }
                                null
                        }

                        if (refreshedToken != null) {
                                token = refreshedToken
                                return try {
                                        api.savedTracks(token, limit).items.map { savedItem ->
                                                MusicTrack(
                                                        name = savedItem.track.name,
                                                        artists = savedItem.track.artists.map { it.name }
                                                )
                                        }
                                } catch (e: Throwable) {
                                        Log.logger.e(e) { "SpotifyMusicService: failed to load tracks after refresh" }
                                        emptyList()
                                }
                        }

                        return emptyList()
                }
        }

        override fun isAuthorized(): Boolean {
                return !authRepo.getAccessToken().isNullOrBlank() || !authRepo.getRefreshToken()
                        .isNullOrBlank()
        }
}