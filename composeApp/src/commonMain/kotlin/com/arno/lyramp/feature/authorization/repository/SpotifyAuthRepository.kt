package com.arno.lyramp.feature.authorization.repository

import com.arno.lyramp.feature.authorization.api.SpotifyAuthApi
import com.arno.lyramp.feature.authorization.model.SpotifyAuthConfig
import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.arno.lyramp.util.Log
import com.arno.lyramp.util.generateCodeVerifier
import com.arno.lyramp.util.toCodeChallengeS256
import com.arno.lyramp.feature.authorization.presentation.spotify.launchSpotifyAuth

internal class SpotifyAuthRepository(
        private val api: SpotifyAuthApi,
        private val authConfig: SpotifyAuthConfig
) : AuthApiRepository {

        override val service: MusicServiceType = MusicServiceType.SPOTIFY

        override fun getAccessToken(): String? = SpotifyAuthStorage.accessToken
        override fun getRefreshToken(): String? = SpotifyAuthStorage.refreshToken

        override suspend fun refreshAccessToken(): String? {
                val refreshToken = getRefreshToken() ?: return null

                return try {
                        val tokenResponse = api.getRefreshToken(refreshToken, authConfig.CLIENT_ID)
                        saveTokens(tokenResponse.access_token, tokenResponse.refresh_token)
                        tokenResponse.access_token
                } catch (_: Throwable) {
                        null
                }
        }

        override suspend fun provideValidAccessToken(): String? {
                val access = getAccessToken()
                if (!access.isNullOrBlank()) return access

                return refreshAccessToken()
        }

        override suspend fun initAuthFlow() {
                val verifier = generateCodeVerifier()
                val challenge = verifier.toCodeChallengeS256()
                SpotifyAuthStorage.codeVerifier = verifier
                launchSpotifyAuth(challenge) // TODO: вынести из репозитория
        }

        override suspend fun handleAuthCallback(code: String) {
                val verifier = SpotifyAuthStorage.codeVerifier ?: return

                return try {
                        val tokenResponse = api.getTokenFromCode(
                                code,
                                verifier,
                                authConfig.REDIRECT_URI,
                                authConfig.CLIENT_ID
                        )
                        saveTokens(tokenResponse.access_token, tokenResponse.refresh_token)

                } catch (e: Throwable) {
                        Log.logger.e(e) { "SpotifyAuthRepository: token exchange failed" }
                }
        }

        private fun saveTokens(accessToken: String, refreshToken: String?) {
                SpotifyAuthStorage.accessToken = accessToken
                if (refreshToken != null) SpotifyAuthStorage.refreshToken = refreshToken
                try {
                        AuthSelectionStorage.lastAuthorizedService = MusicServiceType.SPOTIFY.name
                } catch (e: Throwable) {
                        Log.logger.e(e) { "SpotifyAuthRepository: token saving failed" }
                }
        }
}