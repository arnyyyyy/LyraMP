package com.arno.lyramp.util

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.statement.request
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
        fun create(): HttpClient = HttpClient {
                expectSuccess = false

                install(ContentNegotiation) {
                        json(Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                        })
                }

                install(ResponseObserver) {
                        onResponse { response ->
                                Log.logger.i { "Ktor: Response - status=${response.status.value} url=${response.request.url} method=${response.request.method.value}" }
                        }
                }
        }
}
