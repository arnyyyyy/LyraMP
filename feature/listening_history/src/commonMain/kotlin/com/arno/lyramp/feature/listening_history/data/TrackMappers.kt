package com.arno.lyramp.feature.listening_history.data

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack

internal fun ListeningHistoryTrackEntity.toDomain(): ListeningHistoryMusicTrack = ListeningHistoryMusicTrack(
        id = trackId,
        albumId = albumId,
        name = name,
        artists = artists.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        albumName = albumName,
        imageUrl = imageUrl,
        language = language
)

internal fun ListeningHistoryMusicTrack.toEntity(): ListeningHistoryTrackEntity = ListeningHistoryTrackEntity(
        trackId = id,
        albumId = albumId,
        name = name,
        artists = artists.joinToString(","),
        albumName = albumName,
        imageUrl = imageUrl,
        language = language
)
