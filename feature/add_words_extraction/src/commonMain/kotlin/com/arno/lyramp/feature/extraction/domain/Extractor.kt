package com.arno.lyramp.feature.extraction.domain

import co.touchlab.kermit.Logger
import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.core.model.TrackInfo
import com.arno.lyramp.feature.extraction.domain.model.ExtractedWord
import com.arno.lyramp.feature.extraction.domain.model.ExtractionResult
import com.arno.lyramp.feature.extraction.domain.usecase.GetCefrVocabularyUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.GetShownWordsUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.MarkWordsAsShownUseCase
import com.arno.lyramp.feature.listening_history.domain.GetRecentTracksUseCase
import com.arno.lyramp.feature.lyrics.domain.LyricsResult
import com.arno.lyramp.feature.lyrics.domain.LyricsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal class Extractor(
        private val getRecentTracks: GetRecentTracksUseCase,
        private val lyricsUseCase: LyricsUseCase,
        private val getCefrVocabulary: GetCefrVocabularyUseCase,
        private val getShownWords: GetShownWordsUseCase,
        val markAsShown: MarkWordsAsShownUseCase,
) {
        private val log = Logger.withTag("LyricsWordsExtractor")

        internal suspend fun extractFromRecentTracks(): ExtractionResult = withContext(Dispatchers.IO) {
                val shownWords = getShownWords()

                // TODO ФИЛЬТРУЕМ ПО ВЫБРАННОМУ ЯЗЫКУ КОГДА БУДЕТ ПРОФИЛЬ
                val candidateTracks = getRecentTracks().filter { it.language in SUPPORTED_LANGUAGES }.take(MAX_TRACKS_TO_SCAN)
                if (candidateTracks.isEmpty()) {
                        return@withContext ExtractionResult(0, 0, 0)
                }

                val vocabByLang = candidateTracks.mapNotNull { it.language }.toSet().associateWith { lang -> getCefrVocabulary(lang) }

                val allExtractedWords = mutableListOf<ExtractedWord>()
                val seenWords = mutableSetOf<String>()
                var processedTracks = 0
                var totalWordsInLyrics = 0

                for (track in candidateTracks) {
                        if (allExtractedWords.size >= MAX_NEW_WORDS) break

                        try {
                                coroutineContext.ensureActive()
                                val result = processTrack(track, vocabByLang, shownWords, seenWords, allExtractedWords.size)
                                if (result != null) {
                                        allExtractedWords.addAll(result.words)
                                        totalWordsInLyrics += result.totalWordsInLyrics
                                        processedTracks++
                                }
                        } catch (e: CancellationException) {
                                throw e
                        } catch (e: Exception) {
                                log.w(e) { "Failed to process track: ${track.name}" }
                        }
                }

                val sorted = allExtractedWords.sortedWith(compareBy<ExtractedWord> { it.cefrLevel.ordinal }.thenBy { it.word })
                markAsShown(sorted)

                ExtractionResult(processedTracks, totalWordsInLyrics, sorted.size, sorted)
        }

        private suspend fun processTrack(
                track: TrackInfo,
                vocabByLang: Map<String, Map<String, CefrLevel>>,
                shownWords: Set<String>,
                seenWords: MutableSet<String>,
                currentWordCount: Int,
        ): TrackProcessingResult? {
                val trackLang = track.language ?: return null
                val cefrVocab = vocabByLang[trackLang] ?: return null

                val lyricsResult = lyricsUseCase.getLyrics(track.artists, track.name, track.id)
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
                const val MAX_TRACKS_TO_SCAN = 3
                const val MAX_NEW_WORDS = 30
                val SUPPORTED_LANGUAGES = setOf("en", "fr", "de", "es", "it", "hu", "ja", "zh", "he", "ar")
        }
}
