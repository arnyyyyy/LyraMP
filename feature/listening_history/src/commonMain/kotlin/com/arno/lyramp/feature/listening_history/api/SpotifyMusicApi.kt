package com.arno.lyramp.feature.listening_history.api

import com.arno.lyramp.feature.listening_history.model.SpotifySavedTracksResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

class SpotifyMusicApi(private val client: HttpClient) {
        suspend fun getLikedTracks(accessToken: String, limit: Int = 20): SpotifySavedTracksResponse {
                return client.get("https://api.spotify.com/v1/me/tracks?limit=$limit") {
                        header("Authorization", "Bearer $accessToken")
                }.body()
        }
}