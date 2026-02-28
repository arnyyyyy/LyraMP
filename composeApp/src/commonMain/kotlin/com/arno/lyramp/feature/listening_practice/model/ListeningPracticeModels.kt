package com.arno.lyramp.feature.listening_practice.model

internal data class PracticeTrack(
        val id: String,
        val albumId: String?,
        val name: String,
        val artists: List<String>,
        val albumName: String? = null,
        val imageUrl: String? = null
)

internal enum class LineCheckResult {
        PENDING,
        CORRECT,
        INCORRECT
}

internal data class LyricLine(
        val index: Int,
        val text: String,
        val userInput: String = "",
        val checkResult: LineCheckResult = LineCheckResult.PENDING,
        val startMs: Long? = null,
        val endMs: Long? = null
) {
        val hasTimecode: Boolean
                get() = startMs != null
}

internal enum class PracticeMode {
        FULL_SONG,
        RANDOM_LINE
}

internal data class TrackDownloadInfo(
        val url: String,
        val encryptionKey: String? = null,
        val transport: String? = null
)
