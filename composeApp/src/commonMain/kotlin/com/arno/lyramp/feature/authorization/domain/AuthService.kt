package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.arno.lyramp.feature.authorization.repository.AuthRepository

interface AuthService {
        suspend fun initAuth(service: MusicServiceType)
        suspend fun handleAuthCallback(service: MusicServiceType, code: String)
}

internal class AuthServiceImpl(
        private val repositories: Map<MusicServiceType, AuthRepository>
) : AuthService {

        private fun getRepositoryForService(service: MusicServiceType): AuthRepository =
                repositories[service]
                        ?: error("AuthRepository for $service is not provided")

        override suspend fun initAuth(service: MusicServiceType) {
                getRepositoryForService(service).initAuthFlow()
        }

        override suspend fun handleAuthCallback(
                service: MusicServiceType,
                code: String
        ) {
                getRepositoryForService(service).handleAuthCallback(code)
        }
}
