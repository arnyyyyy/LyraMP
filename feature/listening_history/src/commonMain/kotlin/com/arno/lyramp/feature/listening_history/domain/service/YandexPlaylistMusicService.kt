package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.authorization.domain.GetAuthPlaylistUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.api.AppleMusicApi
import com.arno.lyramp.feature.listening_history.mapper.YandexPlaylistParser
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log

internal class YandexPlaylistMusicService(
        private val getPlaylistUrl: GetAuthPlaylistUseCase,
        private val api: AppleMusicApi,
) : MusicService {
        private val parser by lazy { YandexPlaylistParser() }

        override suspend fun getListeningHistory(limit: Int): List<ListeningHistoryMusicTrack> {
                val url = getPlaylistUrl(MusicServiceType.NONE) ?: error("No Yandex playlist URL")

                return runCatching {
                        api.loadPlaylistHtml(url).let(parser::parse).take(limit)
                }.onFailure {
                        Log.logger.e(it) { "YandexPlaylistMusicService: failed to load playlist" }
                }.getOrThrow()
        }
}
