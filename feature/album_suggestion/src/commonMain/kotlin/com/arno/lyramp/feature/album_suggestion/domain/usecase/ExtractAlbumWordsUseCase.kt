package com.arno.lyramp.feature.album_suggestion.domain.usecase

import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.feature.album_suggestion.domain.model.SuggestedWord
import com.arno.lyramp.feature.album_suggestion.domain.model.SuggestedWordComparator
import com.arno.lyramp.feature.extraction.data.CefrRepository
import com.arno.lyramp.feature.extraction.domain.WordExtractionUtils
import com.arno.lyramp.feature.lyrics.domain.GetLyricsUseCase
import com.arno.lyramp.feature.lyrics.domain.LyricsResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class ExtractAlbumWordsUseCase(
        private val getLyrics: GetLyricsUseCase,
        private val cefrRepository: CefrRepository
) {
        suspend operator fun invoke(
                trackId: String,
                trackTitle: String,
                artists: String,
                trackIndex: Int,
                cefrFilter: Set<CefrLevel>,
                knownWords: Set<String>,
                language: String = "en"
        ): List<SuggestedWord> = withContext(Dispatchers.IO) {
                val cefrVocab = cefrRepository.getVocabularyMap(language)

                val lyricsText = when (val result = getLyrics(artists, trackTitle, trackId)) {
                        is LyricsResult.Found -> result.lyrics
                        LyricsResult.NotFound -> return@withContext emptyList()
                }

                val wordToInfo = WordExtractionUtils.extractUniqueWords(lyricsText, cefrVocab, language)

                wordToInfo
                        .filter { (word, info) ->
                                val (_, level) = info
                                word !in knownWords && level in cefrFilter
                        }
                        .map { (word, info) ->
                                val (lyricLine, level) = info
                                SuggestedWord(
                                        word = word,
                                        cefrLevel = level,
                                        lyricLine = lyricLine,
                                        trackName = trackTitle,
                                        artists = artists,
                                        trackIndex = trackIndex
                                )
                        }
                        .sortedWith(SuggestedWordComparator)
        }
}
