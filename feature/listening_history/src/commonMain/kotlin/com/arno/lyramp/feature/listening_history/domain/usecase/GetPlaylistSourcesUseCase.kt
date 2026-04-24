package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.authorization.domain.GetAuthPlaylistUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.domain.model.PlaylistSource

internal class GetPlaylistSourcesUseCase(
        private val getAuthPlaylistUrl: GetAuthPlaylistUseCase,
) {
        operator fun invoke() = buildList {
                addSource(
                        id = PlaylistSource.OPTIONAL_PLAYLIST_ID,
                        url = getAuthPlaylistUrl(MusicServiceType.NONE),
                )
                addSource(
                        id = PlaylistSource.APPLE_PLAYLIST_ID,
                        url = getAuthPlaylistUrl(MusicServiceType.APPLE),
                )
        }


        private fun MutableList<PlaylistSource>.addSource(id: String, url: String?) {
                val cleanUrl = url?.takeIf { it.isNotBlank() } ?: return
                add(
                        PlaylistSource(
                                id = id,
                                title = sourceTitle(cleanUrl),
                                url = cleanUrl,
                        )
                )
        }

        private fun sourceTitle(url: String): String = when { // AAA TODO НАЗВАНИЕ ПЛЕЙЛИСТА НО МНЕ ЛЕНЬ ПАРСИТЬ
                "music.apple.com" in url -> "Apple Music"
                "music.yandex" in url -> "Yandex Music"
                else -> "Плейлист"
        }
}
