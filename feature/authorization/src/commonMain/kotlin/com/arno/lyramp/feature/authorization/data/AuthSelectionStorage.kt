package com.arno.lyramp.feature.authorization.data

import com.russhwolf.settings.Settings

internal object AuthSelectionStorage {
        private val settings = Settings()

        var lastAuthorizedService: String?
                get() = settings.getStringOrNull(LAST_SERVICE_KEY)
                set(value) {
                        if (value == null) settings.remove(LAST_SERVICE_KEY)
                        else settings.putString(LAST_SERVICE_KEY, value)
                }

        private const val LAST_SERVICE_KEY = "last_authorized_service"
}
