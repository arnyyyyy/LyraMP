package com.arno.lyramp.feature.listening_practice.presentation

import com.arno.lyramp.feature.listening_practice.model.LyricLine
import com.arno.lyramp.feature.listening_practice.model.TrackDownloadInfo

internal sealed class PracticeDataResult {
        data class Success(
                val downloadInfo: TrackDownloadInfo,
                val lyricLines: List<LyricLine>
        ) : PracticeDataResult()

        object NoStreaming : PracticeDataResult()
        object NoLyrics : PracticeDataResult()
}
