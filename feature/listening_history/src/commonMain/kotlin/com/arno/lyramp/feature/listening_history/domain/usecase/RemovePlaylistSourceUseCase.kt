package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.authorization.domain.SaveAuthPlaylistUrlUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.domain.model.PlaylistSource

internal class RemovePlaylistSourceUseCase(
        private val savePlaylistUrl: SavePlaylistUrlUseCase,
        private val saveAuthPlaylistUrl: SaveAuthPlaylistUrlUseCase,
) {
        operator fun invoke(sourceId: String) {
                when (sourceId) {
                        PlaylistSource.OPTIONAL_PLAYLIST_ID -> savePlaylistUrl(null)
                        PlaylistSource.APPLE_PLAYLIST_ID -> saveAuthPlaylistUrl(MusicServiceType.APPLE, null)
                }
        }
}
