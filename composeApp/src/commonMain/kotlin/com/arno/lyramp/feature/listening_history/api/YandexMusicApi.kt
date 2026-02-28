package com.arno.lyramp.feature.listening_history.api

import com.arno.lyramp.feature.listening_history.model.AccountStatusResponse
import com.arno.lyramp.feature.listening_history.model.LikedTracksResponse
import com.arno.lyramp.feature.listening_history.model.TracksResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

internal class YandexMusicApi(private val client: HttpClient) {
        suspend fun getAccountStatus(token: String): AccountStatusResponse {
                return client.get("$BASE_URL/account/status") {
                        header(HttpHeaders.Authorization, "OAuth $token")
                        header(HttpHeaders.Accept, "application/json")
                }.body()
        }

        suspend fun getLikedTracks(token: String, userId: String): LikedTracksResponse {
                return client.get("$BASE_URL/users/$userId/likes/tracks") {
                        header(HttpHeaders.Authorization, "OAuth $token")
                        header(HttpHeaders.Accept, "application/json")
                }.body()
        }

        suspend fun getFullTracksInfo(token: String, trackIds: String): TracksResponseWrapper {
                return client.post("$BASE_URL/tracks") {
                        header(HttpHeaders.Authorization, "OAuth $token")
                        header(HttpHeaders.Accept, "application/json")
                        header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
                        setBody("track-ids=$trackIds&with-positions=true")
                }.body()
        }

        private companion object {
                const val BASE_URL = "https://api.music.yandex.net"
        }
}