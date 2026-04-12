package com.arno.lyramp.feature.stories_generator.model

sealed interface ModelDownloadState {
        object NotDownloaded : ModelDownloadState
        object Downloaded : ModelDownloadState

        object Checking : ModelDownloadState
        data class Downloading(val progress: Float) : ModelDownloadState
        data class Error(val message: String) : ModelDownloadState
}
