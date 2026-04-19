package com.arno.lyramp.core.background

interface BackgroundTask {
        val taskId: String
        val constraints: TaskConstraints get() = TaskConstraints()

        suspend fun execute(): Boolean
}

data class TaskConstraints(
        val requiresNetwork: Boolean = false,
        val requiresCharging: Boolean = false,
)

sealed interface TaskSchedule {
        data object OneTime : TaskSchedule
        data class Periodic(val intervalMinutes: Long = 60) : TaskSchedule
}
