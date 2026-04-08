package com.arno.lyramp.feature.lyrics.api

import com.arno.lyramp.core.Secrets
import com.arno.lyramp.feature.lyrics.model.LyricsType
import com.arno.lyramp.feature.lyrics.model.YandexLyricsResponse
import com.arno.lyramp.util.HmacSha256
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlin.io.encoding.Base64
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

 class YandexLyricsApi(private val client: HttpClient) {
        private val json = Json { ignoreUnknownKeys = true }

        @OptIn(ExperimentalTime::class)
        suspend fun getLyrics(token: String, trackId: String, type: LyricsType = LyricsType.PLAIN): String {
                val ts = Clock.System.now().epochSeconds
                val sign = generateSignForLyrics("$trackId$ts")

                val response = client.get("${BASE_URL}/tracks/$trackId/lyrics") {
                        header(HttpHeaders.Authorization, "OAuth $token")
                        header(HttpHeaders.Accept, "application/json")
                        header("X-Yandex-Music-Client", "YandexMusicAndroid/24024312")
                        parameter("format", type.value)
                        parameter("timeStamp", ts)
                        parameter("sign", sign)
                }

                if (!response.status.isSuccess()) error("HTTP ${response.status}")

                val lyricsResponse = json.decodeFromString<YandexLyricsResponse>(
                        response.bodyAsText()
                )
                val downloadUrl = lyricsResponse.result?.downloadUrl ?: error("downloadUrl is null")
                return client.get(downloadUrl).bodyAsText()
        }

        private fun generateSignForLyrics(message: String): String {
                val hmac = HmacSha256.compute(Secrets.YANDEX_SIGN_KEY, message)
                return Base64.encode(hmac)
        }

        private companion object {
                const val BASE_URL = "https://api.music.yandex.net"
        }
}
