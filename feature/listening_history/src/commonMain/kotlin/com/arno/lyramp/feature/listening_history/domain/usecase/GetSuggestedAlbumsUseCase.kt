package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.feature.listening_history.domain.model.AlbumSuggestionResult

class GetSuggestedAlbumsUseCase internal constructor(
        private val repository: ListeningHistoryRepository,
) {
        suspend operator fun invoke(
                languages: Set<String> = emptySet(),
                minLiked: Int = 2,
        ) = repository.getAllTracks()
                .filter { track ->
                        !track.albumId.isNullOrBlank() &&
                            (languages.isEmpty() || track.language == null || track.language in languages)
                }
                .groupBy { it.albumId!! }
                .filter { it.value.size >= minLiked }
                .map { (albumId, albumTracks) ->
                        val first = albumTracks.first()
                        val albumTitle = albumTracks
                                .firstNotNullOfOrNull { it.albumName?.takeIf { n -> n.isNotBlank() } }
                                ?: first.artists.joinToString(", ")
                        AlbumSuggestionResult(
                                albumId = albumId,
                                albumTitle = albumTitle,
                                artistName = first.artists.joinToString(", "),
                                imageUrl = first.imageUrl,
                                likedTrackCount = albumTracks.size,
                        )
                }
                .sortedByDescending { it.likedTrackCount }
}
