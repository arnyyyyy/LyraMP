package com.arno.lyramp.feature.authorization.presentation

import android.content.Context
import android.content.Intent
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
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


                else -> error("Unsupported music service: $service")
        }
}

private object AuthUrlLauncher : KoinComponent {
        val context: Context by inject()
}
