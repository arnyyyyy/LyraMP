package com.arno.lyramp.feature.translation.domain

import com.arno.lyramp.feature.translation.model.TranslationResult
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

internal class GoogleTranslationParser {
        private val json = Json { ignoreUnknownKeys = true; isLenient = true }

        fun parse(responseString: String): TranslationResult {
                val translation = extractTranslation(responseString)
                val sourceLang = extractSourceLanguage(responseString)
                return TranslationResult(translation, sourceLang)
        }

        private fun extractTranslation(jsonString: String): String? {
                return try {
                        json.parseToJsonElement(jsonString).jsonArray
                                .firstOrNull()?.jsonArray
                                ?.firstOrNull()?.jsonArray
                                ?.firstOrNull()
                                ?.jsonPrimitive?.content
                } catch (_: Exception) {
                        null
                }
        }

        private fun extractSourceLanguage(jsonString: String): String? {
                return try {
                        json.parseToJsonElement(jsonString).jsonArray
                                .getOrNull(2)
                                ?.jsonPrimitive?.content
                } catch (_: Exception) {
                        null
                }
        }
}
