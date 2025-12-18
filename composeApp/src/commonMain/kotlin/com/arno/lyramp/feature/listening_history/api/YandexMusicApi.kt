package com.arno.lyramp.feature.listening_history.api

import com.arno.lyramp.feature.listening_history.model.AccountStatusResponse
import com.arno.lyramp.feature.listening_history.model.LikedTracksResponse
import com.arno.lyramp.feature.listening_history.model.LikedTracksResult
import com.arno.lyramp.feature.listening_history.model.TracksResponseWrapper
import com.arno.lyramp.feature.listening_history.model.YandexLibrary
import com.arno.lyramp.feature.listening_history.model.YandexTrackItem
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


        suspend fun getLikedTracks(token: String): LikedTracksResponse {
                val accountStatus = getAccountStatus(token)
                val userId = accountStatus.result?.account?.uid ?: throw Exception("User ID not found")

                val likedTracksResponse = client.get("$BASE_URL/users/$userId/likes/tracks") {
                        header(HttpHeaders.Authorization, "OAuth $token")
                        header(HttpHeaders.Accept, "application/json")
                }.body<LikedTracksResponse>()

                val trackItems = likedTracksResponse.result?.library?.tracks.orEmpty()
                if (trackItems.isEmpty()) {
                        return likedTracksResponse
                }

                val trackIds = trackItems.mapNotNull { item ->
                        val trackId = item.id ?: item.track?.id
                        val albumId = item.albumId ?: item.track?.albums?.firstOrNull()?.id?.toString()
                        if (trackId != null && albumId != null) {
                                "$trackId:$albumId"
                        } else null
                }.joinToString(",")

                if (trackIds.isEmpty()) {
                        return likedTracksResponse
                }

                val fullTracksResponseWrapper = client.post("$BASE_URL/tracks") {
                        header(HttpHeaders.Authorization, "OAuth $token")
                        header(HttpHeaders.Accept, "application/json")
                        header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
                        setBody("track-ids=$trackIds&with-positions=true")
                }.body<TracksResponseWrapper>()

                val updatedTrackItems = fullTracksResponseWrapper.result?.map { track ->
                        YandexTrackItem(track = track)
                }.orEmpty()

                return LikedTracksResponse(
                        result = LikedTracksResult(
                                library = YandexLibrary(tracks = updatedTrackItems)
                        )
                )
        }

        private companion object {
                const val BASE_URL = "https://api.music.yandex.net"
        }
}