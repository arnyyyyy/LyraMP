package com.arno.lyramp.feature.music_streaming.api

import com.arno.lyramp.core.Secrets
import com.arno.lyramp.feature.music_streaming.model.DownloadInfo
import com.arno.lyramp.feature.music_streaming.model.TrackDownloadInfoResponse
import com.arno.lyramp.util.HmacSha256
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlin.io.encoding.Base64
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

 class YandexStreamingApi(private val client: HttpClient) {
        @OptIn(ExperimentalTime::class)
        suspend fun getTrackDownloadInfo(token: String, trackId: String): DownloadInfo {
                val ts = Clock.System.now().epochSeconds
                val codecs = "mp3"
                val quality = "hq"

                val signPayload = "$ts$trackId$quality${codecs}raw"
                val sign = generateSignForTrack(signPayload)

                val response = client.get("$BASE_URL/get-file-info") {
                        header(HttpHeaders.Authorization, "OAuth $token")
                        header(HttpHeaders.Accept, "application/json")
                        header("X-Yandex-Music-Client", "YandexMusicAndroid/24024312")

                        parameter("ts", ts)
                        parameter("trackId", trackId)
                        parameter("quality", quality)
                        parameter("codecs", codecs)
                        parameter("transports", "raw")
                        parameter("sign", sign)
                }

                if (!response.status.isSuccess()) error("HTTP ${response.status}")

                val body: TrackDownloadInfoResponse = response.body()
                val downloadInfo = body.result?.downloadInfo ?: error("downloadInfo is null")

                return when {
                        downloadInfo.url != null -> downloadInfo

                        !downloadInfo.urls.isNullOrEmpty() -> downloadInfo.copy(url = downloadInfo.urls.first())

                        else -> error("No download URLs available")
                }
        }

        private fun generateSignForTrack(message: String): String {
                val hmac = HmacSha256.compute(Secrets.YANDEX_SIGN_KEY, message)
                val base64 = Base64.encode(hmac)
                return if (base64.isNotEmpty()) base64.dropLast(1) else base64
        }

        private companion object {
                const val BASE_URL = "https://api.music.yandex.net"
        }
}
