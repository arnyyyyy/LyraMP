package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.listening_history.mapper.YandexTracksMapper
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class YandexMusicService(
        private val authToken: ProvideAuthTokenUseCase,
        private val api: YandexMusicApi
) : MusicService {
        private val tracksMapper = YandexTracksMapper()

        override suspend fun getListeningHistory(limit: Int?): List<ListeningHistoryMusicTrack> =
                withContext(Dispatchers.Default) {
                        val token = authToken(MusicServiceType.YANDEX)
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

                                val mapped = enrichedTrackItems
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
                                if (limit != null) mapped.take(limit) else mapped
                        }
                                .onFailure {
                                        Log.logger.e(it) { "YandexMusicService:  Failed to load liked tracks" }
                                }
                                .getOrThrow()
                }
}
