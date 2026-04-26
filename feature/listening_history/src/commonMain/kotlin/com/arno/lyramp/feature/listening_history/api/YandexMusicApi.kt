package com.arno.lyramp.feature.listening_history.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
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

        suspend fun getPlaylist(uid: Long, kind: Long): YandexPlaylistResponse {
                return client.get("$BASE_URL/users/$uid/playlists/$kind") {
                        header(HttpHeaders.Accept, "application/json")
                }.body()
        }

        suspend fun searchTracks(token: String, text: String): YandexSearchResponse {
                return client.get("$BASE_URL/search") {
                        header(HttpHeaders.Authorization, "OAuth $token")
                        header(HttpHeaders.Accept, "application/json")
                        parameter("text", text)
                        parameter("type", "track")
                        parameter("page", 0)
                }.body()
        }

        // TODO: мейби все-таки не в listening_history_api
        suspend fun getAlbumWithTracks(token: String, albumId: String): YandexAlbumWithTracksResponse {
                return client.get("$BASE_URL/albums/$albumId/with-tracks") {
                        header(HttpHeaders.Authorization, "OAuth $token")
                        header(HttpHeaders.Accept, "application/json")
                }.body()
        }

        private companion object {
                const val BASE_URL = "https://api.music.yandex.net"
        }
}