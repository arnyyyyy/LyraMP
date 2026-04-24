package com.arno.lyramp.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.statement.request
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import com.arno.lyramp.util.Log

val iosNetworkModule = module {
        single<HttpClient> {
                HttpClient(Darwin) {
                        engine {
                                configureSession {
                                        allowsCellularAccess = true
                                        allowsExpensiveNetworkAccess = true
                                        allowsConstrainedNetworkAccess = true
                                }
                        }

                        expectSuccess = false

                        install(HttpTimeout) {
                                connectTimeoutMillis = 15_000
                                requestTimeoutMillis = 30_000
                                socketTimeoutMillis = 30_000
                        }

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
}
