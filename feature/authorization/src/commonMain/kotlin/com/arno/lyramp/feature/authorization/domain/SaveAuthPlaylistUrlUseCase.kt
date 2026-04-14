package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.data.AppleAuthRepository
import com.arno.lyramp.feature.authorization.data.OptionalPlaylistRepository

class SaveAuthPlaylistUrlUseCase internal constructor(
        private val appleRepo: AppleAuthRepository,
        private val playlistRepo: OptionalPlaylistRepository,
) {
        operator fun invoke(service: MusicServiceType, url: String?) {
                when (service) {
                        MusicServiceType.APPLE -> appleRepo.savePlaylistUrl(url)
                        MusicServiceType.NONE -> playlistRepo.savePlaylistUrl(url)
                        else -> {}
                }
        }
}
