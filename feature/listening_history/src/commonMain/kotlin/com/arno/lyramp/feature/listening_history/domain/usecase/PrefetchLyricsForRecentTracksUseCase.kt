package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.feature.lyrics.domain.CheckSyncedLyricsUseCase
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

internal class PrefetchLyricsForRecentTracksUseCase(
        private val repository: ListeningHistoryRepository,
        private val checkSyncedLyrics: CheckSyncedLyricsUseCase,
) {
        suspend operator fun invoke(maxTracks: Int = DEFAULT_MAX_TRACKS): Int {
                val candidates = repository.getTracksForLyricsPrefetch(maxTracks)
                if (candidates.isEmpty()) return 0

                var processed = 0
                for (track in candidates) {
                        try {
                                currentCoroutineContext().ensureActive()
                                val hasSyncedLyrics = checkSyncedLyrics(track.artist, track.name, track.trackId)
                                repository.setLyricsPrefetchStatus(track.localId, hasSyncedLyrics)
                                processed++
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (e: Exception) {
                                Log.logger.w(e) { "Lyrics prefetch failed for ${track.name}" }
                        }
                }
                return processed
        }

        companion object {
                const val DEFAULT_MAX_TRACKS = 5
        }
}
