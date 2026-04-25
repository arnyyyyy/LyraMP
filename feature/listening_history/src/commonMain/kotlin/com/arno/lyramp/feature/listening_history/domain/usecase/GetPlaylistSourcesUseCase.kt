package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.core.data.PlaylistSourcesRepository
import com.arno.lyramp.feature.listening_history.domain.model.PlaylistSource

internal class GetPlaylistSourcesUseCase(
        private val repository: PlaylistSourcesRepository,
) {
        operator fun invoke() = buildList {
                repository.getAll().forEach { url ->
                        addSource(
                                id = PlaylistSource.storedPlaylistId(url),
                                url = url,
                        )
                }
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
