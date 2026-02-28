package com.arno.lyramp.feature.listening_history.data

import com.arno.lyramp.feature.listening_history.model.MusicTrack

internal fun MusicTrackEntity.toDomain(): MusicTrack = MusicTrack(
        id = trackId,
        albumId = albumId,
        name = name,
        artists = artists.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        albumName = albumName,
        imageUrl = imageUrl
)

internal fun MusicTrack.toEntity(): MusicTrackEntity = MusicTrackEntity(
        trackId = id,
        albumId = albumId,
        name = name,
        artists = artists.joinToString(","),
        albumName = albumName,
        imageUrl = imageUrl
)
