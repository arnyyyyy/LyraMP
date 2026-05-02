package com.arno.lyramp.feature.listening_history.domain.model

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryTrackEntity
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack

internal fun ListeningHistoryTrackEntity.toDomain() = ListeningHistoryMusicTrack(
        id = trackId,
        albumId = albumId,
        name = name,
        artists = artists.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        albumName = albumName,
        imageUrl = imageUrl,
        language = language,
        sourceId = sourceId,
        hasSyncedLyrics = lyricsPrefetchStatus == 2,
)

internal fun ListeningHistoryMusicTrack.toEntity() = ListeningHistoryTrackEntity(
        trackId = id,
        albumId = albumId,
        name = name,
        artists = artists.joinToString(","),
        albumName = albumName,
        imageUrl = imageUrl,
        language = language,
        sourceId = sourceId,
)
