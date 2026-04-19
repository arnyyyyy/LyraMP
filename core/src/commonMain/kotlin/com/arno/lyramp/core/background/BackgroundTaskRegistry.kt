package com.arno.lyramp.core.background

object BackgroundTaskRegistry {
        private val factories = mutableMapOf<String, () -> BackgroundTask>()

        fun register(taskId: String, factory: () -> BackgroundTask) {
                factories[taskId] = factory
        }

        fun create(taskId: String): BackgroundTask? = factories[taskId]?.invoke()
        fun allTaskIds(): Set<String> = factories.keys.toSet()
}
