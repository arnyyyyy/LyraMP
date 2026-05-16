package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.data.AuthSelectionStorage

class GetLastAuthorizedServiceUseCase internal constructor(
        private val authSelectionStorage: AuthSelectionStorage
) {
        operator fun invoke() = authSelectionStorage.lastAuthorizedService
}
