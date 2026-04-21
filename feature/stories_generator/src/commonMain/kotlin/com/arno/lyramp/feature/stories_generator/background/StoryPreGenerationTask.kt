package com.arno.lyramp.feature.stories_generator.background

import com.arno.lyramp.core.background.BackgroundTask
import com.arno.lyramp.core.background.TaskConstraints
import com.arno.lyramp.feature.stories_generator.data.GeneratedStoryRepository
import com.arno.lyramp.feature.stories_generator.domain.StoryGenerationService
import com.arno.lyramp.util.Log

internal class StoryCatalogBackgroundTask(
        private val repository: GeneratedStoryRepository,
        private val generationService: StoryGenerationService,
) : BackgroundTask {

        override val taskId = TASK_ID

        override val constraints = TaskConstraints(requiresNetwork = false)

        override suspend fun execute(): Boolean {
                val unread = repository.countUnread()
                if (unread >= StoryGenerationService.MIN_UNREAD_TARGET) return true

                return try {
                        generationService.generateAndSaveOne() ?: Log.logger.e { "AAAAAA gen skipped" }
                        true
                } catch (e: Exception) {
                        Log.logger.e(e) { "AAAAAFailed to generate catalog story" }
                        false
                }
        }

        companion object {
                const val TASK_ID = "lyra_story_catalog"
        }
}
