package com.arno.lyramp

import android.app.Application
import com.arno.lyramp.core.background.AndroidBackgroundTaskManager
import com.arno.lyramp.core.background.BackgroundTaskRegistry
import com.arno.lyramp.core.background.TaskSchedule
import com.arno.lyramp.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.mp.KoinPlatform

class LyraMPApplication : Application() {
        override fun onCreate() {
                super.onCreate()

                startKoin {
                        androidLogger(Level.ERROR)
                        androidContext(this@LyraMPApplication)
                        modules(appModules)
                }

                val taskManager = AndroidBackgroundTaskManager(this)
                val koin = KoinPlatform.getKoin()
                for (taskId in BackgroundTaskRegistry.allTaskIds()) {
                        val task = BackgroundTaskRegistry.create(taskId, koin) ?: continue
                        taskManager.schedule(task, TaskSchedule.Periodic(intervalMinutes = 60))
                }
        }
}
