package com.arno.lyramp.feature.authorization.presentation

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.presentation.yandex.launchYandexAuthWebView
import com.arno.lyramp.feature.authorization.presentation.yandex.YandexAuthBus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun launchAuthUrl(url: String, service: MusicServiceType) {
        when (service) {
                MusicServiceType.YANDEX -> launchYandexAuthWebView(url, AuthUrlLauncher.yandexAuthBus)

                else -> error("Unsupported music service: $service")
        }
}

private object AuthUrlLauncher : KoinComponent {
        val yandexAuthBus: YandexAuthBus by inject()
}
