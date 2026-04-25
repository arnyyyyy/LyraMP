package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.core.data.PlaylistSourcesRepository

internal class SavePlaylistUrlUseCase(
        private val repository: PlaylistSourcesRepository,
) {
        operator fun invoke(url: String) {
                repository.add(url)
        }
}

