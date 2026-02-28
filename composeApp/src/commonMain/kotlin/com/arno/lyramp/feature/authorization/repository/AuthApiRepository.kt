package com.arno.lyramp.feature.authorization.repository

import com.arno.lyramp.feature.authorization.model.MusicServiceType

interface AuthApiRepository {
        val service: MusicServiceType

        suspend fun initAuthFlow(): String
        suspend fun handleAuthCallback(code: String)

        fun getAccessToken(): String?
        fun getRefreshToken(): String?

        suspend fun refreshAccessToken(): String?
        suspend fun provideValidAccessToken(): String?
}