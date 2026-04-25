package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.authorization.domain.SaveAuthPlaylistUrlUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.feature.listening_history.data.PlaylistSourcesRepository
import com.arno.lyramp.feature.listening_history.domain.model.PlaylistSource

internal class RemovePlaylistSourceUseCase(
        private val listeningHistoryRepository: ListeningHistoryRepository,
        private val repository: PlaylistSourcesRepository,
        private val saveAuthPlaylistUrl: SaveAuthPlaylistUrlUseCase,
) {
        suspend operator fun invoke(sourceId: String) {
                listeningHistoryRepository.deleteTracksBySourceId(sourceId)
                when (sourceId) {
                        PlaylistSource.LEGACY_OPTIONAL_PLAYLIST_ID -> saveAuthPlaylistUrl(MusicServiceType.NONE, null)
                        PlaylistSource.APPLE_PLAYLIST_ID -> saveAuthPlaylistUrl(MusicServiceType.APPLE, null)
                        else -> sourceId.removePrefix(STORED_PREFIX)
                                .takeIf { it != sourceId }
                                ?.let(repository::remove)
                }
        }

        private companion object {
                const val STORED_PREFIX = "stored_playlist:"
        }
}
