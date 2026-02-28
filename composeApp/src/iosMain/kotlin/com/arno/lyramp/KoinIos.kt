package com.arno.lyramp

import com.arno.lyramp.di.appModules
import org.koin.core.context.startKoin

@Suppress("unused")
object KoinInitializer {
        fun initialize() {
                startKoin {
                        modules(appModules)
                }
        }
}
