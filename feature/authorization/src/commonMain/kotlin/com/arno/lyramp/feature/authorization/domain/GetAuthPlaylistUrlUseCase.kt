package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.repository.AppleAuthRepository
import com.arno.lyramp.feature.authorization.repository.PlaylistUrlRepository

class GetAuthPlaylistUrlUseCase internal constructor(
        private val appleRepo: AppleAuthRepository,
        private val playlistRepo: PlaylistUrlRepository,
) {
        operator fun invoke(service: MusicServiceType) = when (service) {
                MusicServiceType.APPLE -> appleRepo.getPlaylistUrl()
                MusicServiceType.NONE -> playlistRepo.getPlaylistUrl()
                else -> null
        }
}
