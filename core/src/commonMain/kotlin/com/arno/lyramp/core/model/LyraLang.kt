package com.arno.lyramp.core.model

object LyraLang {
        val SUPPORTED = setOf("en", "fr", "de", "es", "it", "hu", "ja", "zh", "he", "ar")
        val CJK: Set<String> = setOf("ja", "zh")

        private val DISPLAY_NAMES: Map<String, String> = mapOf(
                "en" to "🇬🇧 Английский",
                "ru" to "🇷🇺 Русский",
                "es" to "🇪🇸 Испанский",
                "fr" to "🇫🇷 Французский",
                "de" to "🇩🇪 Немецкий",
                "it" to "🇮🇹 Итальянский",
                "pt" to "🇵🇹 Португальский",
                "ja" to "🇯🇵 Японский",
                "ko" to "🇰🇷 Корейский",
                "zh" to "🇨🇳 Китайский",
                "hu" to "🇭🇺 Венгерский",
                "iw" to "🇮🇱 Иврит",
                "he" to "🇮🇱 Иврит",
                "ar" to "🇸🇦 Арабский",
        )

        fun displayName(code: String) = DISPLAY_NAMES[code.lowercase()] ?: "🌍 $code"

        fun getLanguageFlag(languageCode: String): String {
                return when (languageCode.lowercase()) {
                        "en" -> "🇬🇧"
                        "es" -> "🇪🇸"
                        "fr" -> "🇫🇷"
                        "de" -> "🇩🇪"
                        "it" -> "🇮🇹"
                        "pt" -> "🇵🇹"
                        "ru" -> "🇷🇺"
                        "ja" -> "🇯🇵"
                        "ko" -> "🇰🇷"
                        "zh" -> "🇨🇳"
                        "ar" -> "🇸🇦"
                        "tr" -> "🇹🇷"
                        "pl" -> "🇵🇱"
                        "nl" -> "🇳🇱"
                        "sv" -> "🇸🇪"
                        "da" -> "🇩🇰"
                        "fi" -> "🇫🇮"
                        "no" -> "🇳🇴"
                        "cs" -> "🇨🇿"
                        "uk" -> "🇺🇦"
                        "el" -> "🇬🇷"
                        "he" -> "🇮🇱"
                        "iw" -> "🇮🇱"
                        "hu" -> "🇭🇺"
                        "hi" -> "🇮🇳"
                        "th" -> "🇹🇭"
                        "vi" -> "🇻🇳"
                        "id" -> "🇮🇩"
                        "ms" -> "🇲🇾"
                        else -> "🌐 + $languageCode"
                }
        }
}