package com.arno.lyramp.feature.authorization.presentation.yandex

expect fun registerYandexAuthCallback(callback: (token: String, expiresIn: Long?) -> Unit)

object YandexAuthHolder {
        var callback: ((token: String, expiresIn: Long?) -> Unit)? = null
                set(value) {
                        field = value
                }

        fun invokeCallback(token: String, expiresIn: Long?) {
                callback?.invoke(token, expiresIn)
        }
}
