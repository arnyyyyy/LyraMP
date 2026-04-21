package com.arno.lyramp.core.background

import org.koin.core.Koin

object BackgroundTaskRegistry {
        private val factories = mutableMapOf<String, (Koin) -> BackgroundTask>()

        fun register(taskId: String, factory: (Koin) -> BackgroundTask) {
                factories[taskId] = factory
        }

        fun create(taskId: String, koin: Koin): BackgroundTask? = factories[taskId]?.invoke(koin)
        fun allTaskIds(): Set<String> = factories.keys.toSet()
}
