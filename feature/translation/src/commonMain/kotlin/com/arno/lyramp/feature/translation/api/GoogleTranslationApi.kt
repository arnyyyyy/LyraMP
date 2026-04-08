package com.arno.lyramp.feature.translation.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders

internal class GoogleTranslationApi(private val client: HttpClient) {

        suspend fun getTranslationRaw(text: String): String {
                return client.get("https://translate.googleapis.com/translate_a/single") {
                        parameter("client", "gtx")
                        parameter("sl", "auto")
                        parameter("tl", "ru")
                        parameter("dt", "t")
                        parameter("q", text)
                }.body()
        }

        suspend fun getSpeech(text: String, sourceLang: String): ByteArray {
                return client.get("https://translate.google.com/translate_tts") {
                        parameter("ie", "UTF-8")
                        parameter("tl", sourceLang)
                        parameter("client", "tw-ob")
                        parameter("q", text)
                        headers {
                                append(HttpHeaders.UserAgent, "Mozilla/5.0")
                                append(HttpHeaders.Referrer, "https://translate.google.com/")
                                append(HttpHeaders.Accept, "audio/mpeg, audio/*")
                        }
                }.body()
        }
}