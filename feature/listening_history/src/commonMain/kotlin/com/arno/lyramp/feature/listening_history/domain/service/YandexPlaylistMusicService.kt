package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.listening_history.api.ExternalPlaylistApi
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.listening_history.domain.mapper.YandexPlaylistParser
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class YandexPlaylistMusicService(
        private val url: String,
        private val htmlApi: ExternalPlaylistApi,
        private val yandexApi: YandexMusicApi,
) : MusicService {
        private val parser by lazy { YandexPlaylistParser() }

        override suspend fun getListeningHistory(limit: Int?): List<ListeningHistoryMusicTrack> {
                val html = htmlApi.loadPlaylistHtml(url)

                val ownerInfo = withContext(Dispatchers.Default) { parser.extractOwnerInfo(html) }
                if (ownerInfo != null) {
                        val apiTracks = loadFromApi(ownerInfo.uid, ownerInfo.kind)
                        if (apiTracks != null) return apiTracks
                }
                val tracks = withContext(Dispatchers.Default) { parser.parse(html) }
                return if (limit != null) tracks.take(limit) else tracks
        }

        private suspend fun loadFromApi(uid: Long, kind: Long): List<ListeningHistoryMusicTrack>? {
                return runCatching {
                        val response = yandexApi.getPlaylist(uid, kind)
                        val trackItems = response.result?.tracks.orEmpty()
                        trackItems
                                .mapNotNull { item ->
                                        item.track?.let { track ->
                                                ListeningHistoryMusicTrack(
                                                        id = track.id,
                                                        albumId = item.albumId ?: track.albums?.firstOrNull()?.id?.toString(),
                                                        name = track.title,
                                                        artists = track.artists?.mapNotNull { it.name }.orEmpty(),
                                                        albumName = track.albums?.firstOrNull()?.title,
                                                )
                                        }
                                }

                }.onFailure {
                        Log.logger.e(it) { "YandexPlaylistMusicService: API request failed, falling back to HTML" }
                }.getOrNull()
        }
}
