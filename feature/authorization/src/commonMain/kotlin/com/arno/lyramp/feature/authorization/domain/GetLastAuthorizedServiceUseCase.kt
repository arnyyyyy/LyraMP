package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.repository.AuthSelectionStorage

class GetLastAuthorizedServiceUseCase internal constructor() {
        operator fun invoke() = AuthSelectionStorage.lastAuthorizedService
}
