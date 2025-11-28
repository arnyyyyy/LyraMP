package com.arno.lyramp.feature.listening_history.api

import com.arno.lyramp.feature.listening_history.model.SpotifySavedTracksResponse
import com.arno.lyramp.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

internal class SpotifyMusicApi(private val client: HttpClient) {

        suspend fun savedTracks(accessToken: String, limit: Int = 20): SpotifySavedTracksResponse {
                return try {
                        client.get("https://api.spotify.com/v1/me/tracks?limit=$limit") {
                                header("Authorization", "Bearer $accessToken")
                        }.body()
                } catch (e: Throwable) {
                        Log.logger.e(e) { "SpotifyApi.musicHistory -> error" }
                        throw e
                }
        }
}