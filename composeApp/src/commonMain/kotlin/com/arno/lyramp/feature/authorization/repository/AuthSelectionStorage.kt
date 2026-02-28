package com.arno.lyramp.feature.authorization.repository

import com.russhwolf.settings.Settings

internal object AuthSelectionStorage {
        private val settings = Settings()

        var lastAuthorizedService: String?
                get() = settings.getStringOrNull("last_authorized_service")
                set(value) {
                        if (value == null) settings.remove("last_authorized_service")
                        else settings.putString("last_authorized_service", value)
                }
}

