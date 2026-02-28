package com.arno.lyramp.feature.authorization.presentation

import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.arno.lyramp.feature.authorization.presentation.yandex.launchYandexAuthWebView
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun launchAuthUrl(url: String, service: MusicServiceType) {
        when (service) {
                MusicServiceType.YANDEX -> launchYandexAuthWebView(url)

                MusicServiceType.SPOTIFY -> {
                        val nsUrl = NSURL.URLWithString(url) ?: return
                        UIApplication.sharedApplication.openURL(nsUrl)
                }

                else -> error("Unsupported music service: $service")
        }
}
