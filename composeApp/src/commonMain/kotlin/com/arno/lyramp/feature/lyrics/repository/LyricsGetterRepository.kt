package com.arno.lyramp.feature.lyrics.repository

import com.arno.lyramp.feature.lyrics.api.LyricsOvhApi
import com.arno.lyramp.feature.lyrics.model.LyricData
import com.arno.lyramp.feature.lyrics.model.LyricsResult
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class LyricsGetterRepository(
        private val api: LyricsOvhApi
) {
        suspend fun searchLyrics(artist: String, song: String): LyricsResult =
                withContext(Dispatchers.IO) {
                        try {
                                if (artist.isBlank() || song.isBlank()) {
                                        LyricsResult.Error("Введите исполнителя и название песни")
                                } else {
                                        val response = api.getLyrics(artist.trim(), song.trim())

                                        if (response.lyrics.isNullOrBlank()) {
                                                LyricsResult.Error("Текст песни не найден")
                                        } else {
                                                val lyricData = LyricData(
                                                        artist = artist.trim(),
                                                        name = song.trim(),
                                                        lyrics = response.lyrics
                                                )
                                                LyricsResult.Success(listOf(lyricData))
                                        }
                                }
                        } catch (_: SocketTimeoutException) {
                                LyricsResult.Error("Превышено время ожидания")
                        } catch (e: Exception) {
                                LyricsResult.Error("Ошибка: ${e.message ?: "Не удалось найти текст песни"}")
                        }
                }
}
