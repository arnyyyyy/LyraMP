package com.arno.lyramp.core.background

interface BackgroundTaskManager {
        fun schedule(task: BackgroundTask, schedule: TaskSchedule = TaskSchedule.OneTime)
        fun cancel(taskId: String)
        fun cancelAll()
}
