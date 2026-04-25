package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.listening_history.api.AppleMusicApi
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.listening_history.mapper.YandexPlaylistParser
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log

internal class YandexPlaylistMusicService(
        private val playlistUrlProvider: () -> String?,
        private val htmlApi: AppleMusicApi,
        private val yandexApi: YandexMusicApi,
) : MusicService {
        private val parser by lazy { YandexPlaylistParser() }

        override suspend fun getListeningHistory(limit: Int): List<ListeningHistoryMusicTrack> {
                val url = playlistUrlProvider() ?: error("No Yandex playlist URL")
                val html = htmlApi.loadPlaylistHtml(url)

                val ownerInfo = parser.extractOwnerInfo(html)
                if (ownerInfo != null) {
                        val apiTracks = loadFromApi(ownerInfo.uid, ownerInfo.kind, limit)
                        if (apiTracks != null) return apiTracks
                }
                val tracks = parser.parse(html).take(limit)
                return tracks
        }

        private suspend fun loadFromApi(uid: Long, kind: Long, limit: Int): List<ListeningHistoryMusicTrack>? {
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
                                .take(limit)
                }.onFailure {
                        Log.logger.e(it) { "YandexPlaylistMusicService: API request failed, falling back to HTML" }
                }.getOrNull()
        }
}
