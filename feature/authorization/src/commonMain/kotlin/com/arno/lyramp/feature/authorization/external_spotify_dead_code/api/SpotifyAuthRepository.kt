//package com.arno.lyramp.feature.authorization.repository
//
//import com.arno.lyramp.feature.authorization.api.SpotifyAuthApi
//import com.arno.lyramp.feature.authorization.model.SpotifyAuthConfig
//import com.arno.lyramp.feature.authorization.model.MusicServiceType
//import com.russhwolf.settings.Settings
//import com.arno.lyramp.util.Log
//import com.arno.lyramp.feature.authorization.util.generateCodeVerifier
//import com.arno.lyramp.feature.authorization.util.toCodeChallengeS256
//
//internal class SpotifyAuthRepository(
//        private val api: SpotifyAuthApi,
//        private val authConfig: SpotifyAuthConfig,
//        settings: Settings
//) : AuthApiRepository {
//
//        private val storage = SpotifyAuthStorage(settings)
//
//        override val service: MusicServiceType = MusicServiceType.SPOTIFY
//
//        override fun getAccessToken(): String? = storage.accessToken
//        override fun getRefreshToken(): String? = storage.refreshToken
//
//        override suspend fun refreshAccessToken(): String? {
//                val refreshToken = getRefreshToken() ?: return null
//                val tokenResponse = api.getRefreshToken(refreshToken, authConfig.CLIENT_ID)
//                saveTokens(tokenResponse.access_token, tokenResponse.refresh_token)
//                return tokenResponse.access_token
//        }
//
//        override suspend fun provideValidAccessToken(): String? {
//                val access = getAccessToken()
//                if (!access.isNullOrBlank()) return access
//
//                return refreshAccessToken()
//        }
//
//        override suspend fun initAuthFlow(): String {
//                val verifier = generateCodeVerifier()
//                val challenge = verifier.toCodeChallengeS256()
//                storage.codeVerifier = verifier
//                return "https://accounts.spotify.com/authorize" +
//                        "?client_id=${authConfig.CLIENT_ID}" +
//                        "&response_type=code" +
//                        "&redirect_uri=${authConfig.REDIRECT_URI}" +
//                        "&code_challenge=$challenge" +
//                        "&code_challenge_method=S256" +
//                        "&scope=${authConfig.SCOPE}"
//        }
//
//        override suspend fun handleAuthCallback(code: String) {
//                val verifier = storage.codeVerifier ?: return
//
//                return try {
//                        val tokenResponse = api.getTokenFromCode(
//                                code,
//                                verifier,
//                                authConfig.REDIRECT_URI,
//                                authConfig.CLIENT_ID
//                        )
//                        saveTokens(tokenResponse.access_token, tokenResponse.refresh_token)
//
//                } catch (e: Throwable) {
//                        Log.logger.e(e) { "SpotifyAuthRepository: token exchange failed" }
//                }
//        }
//
//        private fun saveTokens(accessToken: String, refreshToken: String?) {
//                storage.accessToken = accessToken
//                if (refreshToken != null) storage.refreshToken = refreshToken
//                try {
//                        AuthSelectionStorage.lastAuthorizedService = MusicServiceType.SPOTIFY.name
//                } catch (e: Throwable) {
//                        Log.logger.e(e) { "SpotifyAuthRepository: token saving failed" }
//                }
//        }
//}