package com.arno.lyramp.feature.extraction.background

import com.arno.lyramp.core.background.BackgroundTask
import com.arno.lyramp.core.background.TaskConstraints
import com.arno.lyramp.feature.extraction.data.PendingExtractionRepository
import com.arno.lyramp.feature.extraction.domain.Extractor
import com.arno.lyramp.feature.extraction.domain.model.ExtractionResult
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLanguageSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow

internal class ExtractionBackgroundTask(
        private val extractor: Extractor,
        private val getLanguageSettings: GetLanguageSettingsUseCase,
        private val pendingExtraction: PendingExtractionRepository,
) : BackgroundTask {

        override val taskId: String = TASK_ID

        override val constraints: TaskConstraints = TaskConstraints(requiresNetwork = true)

        override suspend fun execute(): Boolean {
                val settings = getLanguageSettings()

                val result = extractor.extractFromRecentTracks(
                        languageFilter = settings.lang,
                        cefrFilter = settings.cefrFilter,
                        levelsKey = settings.levelsKey,
                )

                if (result.words.isEmpty()) {
                        pendingExtraction.save(result)
                        _cachedResult.value = null
                        return true
                }

                pendingExtraction.save(result)
                _cachedResult.value = result
                return true
        }

        companion object {
                const val TASK_ID = "lyra_extraction_background"

                private val _cachedResult = MutableStateFlow<ExtractionResult?>(null)

                fun consumeCachedResult(): ExtractionResult? {
                        val result = _cachedResult.value
                        _cachedResult.value = null
                        return result
                }
        }
}
