package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.listening_history.api.ExternalPlaylistApi
import com.arno.lyramp.feature.listening_history.domain.mapper.AppleMusicParser
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AppleMusicService(
        private val url: String,
        private val api: ExternalPlaylistApi,
) : MusicService {
        private val parser by lazy { AppleMusicParser() }

        override suspend fun getListeningHistory(limit: Int?): List<ListeningHistoryMusicTrack> {
                if (url.isBlank()) {
                        Log.logger.w { "AppleMusicService: no playlist URL for source" }
                        return emptyList()
                }

                return runCatching {
                        val html = api.loadPlaylistHtml(url)
                        val tracks = withContext(Dispatchers.Default) {
                                parser.parse(html).map { track ->
                                        track.copy(id = track.id ?: track.syntheticId())
                                }
                        }
                        if (limit != null) tracks.take(limit) else tracks
                }.onFailure { Log.logger.e(it) { "AppleMusicService: failed to load playlist $url" } }.getOrThrow()
        }

        private fun ListeningHistoryMusicTrack.syntheticId(): String = "$name||${artists.joinToString(",")}"
}
