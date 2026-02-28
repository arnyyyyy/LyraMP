package com.arno.lyramp.di

import com.arno.lyramp.feature.music_streaming.api.YandexStreamingApi
import com.arno.lyramp.feature.music_streaming.domain.AppleStreamingService
import com.arno.lyramp.feature.music_streaming.domain.SpotifyStreamingService
import com.arno.lyramp.feature.music_streaming.domain.StreamingServiceFactory
import com.arno.lyramp.feature.music_streaming.domain.YandexStreamingService
import com.arno.lyramp.feature.music_streaming.domain.GetStreamingInfoUseCase
import org.koin.dsl.module

val musicStreamingModule = module {
        single<YandexStreamingApi> { YandexStreamingApi(get()) }

        single<YandexStreamingService> { YandexStreamingService(get(), get()) }
        single<SpotifyStreamingService> { SpotifyStreamingService() }
        single<AppleStreamingService> { AppleStreamingService() }

        single<StreamingServiceFactory> { StreamingServiceFactory(get(), get(), get()) }

        single<GetStreamingInfoUseCase> { GetStreamingInfoUseCase(get()) }
}
