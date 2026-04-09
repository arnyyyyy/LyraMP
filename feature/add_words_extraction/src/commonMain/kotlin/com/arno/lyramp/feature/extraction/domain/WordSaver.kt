package com.arno.lyramp.feature.extraction.domain

import co.touchlab.kermit.Logger
import com.arno.lyramp.feature.extraction.domain.model.ExtractedWord
import com.arno.lyramp.feature.extraction.domain.usecase.SaveWordUseCase
import com.arno.lyramp.feature.translation.domain.TranslateWordUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

internal class WordSaver(
        private val translateWord: TranslateWordUseCase,
        private val saveWord: SaveWordUseCase,
) {
        @OptIn(ExperimentalCoroutinesApi::class)
        suspend fun saveAll(words: List<ExtractedWord>): Int = withContext(Dispatchers.IO) {

                coroutineScope {
                        words.asFlow().flatMapMerge(concurrency = MAX_PARALLEL) { w ->
                                flow {
                                        try {
                                                saveWord(
                                                        w.word,
                                                        translateWord(w.word),
                                                        w.language,
                                                        w.trackName,
                                                        w.artists,
                                                        w.lyricLine
                                                )
                                                emit(1)
                                        } catch (e: CancellationException) {
                                                throw e
                                        } catch (e: Exception) {
                                                Logger.e(e) { "WORD SAVER: Failed to save: ${w.word}" }
                                                emit(0)
                                        }
                                }
                        }.toList().sum()
                }
        }

        private companion object {
                const val MAX_PARALLEL = 5
        }
}
