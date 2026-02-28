package com.arno.lyramp.feature.authorization.presentation

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.arno.lyramp.feature.authorization.presentation.yandex.YandexAuthActivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun launchAuthUrl(url: String, service: MusicServiceType) {
        val context = AuthUrlLauncher.context

        when (service) {
                MusicServiceType.YANDEX -> {
                        val intent = Intent(context, YandexAuthActivity::class.java).apply {
                                putExtra(YandexAuthActivity.EXTRA_AUTH_URL, url)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                }

                MusicServiceType.SPOTIFY -> {
                        val customTabsIntent = CustomTabsIntent.Builder().build().apply {
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        customTabsIntent.launchUrl(context, url.toUri())
                }

                else -> error("Unsupported music service: $service")
        }
}

private object AuthUrlLauncher : KoinComponent {
        val context: Context by inject()
}
