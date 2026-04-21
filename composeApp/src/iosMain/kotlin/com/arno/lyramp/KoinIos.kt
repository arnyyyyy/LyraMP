package com.arno.lyramp

import com.arno.lyramp.core.background.IosBackgroundTaskManager
import com.arno.lyramp.core.background.BackgroundTaskRegistry
import com.arno.lyramp.core.background.TaskSchedule
import com.arno.lyramp.di.appModules
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

@Suppress("unused")
object KoinInitializer {
        fun initialize() {
                startKoin {
                        modules(appModules)
                }

                val taskManager = IosBackgroundTaskManager()
                taskManager.registerAllHandlers()
                val koin = KoinPlatform.getKoin()
                for (taskId in BackgroundTaskRegistry.allTaskIds()) {
                        val task = BackgroundTaskRegistry.create(taskId, koin) ?: continue
                        taskManager.schedule(task, TaskSchedule.Periodic(intervalMinutes = 60))
                }
        }
}
