package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.core.model.LyraLang.foldLatinDiacritics
import com.arno.lyramp.core.util.replaceNonLetterDigitWithSpace
import com.arno.lyramp.feature.authorization.domain.ProvideAuthTokenUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.api.YandexMusicApi
import com.arno.lyramp.feature.listening_history.api.YandexTrack
import com.arno.lyramp.feature.listening_history.data.TrackResolutionCandidate
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.util.Log

internal class ResolveRemainingsByYandexUseCase(
        private val repository: ListeningHistoryRepository,
        private val api: YandexMusicApi,
        private val provideAuthToken: ProvideAuthTokenUseCase,
) {
        suspend operator fun invoke() {
                val token = provideAuthToken(MusicServiceType.YANDEX) ?: return

                val allCandidates = repository.getTracksMissingYandexIds()
                val tracks = allCandidates.take(MAX_TRACKS_TO_RESOLVE)

                tracks.forEach { track ->
                        repository.markYandexResolveAttempted(track.localId)
                        val match = runCatching {
                                val query = buildQuery(track)
                                val results = api.searchTracks(token = token, text = query).result?.tracks?.results.orEmpty()
                                results.bestMatchFor(track)
                        }.onFailure { e ->
                                Log.logger.w(e) { "Failed to resolve manual track in Yandex: ${track.name}" }
                        }.getOrNull()

                        if (match != null && match.id != null) {
                                repository.resolveTrackByLocalId(
                                        localId = track.localId,
                                        newTrackId = match.id,
                                        albumId = match.albums?.firstOrNull()?.id?.toString(),
                                        albumName = match.albums?.firstOrNull()?.title,
                                        artists = track.artists,
                                )
                        }
                }
                return
        }

        private fun buildQuery(track: TrackResolutionCandidate): String {
                val artist = track.artists.firstOrNull().orEmpty()
                return listOf(track.name, artist).filter { it.isNotBlank() }.joinToString(" ")
        }

        private fun List<YandexTrack>.bestMatchFor(track: TrackResolutionCandidate): YandexTrack? {
                val expectedTitle = track.name.normalized()
                val expectedArtists = track.artists.map { it.normalized() }.filter { it.isNotBlank() }
                if (expectedTitle.isBlank() || expectedArtists.isEmpty()) return null

                return firstOrNull { candidate ->
                        candidate.title.normalized() == expectedTitle &&
                            candidate.matchesAnyExpectedArtist(expectedArtists)
                }
        }

        private fun YandexTrack.matchesAnyExpectedArtist(expectedArtists: List<String>): Boolean {
                val candidateArtists = artists.orEmpty()
                        .map { it.name.orEmpty().normalized() }
                        .filter { it.isNotBlank() }

                return expectedArtists.any { expected ->
                        candidateArtists.any { candidate ->
                                candidate == expected || candidate.contains(expected) || expected.contains(candidate)
                        }
                }
        }

        private fun String.normalized(): String =
                lowercase()
                        .foldLatinDiacritics()
                        .replaceNonLetterDigitWithSpace()
                        .replace(Regex("\\s+"), " ")
                        .trim()

        private companion object Companion {
                const val MAX_TRACKS_TO_RESOLVE = 15
        }
}
