package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.data.AuthSelectionStorage

class GetLastAuthorizedServiceUseCase internal constructor() {
        operator fun invoke() = AuthSelectionStorage.lastAuthorizedService
}
