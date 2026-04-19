package com.arno.lyramp.feature.authorization.presentation.yandex

import android.util.Log

internal object YandexTokenExtractor {
        fun extractFromUrl(url: String): Boolean {
                if (url.isBlank() || !url.contains("access_token")) return false

                return try {
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

                        false
                } catch (e: Exception) {
                        Log.e(TAG, "Token extraction error: ${e.message}", e)
                        false
                }
        }

        private fun parseAndSaveToken(params: String): Boolean {
                return try {
                        val paramsMap = params.split("&").associate { param ->
                                val parts = param.split("=", limit = 2)
                                parts[0] to (parts.getOrNull(1) ?: "")
                        }

                        val accessToken = paramsMap["access_token"]
                        val expiresIn = paramsMap["expires_in"]?.toLongOrNull()

                        if (accessToken.isNullOrEmpty()) return false

                        YandexAuthBusProvider.get().emit(accessToken, expiresIn)
                        true
                } catch (e: Exception) {
                        Log.e(TAG, "Token parsing failed: ${e.message}", e)
                        false
                }
        }

        private const val TAG = "YandexTokenExtractor"
}
