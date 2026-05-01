package com.arno.lyramp.feature.listening_history.presentation

import com.arno.lyramp.feature.listening_history.domain.model.PlaylistSource
import com.arno.lyramp.feature.listening_history.domain.service.SOURCE_YANDEX_LIKES
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack

data class FolderItem(
        val id: String?,
        val emoji: String,
        val title: String,
        val count: Int,
)

internal class ListeningHistoryUiStateBuilder {
        fun availableLanguages(
                tracks: List<ListeningHistoryMusicTrack>,
                learningLanguages: Set<String>,
        ): List<String> {
                val trackCountByLanguage = tracks.groupBy { it.language }.mapValues { it.value.size }
                return if (learningLanguages.isNotEmpty()) {
                        learningLanguages.filter { (trackCountByLanguage[it] ?: 0) > MIN_TRACKS_PER_LANGUAGE }.sorted()
                } else {
                        tracks.mapNotNull { it.language }
                                .distinct()
                                .filter { (trackCountByLanguage[it] ?: 0) > MIN_TRACKS_PER_LANGUAGE }
                                .sorted()
                }
        }

        fun filteredTracks(
                tracks: List<ListeningHistoryMusicTrack>,
                selectedLanguage: String?,
                selectedSourceId: String?,
                searchQuery: String,
        ): List<ListeningHistoryMusicTrack> {
                var list = tracks
                selectedLanguage?.let { lang -> list = list.filter { it.language == lang } }

                selectedSourceId?.let { sourceId ->
                        list = if (sourceId == SOURCE_FILTER_MANUAL) {
                                list.filter { it.sourceId.isNullOrEmpty() }
                        } else {
                                list.filter { it.sourceId == sourceId }
                        }
                }

                val needle = searchQuery.trim().lowercase()
                if (needle.isNotEmpty()) {
                        list = list.filter { track ->
                                track.name.lowercase().contains(needle) ||
                                    track.artists.any { it.lowercase().contains(needle) }
                        }
                }
                return list
        }

        fun stateFor(
                filteredTracks: List<ListeningHistoryMusicTrack>,
                currentState: ListeningHistoryUiState,
                showEmptyState: Boolean,
        ): ListeningHistoryUiState {
                return if (filteredTracks.isEmpty()) {
                        if (showEmptyState) ListeningHistoryUiState.Empty else currentState
                } else {
                        ListeningHistoryUiState.Success(filteredTracks)
                }
        }

        fun folderItems(
                tracks: List<ListeningHistoryMusicTrack>,
                playlistSources: List<PlaylistSource>,
        ): List<FolderItem> {
                val items = mutableListOf(
                        FolderItem(
                                id = null,
                                emoji = "🎵",
                                title = "Все треки",
                                count = tracks.size,
                        )
                )

                val yandexCount = tracks.count { it.sourceId == SOURCE_YANDEX_LIKES }
                if (yandexCount > 0) {
                        items += FolderItem(
                                id = SOURCE_YANDEX_LIKES,
                                emoji = "❤️",
                                title = "Яндекс Избранное",
                                count = yandexCount,
                        )
                }

                playlistSources.forEach { source ->
                        val count = tracks.count { it.sourceId == source.id }
                        if (count > 0) {
                                items += FolderItem(
                                        id = source.id,
                                        emoji = sourceEmoji(source.url),
                                        title = source.title,
                                        count = count,
                                )
                        }
                }

                val manualCount = tracks.count { it.sourceId.isNullOrEmpty() }
                if (manualCount > 0) {
                        items += FolderItem(
                                id = SOURCE_FILTER_MANUAL,
                                emoji = "✍️",
                                title = "Добавленные",
                                count = manualCount,
                        )
                }

                return items
        }

        private fun sourceEmoji(url: String): String = when {
                "apple.com" in url -> "🍎"
                "yandex" in url -> "🎵"
                else -> "📁"
        }

        private companion object {
                const val MIN_TRACKS_PER_LANGUAGE = 3
        }
}

internal const val SOURCE_FILTER_MANUAL = "__manual__"
