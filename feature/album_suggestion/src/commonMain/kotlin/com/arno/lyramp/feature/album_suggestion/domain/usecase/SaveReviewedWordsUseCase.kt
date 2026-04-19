package com.arno.lyramp.feature.album_suggestion.domain.usecase

import com.arno.lyramp.feature.album_suggestion.data.AlbumSuggestionRepository
import com.arno.lyramp.feature.album_suggestion.domain.model.SuggestedWord
import com.arno.lyramp.feature.extraction.domain.usecase.MarkWordStringsAsShownUseCase
import com.arno.lyramp.feature.learn_words.domain.usecase.SaveLearnWordUseCase

internal class SaveReviewedWordsUseCase(
        private val saveLearnWord: SaveLearnWordUseCase,
        private val repository: AlbumSuggestionRepository,
        private val markWordsAsShown: MarkWordStringsAsShownUseCase
) {
        suspend operator fun invoke(
                albumId: String,
                words: List<SuggestedWord>,
                selectedToLearn: Set<String>,
                lang: String
        ): Int {
                val allWordStrings = words.map { it.word }
                markWordsAsShown(allWordStrings)
                repository.removeCandidateWords(albumId, allWordStrings)

                var totalPromoted = 0
                val byTrack = words.groupBy { it.trackIndex }

                for ((_, trackWords) in byTrack) {
                        val toLearn = trackWords.filter { it.word in selectedToLearn }

                        for (w in toLearn) {
                                saveLearnWord(
                                        word = w.word,
                                        translation = "",
                                        sourceLang = lang,
                                        trackName = w.trackName,
                                        artists = listOf(w.artists),
                                        lyricLine = w.lyricLine,
                                        albumId = albumId,
                                        trackIndex = w.trackIndex
                                )
                                totalPromoted++
                        }
                }
                return totalPromoted
        }
}
