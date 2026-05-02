package com.arno.lyramp.feature.stories_generator.domain

import com.arno.lyramp.feature.stories_generator.model.DownloadableModel
import com.arno.lyramp.feature.stories_generator.model.ModelDownloadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class ModelDownloadService(
        private val repository: ModelDownloadRepository,
        private val generator: StoryGenerator,
) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        private val _state = MutableStateFlow<ModelDownloadState>(ModelDownloadState.NotDownloaded)
        val state: StateFlow<ModelDownloadState> = _state.asStateFlow()

        private val _activeModel = MutableStateFlow<DownloadableModel?>(null)
        val activeModel: StateFlow<DownloadableModel?> = _activeModel.asStateFlow()

        private val operationMutex = Mutex()
        private var downloadJob: Job? = null

        init {
                scope.launch {
                        operationMutex.withLock<Unit> {
                                if (_state.value is ModelDownloadState.Downloading) return@withLock
                                if (_state.value is ModelDownloadState.Paused) return@withLock

                                val downloaded = withContext(Dispatchers.IO) { repository.findDownloadedModel() }
                                if (downloaded != null) {
                                        _activeModel.value = downloaded
                                        _state.value = ModelDownloadState.Downloaded
                                } else {
                                        val partiallyLoaded = withContext(Dispatchers.IO) {
                                                DownloadableModel.entries.firstOrNull {
                                                        repository.getPartialBytes(it) > 0L
                                                }
                                        }
                                        if (partiallyLoaded != null) {
                                                _activeModel.value = partiallyLoaded
                                                _state.value = ModelDownloadState.Paused(
                                                        progress = withContext<Float>(Dispatchers.IO) {
                                                                repository.getSavedProgress(partiallyLoaded)
                                                        }
                                                )
                                        } else {
                                                _activeModel.value = null
                                                _state.value = ModelDownloadState.NotDownloaded
                                        }
                                }
                        }
                }
        }

        fun startDownload(model: DownloadableModel) = scope.launch {
                operationMutex.withLock {
                        if (downloadJob?.isActive == true) return@withLock

                        val current = _activeModel.value
                        if (current != null && current != model) {
                                generator.release()
                                withContext(Dispatchers.IO) { repository.deleteModel(current) }
                        }
                        _activeModel.value = model
                        val resumeFromBytes = withContext(Dispatchers.IO) { repository.getPartialBytes(model) }
                        launchDownload(model, resumeFromBytes = resumeFromBytes)
                }
        }

        fun resumeDownload() = scope.launch {
                operationMutex.withLock {
                        if (downloadJob?.isActive == true) return@withLock
                        val model = _activeModel.value ?: return@withLock
                        val bytes = withContext(Dispatchers.IO) { repository.getPartialBytes(model) }
                        launchDownload(model, resumeFromBytes = bytes)
                }
        }

        fun pauseDownload() = scope.launch {
                operationMutex.withLock {
                        val current = _state.value
                        if (current !is ModelDownloadState.Downloading) return@withLock
                        downloadJob?.cancelAndJoin()
                        downloadJob = null
                        _state.value = ModelDownloadState.Paused(current.progress)
                }
        }

        fun deleteAll() = scope.launch {
                operationMutex.withLock {
                        downloadJob?.cancelAndJoin()
                        downloadJob = null
                        generator.release()
                        withContext(Dispatchers.IO) { repository.deleteAllModels() }
                        _activeModel.value = null
                        _state.value = ModelDownloadState.NotDownloaded
                }
        }

        private fun launchDownload(model: DownloadableModel, resumeFromBytes: Long) {
                downloadJob = scope.launch {
                        repository.downloadModel(model, resumeFromBytes).collect { state ->
                                _state.value = state
                        }
                }
        }
}

