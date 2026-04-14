package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.repository.AppleAuthRepository
import com.arno.lyramp.feature.authorization.repository.PlaylistUrlRepository

class SaveAuthPlaylistUrlUseCase internal constructor(
        private val appleRepo: AppleAuthRepository,
        private val playlistRepo: PlaylistUrlRepository,
) {
        operator fun invoke(service: MusicServiceType, url: String?) {
                when (service) {
                        MusicServiceType.APPLE -> appleRepo.savePlaylistUrl(url)
                        MusicServiceType.NONE -> playlistRepo.savePlaylistUrl(url)
                        else -> {}
                }
        }
}
