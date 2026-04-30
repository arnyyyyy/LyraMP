package com.arno.lyramp.feature.stats.domain.usecase

import com.arno.lyramp.core.model.LyraLang
import com.arno.lyramp.core.model.TrackInfo
import com.arno.lyramp.core.model.WordDifficultyProvider
import com.arno.lyramp.core.util.wordTokenSequence
import com.arno.lyramp.feature.extraction.domain.WordExtractionUtils
import com.arno.lyramp.feature.listening_history.domain.usecase.GetRecentTracksUseCase
import com.arno.lyramp.feature.lyrics.domain.GetLyricsUseCase
import com.arno.lyramp.feature.lyrics.domain.LyricsResult
import com.arno.lyramp.feature.stats.data.StatsTrackCefrWordRepository
import com.arno.lyramp.feature.stats.data.StatsTrackMetaRepository
import com.arno.lyramp.feature.stats.domain.model.TrackCefrWord
import com.arno.lyramp.feature.stats.domain.model.TrackStatsMeta
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class ProcessTracksCefrUseCase(
        private val getRecentTracks: GetRecentTracksUseCase,
        private val getLyrics: GetLyricsUseCase,
        private val wordDifficulty: WordDifficultyProvider,
        private val cefrWordRepository: StatsTrackCefrWordRepository,
        private val metaRepository: StatsTrackMetaRepository,
) {
        @OptIn(ExperimentalTime::class)
        suspend operator fun invoke(maxTracks: Int = DEFAULT_MAX_TRACKS): Int {
                val processedIds = metaRepository.getAllProcessedTrackIds().toSet()
                val candidates = getRecentTracks()
                        .filter { it.language in LyraLang.SUPPORTED }
                        .filter { (it.id ?: "") !in processedIds }
                        .take(maxTracks)

                if (candidates.isEmpty()) return 0

                val vocabByLang = candidates.mapNotNull { it.language }.toSet()
                        .associateWith { lang -> wordDifficulty.getWordLevels(lang) }

                var processed = 0
                for (track in candidates) {
                        try {
                                currentCoroutineContext().ensureActive()
                                val trackId = track.id ?: continue
                                val lang = track.language ?: continue
                                val vocab = vocabByLang[lang] ?: emptyMap()

                                val lyricsResult = getLyrics(track.artists, track.name, track.id)
                                val lyrics = (lyricsResult as? LyricsResult.Found)?.lyrics
                                if (lyrics == null) {
                                        markTrackProcessedNoLyrics(trackId, track, lang)
                                        processed++
                                        continue
                                }

                                val wordToInfo = WordExtractionUtils.extractUniqueWords(lyrics, vocab, lang)

                                val words = wordToInfo.map { (word, info) ->
                                        TrackCefrWord(
                                                trackId = trackId,
                                                word = word,
                                                cefrLevel = info.second.name,
                                                language = lang,
                                        )
                                }

                                cefrWordRepository.deleteForTrack(trackId)
                                if (words.isNotEmpty()) {
                                        cefrWordRepository.saveAll(words)
                                }

                                metaRepository.save(
                                        TrackStatsMeta(
                                                trackId = trackId,
                                                trackName = track.name,
                                                artists = track.artists,
                                                language = lang,
                                                totalWordsInLyrics = countTotalWords(lyrics, lang),
                                                uniqueCefrWordsCount = wordToInfo.size,
                                                processedAt = Clock.System.now().toEpochMilliseconds(),
                                        )
                                )
                                processed++
                        } catch (e: CancellationException) {
                                throw e
                        } catch (e: Exception) {
                                Log.logger.w(e) { "CEFR stats processing failed for ${track.name}" }
                        }
                }
                return processed
        }

        @OptIn(ExperimentalTime::class)
        private suspend fun markTrackProcessedNoLyrics(
                trackId: String,
                track: TrackInfo,
                lang: String,
        ) {
                cefrWordRepository.deleteForTrack(trackId)
                metaRepository.save(
                        TrackStatsMeta(
                                trackId = trackId,
                                trackName = track.name,
                                artists = track.artists,
                                language = lang,
                                totalWordsInLyrics = 0,
                                uniqueCefrWordsCount = 0,
                                processedAt = Clock.System.now().toEpochMilliseconds(),
                        )
                )
        }

        private fun countTotalWords(lyrics: String, language: String) = if (language in LyraLang.CJK) {
                lyrics.count { !it.isWhitespace() && !it.isDigit() }
        } else {
                lyrics.lowercase().wordTokenSequence().count()
        }

        private companion object {
                const val DEFAULT_MAX_TRACKS = 15
        }
}
