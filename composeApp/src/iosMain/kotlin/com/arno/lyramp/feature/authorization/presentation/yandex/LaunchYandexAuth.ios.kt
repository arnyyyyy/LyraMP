package com.arno.lyramp.feature.authorization.presentation.yandex

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.cinterop.ObjCAction
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.NSLog
import platform.Foundation.NSError
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UINavigationController
import platform.UIKit.UIBarButtonItem
import platform.CoreGraphics.CGRectMake
import platform.UIKit.navigationItem
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKWebsiteDataStore
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigation
import platform.WebKit.setJavaScriptEnabled
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun launchYandexAuth() {
        val app = UIApplication.sharedApplication
        val root = app.keyWindow?.rootViewController ?: app.delegate?.window?.rootViewController
        if (root == null) return

        val authVC = YandexAuthViewController()
        val nav = UINavigationController(rootViewController = authVC)
        root.presentViewController(nav, true, null)
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class YandexAuthViewController : UIViewController(nibName = null, bundle = null) {
        private val webViewConfig = WKWebViewConfiguration().apply {
                websiteDataStore = WKWebsiteDataStore.defaultDataStore()
                preferences.setJavaScriptEnabled(true)
                preferences.setJavaScriptCanOpenWindowsAutomatically(true)
        }

        private val webView = WKWebView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = webViewConfig)

        private var navigationDelegate: YandexWebViewDelegate? = null

        private val closeTarget = object : NSObject() {
                @ObjCAction
                fun closeAction() {
                        dismissViewControllerAnimated(true, null)
                }
        }

        override fun viewDidLoad() {
                super.viewDidLoad()
                setTitle("Яндекс Музыка")

                view.setBackgroundColor(platform.UIKit.UIColor.whiteColor)
                view.addSubview(webView)

                navigationDelegate = YandexWebViewDelegate {
                        dismissViewControllerAnimated(true, null)
                }
                webView.navigationDelegate = navigationDelegate

                val closeItem = UIBarButtonItem(
                        title = "Закрыть",
                        style = platform.UIKit.UIBarButtonItemStyle.UIBarButtonItemStylePlain,
                        target = closeTarget,
                        action = platform.objc.sel_registerName("closeAction")
                )

                this.navigationItem().setRightBarButtonItem(closeItem)

                val authUrl = NSURL.URLWithString(AUTH_URL)
                if (authUrl != null) {
                        val req = NSURLRequest.requestWithURL(authUrl)
                        webView.loadRequest(req)
                } else {
                        NSLog("YandexAuth: Failed to create auth URL")
                }
        }

        override fun viewWillLayoutSubviews() {
                super.viewWillLayoutSubviews()
                val bounds = view.bounds
                webView.setFrame(CGRectMake(0.0, 0.0, bounds.useContents { size.width }, bounds.useContents { size.height }))
        }
}

@OptIn(ExperimentalForeignApi::class)
private class YandexWebViewDelegate(
        private val onTokenFound: () -> Unit
) : NSObject(), WKNavigationDelegateProtocol {

        override fun webView(
                webView: WKWebView,
                decidePolicyForNavigationAction: WKNavigationAction,
                decisionHandler: (WKNavigationActionPolicy) -> Unit
        ) {
                val requestUrl = decidePolicyForNavigationAction.request.URL?.absoluteString ?: ""
                NSLog("YandexAuth navigation to: $requestUrl")

                if (tryExtractToken(requestUrl)) {
                        onTokenFound()
                        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                        return
                }

                decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
        }

        override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
                val currentUrl = webView.URL?.absoluteString ?: ""
                NSLog("YandexAuth page loaded: $currentUrl")

                if (tryExtractToken(currentUrl)) {
                        onTokenFound()
                }
        }

        override fun webView(
                webView: WKWebView,
                didFailProvisionalNavigation: WKNavigation?,
                withError: NSError
        ) {
                val failedUrl = withError.userInfo["NSErrorFailingURLStringKey"] as? String
                NSLog("YandexAuth provisional navigation failed, URL: $failedUrl")
                if (failedUrl != null && tryExtractToken(failedUrl)) {
                        onTokenFound()
                }
        }
}

@OptIn(ExperimentalForeignApi::class)
private fun tryExtractToken(url: String): Boolean {
        if (url.isBlank()) return false
        if (!url.contains("access_token")) return false

        NSLog("YandexAuth: Found potential token URL: $url")

        try {
                val fragmentIndex = url.indexOf('#')
                if (fragmentIndex != -1) {
                        val fragment = url.substring(fragmentIndex + 1)
                        if (parseAndSaveToken(fragment)) return true
                }

                val tokenStart = url.indexOf("access_token=")
                if (tokenStart != -1) {
                        val tokenPart = url.substring(tokenStart)
                        if (parseAndSaveToken(tokenPart)) return true
                }
        } catch (e: Throwable) {
                NSLog("YandexAuth: Token extraction error: ${e.message}")
        }

        return false
}

@OptIn(ExperimentalForeignApi::class)
private fun parseAndSaveToken(params: String): Boolean {
        try {
                val paramsMap = params.split("&").associate { param ->
                        val parts = param.split("=", limit = 2)
                        parts[0] to (parts.getOrNull(1) ?: "")
                }

                val accessToken = paramsMap["access_token"]
                val expiresInStr = paramsMap["expires_in"]

                if (accessToken.isNullOrEmpty()) {
                        NSLog("YandexAuth: No access token in params")
                        return false
                }

                val expiresIn = expiresInStr?.toLongOrNull()
                NSLog("YandexAuth: Successfully extracted token, length: ${accessToken.length}, expiresIn: $expiresIn")

                YandexAuthHolder.invokeCallback(accessToken, expiresIn)
                return true
        } catch (e: Throwable) {
                NSLog("YandexAuth: Token parsing failed: ${e.message}")
                return false
        }
}

private const val AUTH_URL = "https://passport.yandex.ru/auth?origin=music_app" +
        "&retpath=https%3A%2F%2Foauth.yandex.ru%2Fauthorize%3Fresponse_type%3Dtoken" +
        "%26client_id%3D23cabbbdc6cd418abb4b39c32c41195d%26redirect_uri" +
        "%3Dhttps%253A%252F%252Fmusic.yandex.ru%252F%26force_confirm" +
        "%3DFalse%26language%3Dru"
