package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.repository.AuthApiRepository

interface AuthService {
        suspend fun initAuth(service: MusicServiceType): String
        suspend fun handleAuthCallback(service: MusicServiceType, code: String)
}

internal class AuthServiceImpl(
        private val repositories: Map<MusicServiceType, AuthApiRepository>
) : AuthService {
        private fun getRepositoryForService(service: MusicServiceType): AuthApiRepository =
                repositories[service] ?: error("AuthRepository for $service is not provided")

        override suspend fun initAuth(service: MusicServiceType) = getRepositoryForService(service).initAuthFlow()
        override suspend fun handleAuthCallback(service: MusicServiceType, code: String) = getRepositoryForService(service).handleAuthCallback(code)
}
