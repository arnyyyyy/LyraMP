package com.arno.lyramp.feature.listening_history.domain

import com.arno.lyramp.feature.authorization.repository.YandexAuthRepository
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.listening_history.mapper.YandexTracksMapper
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

 class YandexMusicService(
        private val authRepo: YandexAuthRepository,
        private val api: YandexMusicApi
) : MusicService {
        private val tracksMapper = YandexTracksMapper()

        override suspend fun getListeningHistory(limit: Int): List<ListeningHistoryMusicTrack> =
                withContext(Dispatchers.Default) {
                        val token = authRepo.provideValidAccessToken()
                        if (token == null) error("YandexMusicService: No valid access token available!")

                        runCatching {
                                val accountStatus = api.getAccountStatus(token)
                                val userId = accountStatus.result?.account?.uid ?: error("User ID not found")

                                val likedTracksResponse = api.getLikedTracks(token, userId)
                                val basicTrackItems = likedTracksResponse.result?.library?.tracks.orEmpty()
                                if (basicTrackItems.isEmpty()) return@runCatching emptyList()

                                val trackIds = tracksMapper.buildTrackIdsString(basicTrackItems)
                                if (trackIds.isBlank()) return@runCatching emptyList()

                                val fullTracksResponse = api.getFullTracksInfo(token, trackIds)
                                val fullTracks = fullTracksResponse.result.orEmpty()

                                val enrichedTrackItems =
                                        tracksMapper.enrichTracksWithFullInfo(basicTrackItems, fullTracks)

                                enrichedTrackItems
                                        .mapNotNull { trackItem ->
                                                trackItem.track?.let { track ->
                                                        ListeningHistoryMusicTrack(
                                                                id = track.id,
                                                                albumId = trackItem.albumId ?: track.albums?.firstOrNull()?.id?.toString(),
                                                                name = track.title,
                                                                artists = track.artists?.mapNotNull { it.name }.orEmpty(),
                                                                albumName = track.albums?.firstOrNull()?.title,
                                                                imageUrl = null
                                                        )
                                                }
                                        }
                                        .take(limit)
                        }
                                .onFailure {
                                        Log.logger.e(it) { "YandexMusicService:  Failed to load liked tracks" }
                                }
                                .getOrThrow()
                }
}
