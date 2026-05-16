package com.arno.lyramp.feature.authorization.presentation.yandex

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class YandexAuthActivity : ComponentActivity(), KoinComponent {
        private val yandexAuthBus: YandexAuthBus by inject()

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                val authUrl = intent?.getStringExtra(EXTRA_AUTH_URL)
                if (authUrl.isNullOrBlank()) {
                        finish()
                        return
                }

                val webView = createYandexAuthWebView(
                        context = this,
                        authBus = yandexAuthBus,
                        onTokenFound = ::finish
                )
                webView.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
                )

                val closeButton = Button(this).apply {
                        text = "Закрыть"
                        setOnClickListener { finish() }
                        layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                }

                val layout = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        addView(closeButton)
                        addView(webView)
                }

                setContentView(layout)
                webView.loadUrl(authUrl)
        }

        internal companion object {
                const val EXTRA_AUTH_URL = "auth_url"
        }
}
