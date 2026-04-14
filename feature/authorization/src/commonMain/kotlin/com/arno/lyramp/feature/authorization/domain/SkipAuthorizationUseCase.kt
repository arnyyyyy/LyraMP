package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.repository.AuthSelectionStorage

class SkipAuthorizationUseCase internal constructor() {
        operator fun invoke() {
                AuthSelectionStorage.lastAuthorizedService = MusicServiceType.NONE.name
        }
}
