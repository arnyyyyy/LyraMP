package com.arno.lyramp.di

import com.arno.lyramp.util.HttpClientFactory
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> { HttpClientFactory.create() }
}

