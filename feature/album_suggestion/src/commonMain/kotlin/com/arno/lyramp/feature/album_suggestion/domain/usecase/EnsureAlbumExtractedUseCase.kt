package com.arno.lyramp.feature.album_suggestion.domain.usecase

import com.arno.lyramp.feature.album_suggestion.data.AlbumProgressInfo
import com.arno.lyramp.feature.album_suggestion.data.AlbumSuggestionRepository
import com.arno.lyramp.feature.extraction.domain.usecase.GetShownWordsUseCase
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllUserWordStringsUseCase
import com.arno.lyramp.feature.listening_history.domain.model.AlbumWithTracksResult
import com.arno.lyramp.feature.user_settings.domain.model.LanguageSettings
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

internal class EnsureAlbumExtractedUseCase(
        private val repository: AlbumSuggestionRepository,
        private val extractAndSaveTrack: ExtractAndSaveTrackUseCase,
        private val getAllUserWords: GetAllUserWordStringsUseCase,
        private val getShownWords: GetShownWordsUseCase
) {
        suspend operator fun invoke(
                albumId: String,
                result: AlbumWithTracksResult,
                settings: LanguageSettings,
                onProgress: ((extracted: Int, total: Int) -> Unit)? = null
        ) {
                val existing = repository.getAlbumProgress(albumId)

                if (existing == null) {
                        repository.upsertAlbumProgress(
                                AlbumProgressInfo(
                                        albumId = albumId,
                                        albumTitle = result.title,
                                        artistName = result.artistName,
                                        coverUri = result.coverUri,
                                        totalTracks = result.tracks.size,
                                        wordsExtracted = false
                                )
                        )
                }

                val settingsChanged = existing != null && existing.wordsExtracted &&
                    (existing.extractedLanguage != settings.lang || existing.extractedLevels != settings.levelsKey)

                val needsExtraction = existing == null || !existing.wordsExtracted || settingsChanged

                if (!needsExtraction) return
                if (settingsChanged) repository.removeAllCandidatesForAlbum(albumId)

                extractAndPersistAll(albumId, result, settings, onProgress)

                val progress = repository.getAlbumProgress(albumId) ?: error("AlbumProgress for $albumId not found after insert")
                repository.upsertAlbumProgress(
                        progress.copy(
                                wordsExtracted = true,
                                extractedLanguage = settings.lang,
                                extractedLevels = settings.levelsKey
                        )
                )
        }

        private suspend fun extractAndPersistAll(
                albumId: String,
                result: AlbumWithTracksResult,
                settings: LanguageSettings,
                onProgress: ((extracted: Int, total: Int) -> Unit)?
        ) = coroutineScope {
                val allUserWords = getAllUserWords(settings.lang)
                val shownWords = getShownWords.forExtraction(settings.lang)
                val excludedWords = allUserWords + shownWords
                val semaphore = Semaphore(4)
                var completed = 0
                val total = result.tracks.size

                result.tracks.map { track ->
                        async {
                                semaphore.withPermit {
                                        extractAndSaveTrack(
                                                albumId = albumId,
                                                trackId = track.trackId,
                                                trackTitle = track.title,
                                                artists = track.artists,
                                                trackIndex = track.trackIndex,
                                                settings = settings,
                                                knownWords = excludedWords
                                        )
                                }
                                completed++
                                onProgress?.invoke(completed, total)
                        }
                }.awaitAll()
        }
}
