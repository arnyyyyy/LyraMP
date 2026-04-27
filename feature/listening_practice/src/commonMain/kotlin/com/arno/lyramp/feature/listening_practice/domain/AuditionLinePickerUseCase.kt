package com.arno.lyramp.feature.listening_practice.domain

import com.arno.lyramp.feature.listening_practice.model.AuditionLine
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import com.arno.lyramp.feature.listening_practice.model.TrackDownloadInfo
import com.arno.lyramp.feature.lyrics.domain.GetTimestampedLyricsUseCase
import com.arno.lyramp.feature.lyrics.domain.LyricsResult
import com.arno.lyramp.feature.music_streaming.domain.GetStreamingInfoUseCase
import com.arno.lyramp.feature.music_streaming.domain.StreamingResult
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException

internal class AuditionLinePickerUseCase(
        private val libraryProvider: AuditionLibraryProvider,
        private val getStreamingInfo: GetStreamingInfoUseCase,
        private val getTimestampedLyrics: GetTimestampedLyricsUseCase,
) {
        private var queue: ArrayDeque<PracticeTrack>? = null

        suspend fun reset(language: String? = null) {
                val tracks = libraryProvider.getTracks(language).shuffled()
                queue = ArrayDeque(tracks)
        }

        suspend fun nextLine(): AuditionLine? {
                if (queue == null) reset()
                val q = queue ?: return null

                while (q.isNotEmpty()) {
                        val track = q.removeFirst()
                        val candidate = tryBuildAuditionLine(track) ?: continue
                        return candidate
                }
                return null
        }

        private suspend fun tryBuildAuditionLine(track: PracticeTrack): AuditionLine? {
                return try {
                        val lyricsResult = getTimestampedLyrics(
                                artist = track.artists.firstOrNull().orEmpty(),
                                song = track.name,
                                trackId = track.id
                        )
                        val timestampedLyrics = when (lyricsResult) {
                                is LyricsResult.Found -> if (lyricsResult.isTimestamped) lyricsResult.lyrics else return null
                                LyricsResult.NotFound -> return null
                        }
                        val lines = LyricsParser.parseLrc(timestampedLyrics)?.filter { it.hasTimecode } ?: return null
                        if (lines.isEmpty()) return null

                        val streamingResult = getStreamingInfo.getStreamingInfo(track.id)
                        val streaming = when (streamingResult) {
                                is StreamingResult.Found -> streamingResult.info
                                StreamingResult.NotFound -> return null
                        }

                        AuditionLine(
                                track = track,
                                line = lines.random(),
                                downloadInfo = TrackDownloadInfo(
                                        url = streaming.url,
                                        encryptionKey = streaming.encryptionKey,
                                        transport = streaming.transport
                                )
                        )
                } catch (ce: CancellationException) {
                        throw ce
                } catch (e: Exception) {
                        Log.logger.w(e) { "AuditionLinePicker: skip track ${track.name} due to error" }
                        null
                }
        }
}

