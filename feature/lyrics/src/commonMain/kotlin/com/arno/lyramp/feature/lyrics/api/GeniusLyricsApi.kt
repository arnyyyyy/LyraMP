package com.arno.lyramp.feature.lyrics.api

import com.arno.lyramp.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

internal class GeniusLyricsApi(private val client: HttpClient) {
        suspend fun fetchHtml(url: String): String? {
                val response = client.get(url) {
                        header(
                                "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                                    "(KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36"
                        )
                        header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        header("Accept-Language", "en-US,en;q=0.9")
                }

                if (!response.status.isSuccess()) {
                        Log.logger.e { "GeniusLyricsApi: HTTP ${response.status.value} for $url" }
                        return null
                }

                return response.bodyAsText()
        }
}
