package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.core.data.PlaylistSourcesRepository
import com.arno.lyramp.feature.authorization.data.AuthSelectionStorage
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType

class CompleteNoAuthOnboardingUseCase internal constructor(
        private val playlistSources: PlaylistSourcesRepository,
) {
        operator fun invoke(playlistUrl: String?) {
                playlistUrl?.takeIf { it.isNotBlank() }?.let(playlistSources::add)
                AuthSelectionStorage.lastAuthorizedService = MusicServiceType.NONE.name
        }
}
