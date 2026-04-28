package com.arno.lyramp.feature.listening_history.background

import com.arno.lyramp.core.background.BackgroundTask
import com.arno.lyramp.core.background.TaskConstraints
import com.arno.lyramp.feature.listening_history.domain.usecase.PrefetchLyricsForRecentTracksUseCase
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException

internal class LyricsPrefetchBackgroundTask(
        private val prefetchLyrics: PrefetchLyricsForRecentTracksUseCase,
) : BackgroundTask {

        override val taskId: String = TASK_ID

        override val constraints: TaskConstraints = TaskConstraints(requiresNetwork = true)

        override suspend fun execute(): Boolean = try {
                prefetchLyrics(maxTracks = BATCH_SIZE)
                true
        } catch (ce: CancellationException) {
                throw ce
        } catch (e: Exception) {
                Log.logger.e(e) { "Lyrics prefetch background task failed" }
                false
        }

        companion object {
                const val TASK_ID = "lyra_lyrics_prefetch_background"
                private const val BATCH_SIZE = 5
        }
}

