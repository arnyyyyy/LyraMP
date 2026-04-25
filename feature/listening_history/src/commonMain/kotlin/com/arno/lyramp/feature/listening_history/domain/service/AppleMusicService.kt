package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.listening_history.api.AppleMusicApi
import com.arno.lyramp.feature.listening_history.mapper.AppleMusicParser
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log

internal class AppleMusicService(
        private val playlistUrlProvider: () -> String?,
        private val api: AppleMusicApi,
) : MusicService {
        private val parser by lazy { AppleMusicParser() }

        override suspend fun getListeningHistory(limit: Int): List<ListeningHistoryMusicTrack> {
                val playlist = playlistUrlProvider()
                if (playlist.isNullOrBlank()) {
                        Log.logger.w { "AppleMusicService: no playlist URL for source" }
                        return emptyList()
                }

                return runCatching {
                        val html = api.loadPlaylistHtml(playlist)
                        val tracks = parser.parse(html).take(limit)
                        tracks
                }.onFailure { Log.logger.e(it) { "AppleMusicService: failed to load playlist $playlist" } }.getOrThrow()
        }
}
