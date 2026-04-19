package com.arno.lyramp.feature.album_suggestion.domain.model

import com.arno.lyramp.feature.album_suggestion.data.AlbumProgressEntity
import com.arno.lyramp.feature.album_suggestion.data.AlbumProgressInfo

internal fun AlbumProgressEntity.toInfo() = AlbumProgressInfo(
        albumId = albumId,
        albumTitle = albumTitle,
        artistName = artistName,
        coverUri = coverUri,
        totalTracks = totalTracks,
        wordsExtracted = wordsExtracted,
        extractedLanguage = extractedLanguage,
        extractedLevels = extractedLevels
)

internal fun AlbumProgressInfo.toEntity() = AlbumProgressEntity(
        albumId = albumId,
        albumTitle = albumTitle,
        artistName = artistName,
        coverUri = coverUri,
        totalTracks = totalTracks,
        wordsExtracted = wordsExtracted,
        extractedLanguage = extractedLanguage,
        extractedLevels = extractedLevels
)
