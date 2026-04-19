package com.arno.lyramp.feature.authorization.presentation.yandex

import platform.Foundation.NSLog
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem

internal object YandexTokenExtractor {
        fun extractFromUrl(url: String): Boolean {
                if (url.isBlank() || !url.contains("access_token")) return false

                return try {
                        val fragmentIndex = url.indexOf('#')
                        if (fragmentIndex != -1) {
                                val fragment = url.substring(fragmentIndex + 1)
                                if (parseAndSaveToken(fragment)) return true
                        }

                        val components = NSURLComponents(string = url)
                        val token = components.queryItems
                                ?.filterIsInstance<NSURLQueryItem>()
                                ?.firstOrNull { it.name == "access_token" }
                                ?.value
                        val expiresIn = components.queryItems
                                ?.filterIsInstance<NSURLQueryItem>()
                                ?.firstOrNull { it.name == "expires_in" }
                                ?.value?.toLongOrNull()

                        if (!token.isNullOrEmpty()) {
                                YandexAuthBusProvider.get().emit(token, expiresIn)
                                return true
                        }

                        false
                } catch (e: Throwable) {
                        NSLog("YandexAuth: Token extraction error: ${e.message}")
                        false
                }
        }

        private fun parseAndSaveToken(fragmentString: String): Boolean {
                return try {
                        val fakeUrl = "https://dummy?$fragmentString"
                        val components = NSURLComponents(string = fakeUrl)
                        val queryItems = components.queryItems
                                ?.filterIsInstance<NSURLQueryItem>()
                                ?: return false

                        val accessToken = queryItems.firstOrNull { it.name == "access_token" }?.value
                        val expiresIn = queryItems.firstOrNull { it.name == "expires_in" }?.value?.toLongOrNull()

                        if (accessToken.isNullOrEmpty()) {
                                NSLog("YandexAuth: No access token in params")
                                return false
                        }

                        YandexAuthBusProvider.get().emit(accessToken, expiresIn)
                        true
                } catch (e: Throwable) {
                        NSLog("YandexAuth: Token parsing failed: ${e.message}")
                        false
                }
        }
}

