package com.arno.lyramp.core.background

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.arno.lyramp.util.Log
import java.util.concurrent.TimeUnit

class AndroidBackgroundTaskManager(private val context: Context) : BackgroundTaskManager {

        private val workManager
                get() = WorkManager.getInstance(context)

        override fun schedule(task: BackgroundTask, schedule: TaskSchedule) {
                val constraints = Constraints.Builder().setRequiredNetworkType(
                        if (task.constraints.requiresNetwork) NetworkType.CONNECTED
                        else NetworkType.NOT_REQUIRED
                )
                        .setRequiresCharging(task.constraints.requiresCharging)
                        .build()

                when (schedule) {
                        is TaskSchedule.OneTime -> {
                                val request = OneTimeWorkRequestBuilder<LyraBackgroundWorker>()
                                        .setInputData(workDataOf(KEY_TASK_ID to task.taskId))
                                        .setConstraints(constraints)
                                        .addTag(task.taskId)
                                        .build()
                                workManager.enqueueUniqueWork(
                                        task.taskId,
                                        ExistingWorkPolicy.REPLACE,
                                        request
                                )
                        }

                        is TaskSchedule.Periodic -> {
                                val request = PeriodicWorkRequestBuilder<LyraBackgroundWorker>(
                                        schedule.intervalMinutes, TimeUnit.MINUTES
                                )
                                        .setInputData(workDataOf(KEY_TASK_ID to task.taskId))
                                        .setConstraints(constraints)
                                        .addTag(task.taskId)
                                        .build()
                                workManager.enqueueUniquePeriodicWork(
                                        task.taskId,
                                        ExistingPeriodicWorkPolicy.UPDATE,
                                        request
                                )
                        }
                }
        }

        override fun cancel(taskId: String) {
                workManager.cancelUniqueWork(taskId)
        }

        override fun cancelAll() {
                workManager.cancelAllWork()
        }
}

class LyraBackgroundWorker(
        context: Context,
        params: WorkerParameters
) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
                val taskId = inputData.getString(KEY_TASK_ID) ?: return Result.failure()

                val task = BackgroundTaskRegistry.create(taskId, org.koin.mp.KoinPlatform.getKoin())
                if (task == null) {
                        Log.logger.e { "No task registered for id=$taskId" }
                        return Result.failure()
                }

                return try {
                        if (task.execute()) Result.success() else Result.retry()
                } catch (e: Exception) {
                        Log.logger.e(e) { "Background task failed: $taskId" }
                        Result.retry()
                }
        }
}

private const val KEY_TASK_ID = "background_task_id"