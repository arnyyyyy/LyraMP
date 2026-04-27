package com.arno.lyramp.feature.listening_practice.domain

import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import com.arno.lyramp.feature.listening_practice.model.TrackDownloadInfo
import com.arno.lyramp.feature.lyrics.domain.GetTimestampedLyricsUseCase
import com.arno.lyramp.feature.lyrics.domain.LyricsResult
import com.arno.lyramp.feature.music_streaming.domain.GetStreamingInfoUseCase
import com.arno.lyramp.feature.music_streaming.domain.StreamingResult

internal class LoadPracticeDataUseCase(
        private val getStreamingInfoUseCase: GetStreamingInfoUseCase,
        private val getTimestampedLyrics: GetTimestampedLyricsUseCase
) {
        suspend operator fun invoke(track: PracticeTrack): PracticeDataResult {
                val streamingResult = getStreamingInfoUseCase.getStreamingInfo(track.id)
                val streaming = when (streamingResult) {
                        is StreamingResult.Found -> streamingResult.info
                        StreamingResult.NotFound -> return PracticeDataResult.NoStreaming
                }

                val lyricsResult = getTimestampedLyrics(
                        artist = track.artists.firstOrNull().orEmpty(),
                        song = track.name,
                        trackId = track.id
                )

                val lines = when (lyricsResult) {
                        is LyricsResult.Found -> {
                                if (lyricsResult.isTimestamped) LyricsParser.parseLrc(lyricsResult.lyrics)
                                        ?: LyricsParser.parsePlain(lyricsResult.lyrics)
                                else LyricsParser.parsePlain(lyricsResult.lyrics)
                        }

                        LyricsResult.NotFound -> return PracticeDataResult.NoLyrics
                }

                return PracticeDataResult.Success(
                        downloadInfo = TrackDownloadInfo(
                                url = streaming.url,
                                encryptionKey = streaming.encryptionKey,
                                transport = streaming.transport
                        ),
                        lyricLines = lines
                )
        }
}
