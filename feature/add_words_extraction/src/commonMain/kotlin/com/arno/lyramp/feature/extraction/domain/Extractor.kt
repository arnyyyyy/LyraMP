package com.arno.lyramp.feature.extraction.domain

import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.core.model.LyraLang
import com.arno.lyramp.core.model.TrackInfo
import com.arno.lyramp.feature.extraction.domain.model.ExtractedWord
import com.arno.lyramp.feature.extraction.domain.model.ExtractionResult
import com.arno.lyramp.feature.extraction.domain.usecase.GetCefrVocabularyUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.GetExhaustedTrackIdsUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.GetShownWordsUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.MarkTrackExhaustedUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.MarkWordsAsShownUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetRecentTracksUseCase
import com.arno.lyramp.feature.lyrics.domain.GetLyricsUseCase
import com.arno.lyramp.feature.lyrics.domain.LyricsResult
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal class Extractor(
        private val getRecentTracks: GetRecentTracksUseCase,
        private val getLyrics: GetLyricsUseCase,
        private val getCefrVocabulary: GetCefrVocabularyUseCase,
        private val getShownWords: GetShownWordsUseCase,
        val markAsShown: MarkWordsAsShownUseCase,
        private val getExhaustedTrackIds: GetExhaustedTrackIdsUseCase,
        private val markTrackExhausted: MarkTrackExhaustedUseCase,
) {
        internal suspend fun extractFromRecentTracks(
                languageFilter: String? = null,
                cefrFilter: Set<CefrLevel>? = null,
                levelsKey: String? = null,
                onProgress: (progress: Float, trackName: String) -> Unit = { _, _ -> }
        ): ExtractionResult = withContext(Dispatchers.IO) {
                val shownWords = getShownWords()
                val exhaustedIds = if (levelsKey != null) getExhaustedTrackIds(levelsKey) else emptySet()

                val candidateTracks = getRecentTracks()
                        .filter { it.language in LyraLang.SUPPORTED }
                        .let { tracks ->
                                if (languageFilter != null) tracks.filter { it.language == languageFilter }
                                else tracks
                        }
                        .filter { (it.id ?: "") !in exhaustedIds }
                        .take(MAX_TRACKS_TO_SCAN)

                if (candidateTracks.isEmpty()) return@withContext ExtractionResult(0, 0, 0)

                val vocabByLang = candidateTracks.mapNotNull { it.language }.toSet()
                        .associateWith { lang -> getCefrVocabulary(lang) }

                val allExtractedWords = mutableListOf<ExtractedWord>()
                val seenWords = mutableSetOf<String>()
                var processedTracks = 0
                var totalWordsInLyrics = 0

                for ((index, track) in candidateTracks.withIndex()) {
                        if (allExtractedWords.size >= MAX_NEW_WORDS) break

                        onProgress(index.toFloat() / candidateTracks.size, track.name)

                        try {
                                coroutineContext.ensureActive()
                                val result = processTrack(
                                        track, vocabByLang, shownWords, seenWords,
                                        allExtractedWords.size, cefrFilter
                                )
                                if (result != null) {
                                        allExtractedWords.addAll(result.words)
                                        totalWordsInLyrics += result.totalWordsInLyrics
                                        processedTracks++

                                        val trackId = track.id
                                        if (result.words.isEmpty() && levelsKey != null && trackId != null) {
                                                markTrackExhausted(trackId, track.name, levelsKey)
                                        }
                                } else {
                                        val trackId = track.id
                                        if (levelsKey != null && trackId != null) {
                                                markTrackExhausted(trackId, track.name, levelsKey)
                                        }
                                }
                        } catch (e: CancellationException) {
                                throw e
                        } catch (e: Exception) {
                                Log.logger.w(e) { "Failed to process track: ${track.name}" }
                        }
                }

                val sorted = allExtractedWords.sortedWith(
                        compareBy<ExtractedWord> { it.cefrLevel.ordinal }.thenBy { it.word }
                )
                markAsShown(sorted)

                ExtractionResult(processedTracks, totalWordsInLyrics, sorted.size, sorted)
        }

        private suspend fun processTrack(
                track: TrackInfo,
                vocabByLang: Map<String, Map<String, CefrLevel>>,
                shownWords: Set<String>,
                seenWords: MutableSet<String>,
                currentWordCount: Int,
                cefrFilter: Set<CefrLevel>?,
        ): TrackProcessingResult? {
                val trackLang = track.language ?: return null
                val cefrVocab = vocabByLang[trackLang] ?: return null

                val lyricsResult = getLyrics(track.artists, track.name, track.id)
                val lyrics = when (lyricsResult) {
                        is LyricsResult.Found -> lyricsResult.lyrics
                        else -> return null
                }

                val wordToInfo = WordExtractionUtils.extractUniqueWords(lyrics, cefrVocab, trackLang)
                val extracted = mutableListOf<ExtractedWord>()

                for ((word, info) in wordToInfo) {
                        if (currentWordCount + extracted.size >= MAX_NEW_WORDS) break
                        if (!seenWords.add(word) || word in shownWords) continue

                        val (lyricLine, cefrLevel) = info
                        if (cefrFilter != null && cefrLevel !in cefrFilter) continue

                        extracted.add(
                                ExtractedWord(
                                        word = word,
                                        cefrLevel = cefrLevel,
                                        lyricLine = lyricLine.trim(),
                                        trackName = track.name,
                                        artists = track.artists.split(",").map { it.trim() },
                                        language = trackLang
                                )
                        )
                }

                return TrackProcessingResult(extracted, wordToInfo.size)
        }

        private data class TrackProcessingResult(
                val words: List<ExtractedWord>,
                val totalWordsInLyrics: Int,
        )

        private companion object {
                const val MAX_TRACKS_TO_SCAN = 5
                const val MAX_NEW_WORDS = 30
        }
}
