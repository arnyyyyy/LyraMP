package com.arno.lyramp.core.background

import com.arno.lyramp.util.Log
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGProcessingTaskRequest
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.dateByAddingTimeInterval

private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

@OptIn(ExperimentalForeignApi::class)
class IosBackgroundTaskManager : BackgroundTaskManager {

        fun registerAllHandlers() {
                for (taskId in BackgroundTaskRegistry.allTaskIds()) {
                        BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
                                identifier = taskId,
                                usingQueue = null
                        ) { bgTask ->
                                if (bgTask == null) return@registerForTaskWithIdentifier
                                scope.launch {
                                        val task = BackgroundTaskRegistry.create(taskId)
                                        if (task == null) {
                                                bgTask.setTaskCompletedWithSuccess(false)
                                                return@launch
                                        }
                                        try {
                                                val success = task.execute()
                                                bgTask.setTaskCompletedWithSuccess(success)
                                        } catch (e: Exception) {
                                                Log.logger.e(e) { "background task failed: $taskId" }
                                                bgTask.setTaskCompletedWithSuccess(false)
                                        }
                                }
                        }
                }
        }

        override fun schedule(task: BackgroundTask, schedule: TaskSchedule) {
                val request = when (schedule) {
                        is TaskSchedule.OneTime -> {
                                BGProcessingTaskRequest(identifier = task.taskId).apply {
                                        requiresNetworkConnectivity = task.constraints.requiresNetwork
                                        requiresExternalPower = task.constraints.requiresCharging
                                }
                        }

                        is TaskSchedule.Periodic -> {
                                BGAppRefreshTaskRequest(identifier = task.taskId).apply {
                                        earliestBeginDate = NSDate().dateByAddingTimeInterval(
                                                schedule.intervalMinutes * 60.0
                                        )
                                }
                        }
                }

                try {
                        BGTaskScheduler.sharedScheduler.submitTaskRequest(request, error = null)
                } catch (e: Exception) {
                        Log.logger.e(e) { "Failed to schedule background task: ${task.taskId}" }
                }
        }

        override fun cancel(taskId: String) {
                BGTaskScheduler.sharedScheduler.cancelTaskRequestWithIdentifier(taskId)
        }

        override fun cancelAll() {
                BGTaskScheduler.sharedScheduler.cancelAllTaskRequests()
        }
}
