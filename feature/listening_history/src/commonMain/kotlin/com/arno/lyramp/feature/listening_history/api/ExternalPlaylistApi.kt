package com.arno.lyramp.feature.listening_history.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

internal class ExternalPlaylistApi(private val client: HttpClient) {
        suspend fun loadPlaylistHtml(playlist: String): String {
                return client.get(playlist) {
                        header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)")
                        header("Accept", "text/html,application/xhtml+xml")
                        header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                }.body()
        }
}
