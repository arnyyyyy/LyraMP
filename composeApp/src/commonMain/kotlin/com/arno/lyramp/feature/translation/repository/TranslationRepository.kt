package com.arno.lyramp.feature.translation.repository

import com.arno.lyramp.feature.translation.api.GoogleTranslationApi
import com.arno.lyramp.feature.translation.model.WordInfo
import com.arno.lyramp.feature.translation.presentation.TranslationState
import com.arno.lyramp.util.AudioFileManager
import com.arno.lyramp.util.AudioPlayer
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class TranslationRepository(
        private val api: GoogleTranslationApi
) {
        private val audioFileManager = AudioFileManager()
        private var currentPlayer: AudioPlayer? = null

        suspend fun translateWord(word: String): TranslationState =
                withContext(Dispatchers.IO) {
                        try {
                                if (word.isBlank()) {
                                        TranslationState.EmptyWord
                                } else {
                                        val response = api.getTranslation(word)

                                        if (response.translation.isNullOrBlank()) {
                                                TranslationState.Error("Перевод не найден")
                                        } else {
                                                TranslationState.Success(response)
                                        }
                                }
                        } catch (_: SocketTimeoutException) {
                                TranslationState.Error("Превышено время ожидания")
                        } catch (e: Exception) {
                                TranslationState.Error(e.message ?: "Не удалось найти перевод")
                        }
                }

        suspend fun getAndPlaySourceSpeech(wordInfo: WordInfo): AudioPlayer? =
                withContext(Dispatchers.IO) {
                        try {
                                currentPlayer?.stop()
                                currentPlayer?.release()


                                if (wordInfo.translation.isNullOrBlank() || wordInfo.sourceLang.isNullOrBlank()) {
                                        return@withContext null
                                }

                                val sourceLang = wordInfo.sourceLang
                                val filePath: String

                                if (audioFileManager.audioFileExists(wordInfo.word, sourceLang)) {
                                        filePath = audioFileManager.getAudioFilePath(wordInfo.word, sourceLang)
                                } else {
                                        val audioBytes = api.getSpeech(wordInfo.word, sourceLang)

                                        if (audioBytes == null) {
                                                return@withContext null
                                        }

                                        filePath = audioFileManager.saveAudioFile(wordInfo.word, sourceLang, audioBytes)
                                }

                                val player = AudioPlayer()
                                player.play(filePath)
                                currentPlayer = player

                                player
                        } catch (_: Exception) {
                                null
                        }
                }


        fun stopAudio() {
                currentPlayer?.stop()
                currentPlayer?.release()
                currentPlayer = null
        }
}
