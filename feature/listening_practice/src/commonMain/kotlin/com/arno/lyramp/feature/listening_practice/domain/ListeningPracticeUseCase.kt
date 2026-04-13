package com.arno.lyramp.feature.listening_practice.domain

import com.arno.lyramp.feature.listening_practice.model.LyricLine
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import com.arno.lyramp.feature.listening_practice.model.TrackDownloadInfo
import com.arno.lyramp.feature.listening_practice.presentation.PracticeDataResult
import com.arno.lyramp.feature.lyrics.domain.GetTimestampedLyricsUseCase
import com.arno.lyramp.feature.lyrics.domain.LyricsResult
import com.arno.lyramp.feature.music_streaming.domain.GetStreamingInfoUseCase
import com.arno.lyramp.feature.music_streaming.domain.StreamingResult

internal class ListeningPracticeUseCase(
        private val getStreamingInfoUseCase: GetStreamingInfoUseCase,
        private val getTimestampedLyrics: GetTimestampedLyricsUseCase
) {
        suspend fun loadPracticeData(track: PracticeTrack): PracticeDataResult {
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
                                if (lyricsResult.isTimestamped) parseLrc(lyricsResult.lyrics) ?: parsePlain(lyricsResult.lyrics)
                                else parsePlain(lyricsResult.lyrics)
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

        private fun parseLrc(lyrics: String): List<LyricLine>? {
                val lrcPattern = Regex("""^\[(\d{2}):(\d{2})\.(\d{2,3})]\s*(.*)$""")

                val parsed = lyrics.lines().mapNotNull { line ->
                        val match = lrcPattern.matchEntire(line.trim()) ?: return@mapNotNull null
                        val minutes = match.groupValues[1].toLong()
                        val seconds = match.groupValues[2].toLong()
                        val centisStr = match.groupValues[3]
                        val centisMs = if (centisStr.length == 2) centisStr.toLong() * 10 else centisStr.toLong()
                        val startMs = (minutes * 60 + seconds) * 1000 + centisMs
                        val text = match.groupValues[4].trim()
                        if (text.isBlank()) return@mapNotNull null
                        startMs to text
                }

                if (parsed.isEmpty()) return null

                return parsed.mapIndexed { index, (startMs, text) ->
                        val endMs = if (index + 1 < parsed.size) parsed[index + 1].first else startMs + 5000
                        LyricLine(index = index, text = text, startMs = startMs, endMs = endMs)
                }
        }

        private fun parsePlain(lyrics: String): List<LyricLine> {
                return lyrics.lines().mapIndexedNotNull { index, line ->
                        line.trim().takeIf { it.isNotBlank() }?.let { LyricLine(index = index, text = it) }
                }
        }
}
