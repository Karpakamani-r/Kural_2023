package com.w2c.kural.notificationwork

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWork(private val context: Context, workerParams: WorkerParameters) : Worker(
    context, workerParams
) {
    override fun doWork(): Result {
        MyNotificationManager.displayNotification(context)
        return Result.success()
    }
}