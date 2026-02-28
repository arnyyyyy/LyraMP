package com.arno.lyramp.feature.lyrics.api

import com.arno.lyramp.feature.lyrics.model.LyricsOvhResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class LyricsOvhApi(private val client: HttpClient) {
        suspend fun getLyrics(artist: String, title: String): String {
                val url = "https://api.lyrics.ovh/v1/$artist/$title"
                val response: LyricsOvhResponse = client.get(url).body()
                return response.lyrics?.takeIf { it.isNotBlank() } ?: error("Lyrics not found")
        }
}
