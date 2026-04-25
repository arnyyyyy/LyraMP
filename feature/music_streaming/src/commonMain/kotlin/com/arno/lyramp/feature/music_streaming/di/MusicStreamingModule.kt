package com.arno.lyramp.feature.music_streaming.di

import com.arno.lyramp.feature.music_streaming.api.YandexStreamingApi
import com.arno.lyramp.feature.music_streaming.domain.StreamingServiceFactory
import com.arno.lyramp.feature.music_streaming.domain.YandexStreamingService
import com.arno.lyramp.feature.music_streaming.domain.GetStreamingInfoUseCase
import org.koin.dsl.module

val musicStreamingModule = module {
        single<YandexStreamingApi> { YandexStreamingApi(get()) }

        single<YandexStreamingService> { YandexStreamingService(get(), get()) }

        single<StreamingServiceFactory> { StreamingServiceFactory(get(), get()) }

        single<GetStreamingInfoUseCase> { GetStreamingInfoUseCase(get()) }
}
