package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.authorization.domain.SaveAuthPlaylistUrlUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.domain.service.DynamicMusicService
import com.arno.lyramp.feature.listening_history.domain.service.MusicService

internal class SavePlaylistUrlUseCase(
        private val saveAuthPlaylistUrl: SaveAuthPlaylistUrlUseCase,
        private val dynamicMusicService: DynamicMusicService,
        private val musicServiceFactory: () -> MusicService,
) {
        operator fun invoke(url: String) {
                saveAuthPlaylistUrl(MusicServiceType.NONE, url.takeIf { it.isNotBlank() })
                dynamicMusicService.replaceDelegate(musicServiceFactory())
        }
}

