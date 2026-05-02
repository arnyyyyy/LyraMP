package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.feature.lyrics.domain.CheckSyncedLyricsUseCase
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

// TODO: потом при подгрузке текста для трека из Apple при внимании пользователя к нему
// если не префетчили трек или не проверяли, что он есть в ЯМ, делаем это
internal class PrefetchLyricsForRecentTracksUseCase(
        private val repository: ListeningHistoryRepository,
        private val checkSyncedLyrics: CheckSyncedLyricsUseCase,
) {
        suspend operator fun invoke(maxTracks: Int = DEFAULT_MAX_TRACKS) {
                val candidates = repository.getTracksForLyricsPrefetch(maxTracks)
                if (candidates.isEmpty()) return

                for (track in candidates) {
                        try {
                                currentCoroutineContext().ensureActive()
                                val hasSyncedLyrics = checkSyncedLyrics(track.artist, track.name, track.trackId)
                                repository.setLyricsPrefetchStatus(track.localId, hasSyncedLyrics)
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (e: Exception) {
                                Log.logger.w(e) { "Lyrics prefetch failed for ${track.name}" }
                        }
                }
                return
        }

        private companion object {
                const val DEFAULT_MAX_TRACKS = 5
        }
}
