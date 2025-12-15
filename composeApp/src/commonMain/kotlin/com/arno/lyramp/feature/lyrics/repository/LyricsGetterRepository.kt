package com.arno.lyramp.feature.lyrics.repository

import com.arno.lyramp.feature.lyrics.api.LyricsOvhApi
import com.arno.lyramp.feature.lyrics.model.LyricData
import com.arno.lyramp.feature.lyrics.presentation.LyricsState
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class LyricsGetterRepository(
        private val api: LyricsOvhApi
) {
        suspend fun searchLyrics(artist: String, song: String): LyricsState =
                withContext(Dispatchers.IO) {
                        try {
                                if (artist.isBlank() || song.isBlank()) {
                                        LyricsState.Error("Введите исполнителя и название песни")
                                } else {
                                        val response = api.getLyrics(artist.trim(), song.trim())

                                        if (response.lyrics.isNullOrBlank()) {
                                                LyricsState.Error("Текст песни не найден")
                                        } else {
                                                val lyricData = LyricData(
                                                        artist = artist.trim(),
                                                        name = song.trim(),
                                                        lyrics = response.lyrics
                                                )
                                                LyricsState.Success(listOf(lyricData))
                                        }
                                }
                        } catch (_: SocketTimeoutException) {
                                LyricsState.Error("Превышено время ожидания")
                        } catch (e: Exception) {
                                LyricsState.Error("Ошибка: ${e.message ?: "Не удалось найти текст песни"}")
                        }
                }
}
