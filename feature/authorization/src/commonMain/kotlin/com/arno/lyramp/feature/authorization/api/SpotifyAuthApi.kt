package com.arno.lyramp.feature.authorization.api

import com.arno.lyramp.feature.authorization.model.SpotifyTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters

class SpotifyAuthApi(private val client: HttpClient) {
        suspend fun getTokenFromCode(
                code: String, codeVerifier: String, redirectUri: String, clientId: String
        ): SpotifyTokenResponse {
                return client.submitForm(
                        url = TOKEN_URL,
                        formParameters = Parameters.Companion.build {
                                append("grant_type", "authorization_code")
                                append("code", code)
                                append("redirect_uri", redirectUri)
                                append("client_id", clientId)
                                append("code_verifier", codeVerifier)
                        },
                        encodeInQuery = false
                ) { header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded) }.body()
        }

        suspend fun getRefreshToken(refreshToken: String, clientId: String): SpotifyTokenResponse {
                return client.submitForm(
                        url = TOKEN_URL, formParameters = Parameters.Companion.build {
                                append("grant_type", "refresh_token")
                                append("refresh_token", refreshToken)
                                append("client_id", clientId)
                        }
                ) { header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded) }.body()
        }

        private companion object {
                const val TOKEN_URL = "https://accounts.spotify.com/api/token"
        }
}
