package com.arno.lyramp.feature.authorization.presentation.yandex

actual fun registerYandexAuthCallback(callback: (token: String, expiresIn: Long?) -> Unit) {
        YandexAuthHolder.callback = callback
}
