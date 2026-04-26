package com.arno.lyramp.core.model

object LyraLang {
        val SUPPORTED = setOf("en", "fr", "de", "es", "it", "hu", "ja", "zh", "he", "ar")
        val CJK: Set<String> = setOf("ja", "zh")

        val LATIN_FOLD: Map<Char, String> = mapOf(
                // lowercase
                'à' to "a", 'á' to "a", 'â' to "a", 'ã' to "a", 'ä' to "a", 'å' to "a", 'ā' to "a", 'ă' to "a", 'ą' to "a",
                'ç' to "c", 'ć' to "c", 'č' to "c",
                'ď' to "d",
                'è' to "e", 'é' to "e", 'ê' to "e", 'ë' to "e", 'ē' to "e", 'ė' to "e", 'ę' to "e",
                'ì' to "i", 'í' to "i", 'î' to "i", 'ï' to "i", 'ī' to "i", 'į' to "i",
                'ł' to "l",
                'ñ' to "n", 'ń' to "n",
                'ò' to "o", 'ó' to "o", 'ô' to "o", 'õ' to "o", 'ö' to "o", 'ø' to "o", 'ō' to "o", 'ő' to "o",
                'ř' to "r",
                'ś' to "s", 'š' to "s", 'ß' to "ss",
                'ť' to "t",
                'ù' to "u", 'ú' to "u", 'û' to "u", 'ü' to "u", 'ū' to "u", 'ů' to "u", 'ű' to "u",
                'ý' to "y", 'ÿ' to "y",
                'ž' to "z", 'ź' to "z", 'ż' to "z",
                'À' to "A", 'Á' to "A", 'Â' to "A", 'Ã' to "A", 'Ä' to "A", 'Å' to "A", 'Ā' to "A", 'Ă' to "A", 'Ą' to "A",
                'Ç' to "C", 'Ć' to "C", 'Č' to "C",
                'Ď' to "D",
                'È' to "E", 'É' to "E", 'Ê' to "E", 'Ë' to "E", 'Ē' to "E", 'Ė' to "E", 'Ę' to "E",
                'Ì' to "I", 'Í' to "I", 'Î' to "I", 'Ï' to "I", 'Ī' to "I", 'Į' to "I",
                'Ł' to "L",
                'Ñ' to "N", 'Ń' to "N",
                'Ò' to "O", 'Ó' to "O", 'Ô' to "O", 'Õ' to "O", 'Ö' to "O", 'Ø' to "O", 'Ō' to "O", 'Ő' to "O",
                'Ř' to "R",
                'Ś' to "S", 'Š' to "S",
                'Ť' to "T",
                'Ù' to "U", 'Ú' to "U", 'Û' to "U", 'Ü' to "U", 'Ū' to "U", 'Ů' to "U", 'Ű' to "U",
                'Ý' to "Y", 'Ÿ' to "Y",
                'Ž' to "Z", 'Ź' to "Z", 'Ż' to "Z",
        )

        fun String.foldLatinDiacritics(): String = buildString {
                for (char in this@foldLatinDiacritics) {
                        append(LATIN_FOLD[char] ?: char.toString())
                }
        }

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