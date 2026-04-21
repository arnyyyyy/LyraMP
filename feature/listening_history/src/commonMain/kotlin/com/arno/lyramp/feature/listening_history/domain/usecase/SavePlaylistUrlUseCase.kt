package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.authorization.domain.SaveAuthPlaylistUrlUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType

internal class SavePlaylistUrlUseCase(
        private val saveAuthPlaylistUrl: SaveAuthPlaylistUrlUseCase,
) {
        operator fun invoke(url: String) {
                saveAuthPlaylistUrl(MusicServiceType.NONE, url.takeIf { it.isNotBlank() })
        }
}

