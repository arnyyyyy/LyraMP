package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryDao
import com.arno.lyramp.feature.lyrics.domain.CheckSyncedLyricsUseCase
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

internal class PrefetchLyricsForRecentTracksUseCase(
        private val dao: ListeningHistoryDao,
        private val checkSyncedLyrics: CheckSyncedLyricsUseCase,
) {
        suspend operator fun invoke(maxTracks: Int = DEFAULT_MAX_TRACKS): Int {
                val candidates = dao.getTracksForLyricsPrefetch(maxTracks)
                if (candidates.isEmpty()) return 0

                var processed = 0
                for (track in candidates) {
                        try {
                                currentCoroutineContext().ensureActive()
                                val artist = track.artists.split(",").firstOrNull()?.trim().orEmpty()
                                val isSynced = checkSyncedLyrics(artist, track.name, track.trackId)
                                val status = if (isSynced) STATUS_SYNCED else STATUS_NOT_SYNCED
                                dao.setLyricsPrefetchStatus(track.localId, status)
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
                const val STATUS_SYNCED = 2
                const val STATUS_NOT_SYNCED = 3
        }
}
