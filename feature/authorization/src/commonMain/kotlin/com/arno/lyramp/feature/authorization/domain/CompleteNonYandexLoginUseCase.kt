package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.core.data.PlaylistSourcesRepository
import com.arno.lyramp.feature.authorization.data.AuthSelectionStorage
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType

class CompleteNonYandexLoginUseCase internal constructor(
        private val playlistSources: PlaylistSourcesRepository,
        private val authSelectionStorage: AuthSelectionStorage,
) {
        operator fun invoke(playlistUrl: String?) {
                playlistUrl?.takeIf { it.isNotBlank() }?.let(playlistSources::add)
                authSelectionStorage.lastAuthorizedService = MusicServiceType.NONE.name
        }
}
