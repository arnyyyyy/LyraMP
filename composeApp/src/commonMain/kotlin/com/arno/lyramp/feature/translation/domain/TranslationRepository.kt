package com.arno.lyramp.feature.translation.domain

import com.arno.lyramp.feature.translation.api.GoogleTranslationApi
import com.arno.lyramp.feature.translation.model.WordInfo
import com.arno.lyramp.util.AudioFileManager
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class TranslationRepository(httpClient: HttpClient) {
        private val api by lazy { GoogleTranslationApi(httpClient) }
        private val parser by lazy { GoogleTranslationParser() }

        private val audioFileManager = AudioFileManager()

        suspend fun translateWord(word: String): TranslationState {
                return withContext(Dispatchers.IO) {
                        if (word.isBlank()) return@withContext TranslationState.EmptyWord
                        try {
                                val raw = api.getTranslationRaw(word)
                                val result = parser.parse(raw)

                                when {
                                        result.translation.isNullOrBlank() -> TranslationState.Error("Перевод не найден")
                                        else -> TranslationState.Success(result)
                                }
                        } catch (_: SocketTimeoutException) {
                                TranslationState.Error("Превышено время ожидания")
                        } catch (e: Exception) {
                                TranslationState.Error(e.message ?: "Не удалось найти перевод")
                        }
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
                        } catch (_: Exception) {
                                return@withContext null
                        }
                }
        }
}
