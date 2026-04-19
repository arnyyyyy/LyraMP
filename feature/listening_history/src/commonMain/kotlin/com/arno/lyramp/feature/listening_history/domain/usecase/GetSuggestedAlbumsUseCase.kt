package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryDao
import com.arno.lyramp.feature.listening_history.domain.model.AlbumSuggestionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class GetSuggestedAlbumsUseCase internal constructor(
        private val dao: ListeningHistoryDao // todo: repo?
) {
        suspend operator fun invoke(
                languages: Set<String> = emptySet(),
                minLiked: Int = 2
        ): List<AlbumSuggestionResult> = withContext(Dispatchers.IO) {
                val tracks = dao.getAll()

                tracks
                        .filter { !it.albumId.isNullOrBlank() && (languages.isEmpty() || it.language == null || it.language in languages) }
                        .groupBy { it.albumId!! }
                        .filter { it.value.size >= minLiked }
                        .map { (albumId, albumTracks) ->
                                val first = albumTracks.first()
                                val albumTitle = albumTracks
                                        .firstNotNullOfOrNull { it.albumName?.takeIf { n -> n.isNotBlank() } }
                                        ?: first.artists
                                AlbumSuggestionResult(
                                        albumId = albumId,
                                        albumTitle = albumTitle,
                                        artistName = first.artists,
                                        imageUrl = first.imageUrl,
                                        likedTrackCount = albumTracks.size
                                )
                        }
                        .sortedByDescending { it.likedTrackCount }
        }
}
