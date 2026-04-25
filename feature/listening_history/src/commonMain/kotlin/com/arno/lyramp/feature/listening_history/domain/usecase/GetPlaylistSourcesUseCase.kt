package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.authorization.domain.GetAuthPlaylistUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.data.PlaylistSourcesRepository
import com.arno.lyramp.feature.listening_history.domain.model.PlaylistSource

internal class GetPlaylistSourcesUseCase(
        private val repository: PlaylistSourcesRepository,
        private val getAuthPlaylistUrl: GetAuthPlaylistUseCase,
) {
        operator fun invoke() = buildList {
                val storedUrls = repository.getAll()
                storedUrls.forEach { url ->
                        addSource(
                                id = PlaylistSource.storedPlaylistId(url),
                                url = url,
                        )
                }
                val storedUrlSet = storedUrls.toSet()
                val legacyUrl = getAuthPlaylistUrl(MusicServiceType.NONE)
                if (legacyUrl !in storedUrlSet) {
                        addSource(
                                id = PlaylistSource.LEGACY_OPTIONAL_PLAYLIST_ID,
                                url = legacyUrl,
                        )
                }
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

        private fun sourceTitle(url: String): String = when {
                "music.apple.com" in url -> "Apple Music"
                "music.yandex" in url -> "Yandex Music"
                else -> "Плейлист"
        }
}
