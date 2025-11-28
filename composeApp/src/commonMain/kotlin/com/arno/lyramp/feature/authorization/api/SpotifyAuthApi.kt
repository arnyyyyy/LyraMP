package com.arno.lyramp.feature.authorization.api

import com.arno.lyramp.feature.authorization.model.SpotifyTokenResponse
import com.arno.lyramp.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters

internal class SpotifyAuthApi(private val client: HttpClient) {

        suspend fun getTokenFromCode(
                code: String,
                codeVerifier: String,
                redirectUri: String,
                clientId: String
        ): SpotifyTokenResponse {
                return try {
                        client.submitForm(
                                url = TOKEN_URL,
                                formParameters = Parameters.Companion.build {
                                        append("grant_type", "authorization_code")
                                        append("code", code)
                                        append("redirect_uri", redirectUri)
                                        append("client_id", clientId)
                                        append("code_verifier", codeVerifier)
                                },
                                encodeInQuery = false
                        ) {
                                header(
                                        HttpHeaders.ContentType,
                                        ContentType.Application.FormUrlEncoded
                                )
                        }.body()

                } catch (e: Throwable) {
                        Log.logger.e(e) { "SpotifyApi.getTokenFromCode -> error" }
                        throw e
                }
        }

        suspend fun getRefreshToken(
                refreshToken: String,
                clientId: String
        ): SpotifyTokenResponse {
                return try {
                        client.submitForm(
                                url = TOKEN_URL,
                                formParameters = Parameters.Companion.build {
                                        append("grant_type", "refresh_token")
                                        append("refresh_token", refreshToken)
                                        append("client_id", clientId)
                                }
                        ) {
                                header(
                                        HttpHeaders.ContentType,
                                        ContentType.Application.FormUrlEncoded
                                )
                        }.body()

                } catch (e: Throwable) {
                        Log.logger.e(e) { "SpotifyApi.getRefreshToken -> error" }
                        throw e
                }
        }

        private companion object Companion {
                const val TOKEN_URL = "https://accounts.spotify.com/api/token"
        }
}