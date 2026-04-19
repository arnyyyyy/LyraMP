package com.arno.lyramp.feature.album_suggestion.domain.usecase

import com.arno.lyramp.feature.album_suggestion.data.AlbumSuggestionRepository
import com.arno.lyramp.feature.album_suggestion.domain.model.CandidateMapper
import com.arno.lyramp.feature.album_suggestion.domain.model.SuggestedWord
import com.arno.lyramp.feature.user_settings.domain.model.LanguageSettings
import com.arno.lyramp.util.Log

internal class ExtractAndSaveTrackUseCase(
        private val extractAlbumWords: ExtractAlbumWordsUseCase,
        private val repository: AlbumSuggestionRepository,
        private val candidateMapper: CandidateMapper
) {
        suspend operator fun invoke(
                albumId: String,
                trackId: String,
                trackTitle: String,
                artists: String,
                trackIndex: Int,
                settings: LanguageSettings,
                knownWords: Set<String>
        ): List<SuggestedWord> {
                val extracted = try {
                        extractAlbumWords(
                                trackId = trackId,
                                trackTitle = trackTitle,
                                artists = artists,
                                trackIndex = trackIndex,
                                cefrFilter = settings.cefrFilter,
                                knownWords = knownWords,
                                language = settings.lang
                        )
                } catch (e: Exception) {
                        Log.logger.e(e) { "Extraction failed for track $trackTitle" }
                        emptyList()
                }

                if (extracted.isNotEmpty()) {
                        repository.saveCandidates(candidateMapper.toEntities(extracted, albumId, settings.lang))
                }
                return extracted
        }
}

