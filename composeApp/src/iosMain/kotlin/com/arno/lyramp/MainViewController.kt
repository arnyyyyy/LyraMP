package com.arno.lyramp

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

@Suppress("unused")
object MainViewControllerFactory {
        fun create(): UIViewController = ComposeUIViewController { App() }
}
