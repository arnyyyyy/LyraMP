package com.arno.lyramp.feature.translation.api

import com.arno.lyramp.feature.translation.model.TranslationResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.client.request.headers
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class GoogleTranslationApi(private val client: HttpClient) {

        private val json = Json { ignoreUnknownKeys = true; isLenient = true }

        suspend fun getTranslation(text: String): TranslationResult {
                val url = "https://translate.googleapis.com/translate_a/single"

                val params = mapOf(
                        "client" to "gtx",
                        "sl" to "auto",
                        "tl" to "ru",
                        "dt" to "t",
                        "q" to text
                )

                try {
                        val responseString: String = client.get(url) {
                                params.forEach { parameter(it.key, it.value) }
                        }.body()

                        val translation = extractTranslation(responseString)
                        val sourceLang = extractSourceLanguage(responseString)

                        return TranslationResult(translation, sourceLang)

                } catch (e: ClientRequestException) {
                        if (e.response.status == HttpStatusCode.NotFound) {
                                return TranslationResult(null, null)
                        }
                        throw e
                } catch (e: Exception) {
                        throw e
                }
        }

        suspend fun getSpeech(text: String, sourceLang: String): ByteArray? {
                val url = "https://translate.google.com/translate_tts"

                return try {
                        val response = client.get(url) {
                                parameter("ie", "UTF-8")
                                parameter("tl", sourceLang)
                                parameter("client", "tw-ob")
                                parameter("q", text)

                                headers {
                                        append(HttpHeaders.UserAgent, "Mozilla/5.0")
                                        append(HttpHeaders.Referrer, "https://translate.google.com/")
                                        append(HttpHeaders.Accept, "audio/mpeg, audio/*")
                                }
                        }

                        if (response.status.isSuccess()) {
                                response.body()
                        } else {
                                null
                        }
                } catch (_: Exception) {
                        null
                }
        }

        private fun extractTranslation(jsonString: String): String? {
                return try {
                        val jsonElement = json.parseToJsonElement(jsonString)
                        jsonElement.jsonArray
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
                        val jsonElement = json.parseToJsonElement(jsonString)
                        jsonElement.jsonArray
                                .getOrNull(2)
                                ?.jsonPrimitive?.content
                } catch (_: Exception) {
                        null
                }
        }
}