package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.core.data.PlaylistSourcesRepository
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository

internal class RemovePlaylistSourceUseCase(
        private val listeningHistoryRepository: ListeningHistoryRepository,
        private val playlistSourcesRepository: PlaylistSourcesRepository,
) {
        suspend operator fun invoke(sourceId: String) {
                listeningHistoryRepository.deleteTracksBySourceId(sourceId)
                sourceId.removePrefix(STORED_PREFIX)
                        .takeIf { it != sourceId }
                        ?.let(playlistSourcesRepository::remove)
        }

        private companion object {
                const val STORED_PREFIX = "stored_playlist:"
        }
}
