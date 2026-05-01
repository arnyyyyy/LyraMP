package com.arno.lyramp.feature.translation.domain

import com.arno.lyramp.feature.translation.api.GoogleTranslationApi
import com.arno.lyramp.feature.translation.api.GoogleTranslationParser
import com.arno.lyramp.util.AudioFileManager
import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class TranslationRepository(httpClient: HttpClient) {
        private val api by lazy { GoogleTranslationApi(httpClient) }
        private val parser by lazy { GoogleTranslationParser() }

        private val audioFileManager = AudioFileManager()

        suspend fun translateWord(word: String): TranslationState {
                if (word.isBlank()) return TranslationState.EmptyWord
                return try {
                        val raw = api.getTranslationRaw(word)
                        val result = parser.parse(raw)

                        when {
                                result.translation.isNullOrBlank() -> TranslationState.Error("Перевод не найден")
                                else -> TranslationState.Success(result)
                        }
                } catch (e: CancellationException) {
                        throw e
                } catch (_: Exception) {
                        TranslationState.Error()
                }
        }

        suspend fun getSourceSpeechFilePath(wordInfo: WordInfo): String? {
                return withContext(Dispatchers.IO) {
                        if (wordInfo.translation.isNullOrBlank() || wordInfo.sourceLang.isNullOrBlank()) {
                                return@withContext null
                        }
                        try {
                                val sourceLang = wordInfo.sourceLang

                                if (audioFileManager.audioFileExists(wordInfo.word, sourceLang))
                                        return@withContext audioFileManager.getAudioFilePath(wordInfo.word, sourceLang)

                                val audioBytes = api.getSpeech(wordInfo.word, sourceLang)

                                return@withContext audioFileManager.saveAudioFile(wordInfo.word, sourceLang, audioBytes)
                        } catch (e: CancellationException) {
                                throw e
                        } catch (_: Exception) {
                                return@withContext null
                        }
                }
        }
}