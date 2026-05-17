package com.focusnotify.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.focusnotify.app.R
import com.focusnotify.app.data.db.AppDatabase
import com.focusnotify.app.util.PrefsHelper

class DigestWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME  = "focusnotify_digest_worker"
        const val CHANNEL_ID = "focusnotify_digest_channel"
    }

    override suspend fun doWork(): Result {
        if (!PrefsHelper.isSessionActive(applicationContext)) return Result.success()

        val db      = AppDatabase.getDatabase(applicationContext)
        val batched = db.notificationDao().getBatchedNotifications()

        if (batched.isEmpty()) return Result.success()

        // Count by app name, show top 5
        val appCounts = batched
            .groupBy { it.appName }
            .mapValues { it.value.size }
            .entries
            .sortedByDescending { it.value }
            .take(5)

        val total      = batched.size
        val digestText = appCounts.joinToString("  |  ") { "${it.key}: ${it.value}" }

        showDigestNotification(
            title   = "📦 FocusNotify — $total notifications held back",
            content = digestText
        )

        // Clear batched after showing digest
        db.notificationDao().clearBatched()

        return Result.success()
    }

    private fun showDigestNotification(title: String, content: String) {
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "FocusNotify Digest",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Shows batched notification summaries"
        }
        nm.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        nm.notify(9001, notification)
    }
}
