package com.arno.lyramp.feature.authorization.presentation.yandex

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

internal fun createYandexAuthWebView(context: Context, onTokenFound: () -> Unit): WebView {
        val webView = WebView(context).apply {
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
                        userAgentString = "Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36 " +
                                "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                }
                isFocusable = true
                isFocusableInTouchMode = true
        }

        webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        val url = request?.url?.toString() ?: return false
                        return tryExtractToken(url, onTokenFound)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        url?.let { tryExtractToken(it, onTokenFound) }
                }

                override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                ) {
                        val url = request?.url?.toString()
                        if (url != null && tryExtractToken(url, onTokenFound)) return
                        super.onReceivedError(view, request, error)
                }
        }

        CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(webView, true)
        }

        return webView
}

private fun tryExtractToken(url: String, onTokenFound: () -> Unit): Boolean {
        val found = YandexTokenExtractor.extractFromUrl(url)
        if (found) onTokenFound()
        return found
}
