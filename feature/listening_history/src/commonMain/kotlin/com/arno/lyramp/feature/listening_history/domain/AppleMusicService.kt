package com.arno.lyramp.feature.listening_history.domain

import com.arno.lyramp.feature.authorization.repository.AppleAuthRepository
import com.arno.lyramp.feature.listening_history.api.AppleMusicApi
import com.arno.lyramp.feature.listening_history.mapper.AppleMusicParser
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log

class AppleMusicService(
        private val authRepo: AppleAuthRepository,
        private val api: AppleMusicApi
) : MusicService {
        private val parser by lazy { AppleMusicParser() }

        override suspend fun getListeningHistory(limit: Int): List<ListeningHistoryMusicTrack> {
                val playlist = authRepo.getPlaylistUrl() ?: error("Failed to get Apple Music playlist URL")

                return runCatching {
                        api.loadPlaylistHtml(playlist).let(parser::parse).take(limit)
                }.onFailure { Log.logger.e(it) { "AppleMusicService: failed to load playlist" } }.getOrThrow()
        }
}
