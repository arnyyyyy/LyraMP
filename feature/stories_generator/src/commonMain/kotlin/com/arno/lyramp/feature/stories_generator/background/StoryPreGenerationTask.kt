package com.arno.lyramp.feature.stories_generator.background

import com.arno.lyramp.core.background.BackgroundTask
import com.arno.lyramp.core.background.TaskConstraints
import com.arno.lyramp.feature.stories_generator.data.GeneratedStoryRepository
import com.arno.lyramp.feature.stories_generator.domain.StoryGenerationService
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import com.arno.lyramp.util.Log

internal class StoryCatalogBackgroundTask(
        private val repository: GeneratedStoryRepository,
        private val generationService: StoryGenerationService,
        private val getSelectedLanguage: GetSelectedLanguageUseCase,
) : BackgroundTask {

        override val taskId = TASK_ID

        override val constraints = TaskConstraints(requiresNetwork = false)

        override suspend fun execute(): Boolean {
                val language = getSelectedLanguage() ?: DEFAULT_LANGUAGE
                val unread = repository.countUnreadByLanguage(language)
                if (unread >= StoryGenerationService.MIN_UNREAD_TARGET) return true

                return try {
                        generationService.generateBackgroundUntilTarget(language = language)
                        true
                } catch (e: Exception) {
                        Log.logger.e(e) { "AAAAAFailed to generate catalog story" }
                        false
                }
        }

        companion object {
                const val TASK_ID = "lyra_story_pregeneration"
                private const val DEFAULT_LANGUAGE = "en"
        }
}
