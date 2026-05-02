package com.arno.lyramp.feature.listening_history.domain.mapper

import com.arno.lyramp.feature.listening_history.api.YandexTrack
import com.arno.lyramp.feature.listening_history.api.YandexTrackItem

internal class YandexTracksMapper {
        fun enrichTracksWithFullInfo(
                basicTrackItems: List<YandexTrackItem>,
                fullTracks: List<YandexTrack>
        ): List<YandexTrackItem> {
                val trackInfoMap = buildTrackInfoMap(basicTrackItems)
                return mapToEnrichedTrackItems(fullTracks, trackInfoMap)
        }

        fun buildTrackIdsString(trackItems: List<YandexTrackItem>): String {
                return trackItems.mapNotNull { item ->
                        val trackId = item.id ?: item.track?.id
                        val albumId = item.albumId ?: item.track?.albums?.firstOrNull()?.id?.toString()

                        trackId?.let { id ->
                                if (albumId != null) "$id:$albumId"
                                else id
                        }
                }.joinToString(",")
        }

        private fun buildTrackInfoMap(
                trackItems: List<YandexTrackItem>
        ): Map<String, Pair<String?, YandexTrackItem>> {
                return trackItems.mapNotNull { item ->
                        val trackId = item.id ?: item.track?.id
                        val albumId = item.albumId ?: item.track?.albums?.firstOrNull()?.id?.toString()

                        trackId?.let { id ->
                                id to Pair(albumId, item)
                        }
                }.toMap()
        }

        private fun mapToEnrichedTrackItems(
                fullTracks: List<YandexTrack>,
                trackInfoMap: Map<String, Pair<String?, YandexTrackItem>>
        ): List<YandexTrackItem> {
                val result = fullTracks.map { track ->
                        val trackId = track.id
                        val savedInfo = trackId?.let { trackInfoMap[it] }
                        val albumId = savedInfo?.first ?: track.albums?.firstOrNull()?.id?.toString()

                        YandexTrackItem(id = trackId, albumId = albumId, track = track)
                }
                return result
        }
}
