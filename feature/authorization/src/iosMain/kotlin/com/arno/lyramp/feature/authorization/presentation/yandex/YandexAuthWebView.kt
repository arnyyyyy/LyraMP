package com.arno.lyramp.feature.authorization.presentation.yandex

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSLog
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.UIKit.UIApplication
import platform.UIKit.UIBarButtonItem
import platform.UIKit.UIBarButtonItemStyle
import platform.UIKit.UIColor
import platform.UIKit.UINavigationController
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.UIKit.navigationItem
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKWebsiteDataStore
import platform.WebKit.setJavaScriptEnabled
import platform.darwin.NSObject
import platform.objc.sel_registerName

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun launchYandexAuthWebView(authUrl: String) {
        val root = findRootViewController() ?: return

        val authVC = YandexAuthViewController(authUrl)
        val nav = UINavigationController(rootViewController = authVC)
        root.presentViewController(nav, true, null)
}

private fun findRootViewController(): UIViewController? {
        val app = UIApplication.sharedApplication
        val windowScene = app.connectedScenes
                .filterIsInstance<UIWindowScene>()
                .firstOrNull()

        val windows = windowScene?.windows?.filterIsInstance<UIWindow>()
        val keyWindow = windows?.firstOrNull { it.isKeyWindow() }
                ?: windows?.firstOrNull()

        return keyWindow?.rootViewController
                ?: app.delegate?.window?.rootViewController
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class YandexAuthViewController(
        private val authUrl: String
) : UIViewController(nibName = null, bundle = null) {

        private val webViewConfig = WKWebViewConfiguration().apply {
                websiteDataStore = WKWebsiteDataStore.defaultDataStore()
                preferences.setJavaScriptEnabled(true)
        }

        private val webView = WKWebView(
                frame = CGRectMake(0.0, 0.0, 0.0, 0.0),
                configuration = webViewConfig
        )

        private var webViewDelegate: YandexWebViewDelegate? = null

        private val closeTarget = object : NSObject() {
                @Suppress("unused")
                @ObjCAction
                fun closeAction() {
                        dismissViewControllerAnimated(true, null)
                }
        }

        override fun viewDidLoad() {
                super.viewDidLoad()
                setTitle("Яндекс Музыка")
                view.setBackgroundColor(UIColor.whiteColor)
                view.addSubview(webView)

                webViewDelegate = YandexWebViewDelegate { dismissViewControllerAnimated(true, null) }
                webView.navigationDelegate = webViewDelegate

                this.navigationItem().setRightBarButtonItem(
                        UIBarButtonItem(
                                title = "Закрыть",
                                style = UIBarButtonItemStyle.UIBarButtonItemStylePlain,
                                target = closeTarget,
                                action = sel_registerName("closeAction")
                        )
                )

                val nsUrl = NSURL.URLWithString(authUrl)

                if (nsUrl != null) webView.loadRequest(NSURLRequest.requestWithURL(nsUrl))
                else NSLog("YandexAuth: Failed to create auth URL")
        }

        override fun viewWillLayoutSubviews() {
                super.viewWillLayoutSubviews()
                val bounds = view.bounds
                webView.setFrame(
                        CGRectMake(
                                0.0, 0.0,
                                bounds.useContents { size.width },
                                bounds.useContents { size.height }
                        )
                )
        }
}
