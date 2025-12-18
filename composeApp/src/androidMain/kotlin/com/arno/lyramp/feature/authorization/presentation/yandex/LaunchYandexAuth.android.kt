package com.arno.lyramp.feature.authorization.presentation.yandex

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.arno.lyramp.MainActivity
import androidx.core.graphics.drawable.toDrawable

actual fun launchYandexAuth() {
        val context = MainActivity.instance ?: return

        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(Color.WHITE.toDrawable())

        val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        }

        val closeButton = AppCompatButton(context).apply {
                text = "Закрыть"
                setOnClickListener { dialog.dismiss() }
                layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

        val webView = createAuthWebView(context)
        webView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        )

        webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        val url = request?.url?.toString() ?: return false
                        return tryExtractTokenAndClose(url, dialog)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        url?.let { tryExtractTokenAndClose(it, dialog) }
                }

                override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                ) {
                        val url = request?.url?.toString()
                        if (url != null && tryExtractTokenAndClose(url, dialog)) {
                                return
                        }
                        super.onReceivedError(view, request, error)
                }
        }

        layout.addView(closeButton)
        layout.addView(webView)

        dialog.setContentView(layout)
        dialog.setCancelable(true)
        dialog.show()

        CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(webView, true)
        }

        webView.loadUrl(AUTH_URL)
}

private fun createAuthWebView(context: Context): WebView =
        WebView(context).apply {
                @Suppress("SetJavaScriptEnabled")
                settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        setSupportZoom(true)
                        builtInZoomControls = false
                        displayZoomControls = false
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        javaScriptCanOpenWindowsAutomatically = true
                        mediaPlaybackRequiresUserGesture = false
                        userAgentString = "Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                }

                isFocusable = true
                isFocusableInTouchMode = true
        }

private fun tryExtractTokenAndClose(url: String, dialog: Dialog): Boolean {
        if (url.isBlank()) return false
        if (!url.contains("access_token")) return false

        try {
                val fragmentIndex = url.indexOf('#')
                if (fragmentIndex != -1) {
                        val fragment = url.substring(fragmentIndex + 1)
                        if (parseAndSaveToken(fragment)) {
                                dialog.dismiss()
                                return true
                        }
                }

                val tokenStart = url.indexOf("access_token=")
                if (tokenStart != -1) {
                        val tokenPart = url.substring(tokenStart)
                        if (parseAndSaveToken(tokenPart)) {
                                dialog.dismiss()
                                return true
                        }
                }
        } catch (e: Exception) {
                Log.e("YandexAuth", "Token extraction error: ${e.message}", e)
        }

        return false
}

private fun parseAndSaveToken(params: String): Boolean {
        try {
                val paramsMap = params.split("&").associate { param ->
                        val parts = param.split("=", limit = 2)
                        parts[0] to (parts.getOrNull(1) ?: "")
                }

                val accessToken = paramsMap["access_token"]
                val expiresInStr = paramsMap["expires_in"]

                if (accessToken.isNullOrEmpty()) {
                        return false
                }

                val expiresIn = expiresInStr?.toLongOrNull()

                YandexAuthHolder.invokeCallback(accessToken, expiresIn)
                return true
        } catch (e: Exception) {
                Log.e("YandexAuth", "Token parsing failed: ${e.message}", e)
                return false
        }
}

private const val AUTH_URL = "https://passport.yandex.ru/auth?origin=music_app" +
        "&retpath=https%3A%2F%2Foauth.yandex.ru%2Fauthorize%3Fresponse_type%3Dtoken" +
        "%26client_id%3D23cabbbdc6cd418abb4b39c32c41195d%26redirect_uri" +
        "%3Dhttps%253A%252F%252Fmusic.yandex.ru%252F%26force_confirm" +
        "%3DFalse%26language%3Dru"
