package com.arno.lyramp.feature.stats.background

import com.arno.lyramp.core.background.BackgroundTask
import com.arno.lyramp.core.background.TaskConstraints
import com.arno.lyramp.feature.stats.domain.usecase.ProcessTracksCefrUseCase
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException

internal class CefrStatsBackgroundTask(
        private val processTracks: ProcessTracksCefrUseCase,
) : BackgroundTask {

        override val taskId: String = TASK_ID

        override val constraints: TaskConstraints = TaskConstraints(requiresNetwork = true)

        override suspend fun execute(): Boolean = try {
                processTracks(maxTracks = DAILY_TRACK_BUDGET)
                true
        } catch (ce: CancellationException) {
                throw ce
        } catch (e: Exception) {
                Log.logger.e(e) { "CEFR stats background task failed" }
                false
        }

        companion object {
                const val TASK_ID = "lyra_cefr_stats_background"
                private const val DAILY_TRACK_BUDGET = 15
        }
}
