package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.listening_history.domain.model.AlbumWithTracksResult

class GetAlbumWithTracksUseCase internal constructor(
        private val api: YandexMusicApi,
        private val provideAuthToken: ProvideAuthTokenUseCase
) {
        suspend operator fun invoke(albumId: String): AlbumWithTracksResult {
                val token = provideAuthToken(MusicServiceType.YANDEX) ?: error("No valid Yandex token")
                val response = api.getAlbumWithTracks(token, albumId)
                val album = response.result ?: error("Album not found: $albumId")
                val allTracks = album.volumes?.flatten().orEmpty()
                val artistName = album.artists?.mapNotNull { it.name }?.joinToString(", ").orEmpty()

                return AlbumWithTracksResult(
                        albumId = albumId,
                        title = album.title ?: "Album",
                        artistName = artistName,
                        coverUri = album.coverUri,
                        tracks = allTracks.mapIndexed { idx, track ->
                                AlbumWithTracksResult.TrackResult(
                                        trackIndex = idx,
                                        trackId = track.id ?: "",
                                        title = track.title,
                                        artists = track.artists?.mapNotNull { it.name }?.joinToString(", ").orEmpty()
                                )
                        }
                )
        }
}

