package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.authorization.domain.GetAuthPlaylistUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType

internal class GetPlaylistUrlUseCase(
        private val getAuthPlaylistUrl: GetAuthPlaylistUseCase,
) {
        operator fun invoke() = getAuthPlaylistUrl(MusicServiceType.NONE).orEmpty()
}
