package com.arno.lyramp.feature.lyrics.api

import com.arno.lyramp.feature.lyrics.model.LyricsOvhResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

class LyricsOvhApi(private val client: HttpClient) {
        suspend fun getLyrics(artist: String, title: String): LyricsOvhResponse {
                val url = "https://api.lyrics.ovh/v1/$artist/$title"
                try {
                        return client.get(url).body()
                } catch (e: ClientRequestException) {
                        if (e.response.status == HttpStatusCode.NotFound) {
                                return LyricsOvhResponse(null)
                        }
                        throw e
                } catch (e: Exception) {
                        throw e
                }
        }
}
