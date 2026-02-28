package com.arno.lyramp.feature.authorization.presentation.yandex

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSError
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
internal class YandexWebViewDelegate(
        private val onTokenFound: () -> Unit
) : NSObject(), WKNavigationDelegateProtocol {

        private var tokenHandled = false

        private fun tryHandleToken(url: String): Boolean {
                if (tokenHandled) return false
                val extracted = YandexTokenExtractor.extractFromUrl(url)
                if (extracted) {
                        tokenHandled = true
                        onTokenFound()
                }
                return extracted
        }

        override fun webView(
                webView: WKWebView,
                decidePolicyForNavigationAction: WKNavigationAction,
                decisionHandler: (WKNavigationActionPolicy) -> Unit
        ) {
                val url = decidePolicyForNavigationAction.request.URL?.absoluteString ?: ""

                if (tryHandleToken(url)) {
                        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                        return
                }

                decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
        }

        override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
                val url = webView.URL?.absoluteString ?: ""
                tryHandleToken(url)
        }

        override fun webView(
                webView: WKWebView,
                didFailProvisionalNavigation: WKNavigation?,
                withError: NSError
        ) {
                val failedUrl = withError.userInfo["NSErrorFailingURLStringKey"] as? String
                if (failedUrl != null) tryHandleToken(failedUrl)
        }
}
