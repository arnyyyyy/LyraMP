package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.data.AppleAuthRepository
import com.arno.lyramp.feature.authorization.data.OptionalPlaylistRepository

class GetAuthPlaylistUseCase internal constructor(
        private val appleRepo: AppleAuthRepository,
        private val playlistRepo: OptionalPlaylistRepository,
) {
        operator fun invoke(service: MusicServiceType) = when (service) {
                MusicServiceType.APPLE -> appleRepo.getPlaylistUrl()
                MusicServiceType.NONE -> playlistRepo.getPlaylistUrl()
                else -> null
        }
}
