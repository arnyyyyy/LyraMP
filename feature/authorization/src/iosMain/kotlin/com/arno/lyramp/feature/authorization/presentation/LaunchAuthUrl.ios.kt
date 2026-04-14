package com.arno.lyramp.feature.authorization.presentation

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.presentation.yandex.launchYandexAuthWebView

actual fun launchAuthUrl(url: String, service: MusicServiceType) {
        when (service) {
                MusicServiceType.YANDEX -> launchYandexAuthWebView(url)

                else -> error("Unsupported music service: $service")
        }
}
