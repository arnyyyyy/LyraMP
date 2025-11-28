package com.arno.lyramp.feature.authorization.repository

import com.arno.lyramp.feature.authorization.model.MusicServiceType

interface AuthRepository {
        val service: MusicServiceType

        suspend fun initAuthFlow()
        suspend fun handleAuthCallback(code: String)

        fun getAccessToken(): String?
        fun getRefreshToken(): String?

        suspend fun refreshAccessToken(): String?
        suspend fun provideValidAccessToken(): String?
}