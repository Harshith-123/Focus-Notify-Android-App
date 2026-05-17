package com.focusnotify.app.service

import android.app.Notification
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.focusnotify.app.data.db.AppDatabase
import com.focusnotify.app.data.model.NotificationEntity
import com.focusnotify.app.engine.ScoringEngine
import com.focusnotify.app.util.CsvLogger
import com.focusnotify.app.util.PrefsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FocusNotificationService : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    // Apps to always ignore (system noise, our own app)
    private val IGNORED_PACKAGES = setOf(
        "android",
        "com.android.systemui",
        "com.android.phone",
        "com.focusnotify.app"
    )

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        // Ignore system/our app
        if (sbn.packageName in IGNORED_PACKAGES) return

        // Only process during active study session
        if (!PrefsHelper.isSessionActive(this)) return

        // Ignore ongoing/sticky notifications (music players, navigation, etc.)
        val isOngoing = (sbn.notification.flags and Notification.FLAG_ONGOING_EVENT) != 0
        if (isOngoing) return

        val extras  = sbn.notification.extras
        val title   = extras?.getString(Notification.EXTRA_TITLE) ?: ""
        val text    = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val pkg     = sbn.packageName
        val appName = getAppName(pkg)

        // Skip empty notifications
        if (title.isBlank() && text.isBlank()) return

        val mode      = PrefsHelper.getMode(this)
        val fomoScore = PrefsHelper.getFomoScore(this)
        val score     = ScoringEngine.calculateScore(pkg, title, text, fomoScore)

        val decision = when (mode) {
            PrefsHelper.MODE_BASELINE -> "DELIVERED"
            PrefsHelper.MODE_BATCHING -> "BATCHED"
            PrefsHelper.MODE_ADAPTIVE -> {
                if (ScoringEngine.shouldDeliver(pkg, title, text, fomoScore)) "DELIVERED" else "BATCHED"
            }
            else -> "DELIVERED"
        }

        scope.launch {
            // Save to Room DB
            AppDatabase.getDatabase(this@FocusNotificationService)
                .notificationDao()
                .insert(
                    NotificationEntity(
                        appPackage = pkg,
                        appName    = appName,
                        title      = title,
                        text       = text,
                        score      = score,
                        timestamp  = System.currentTimeMillis(),
                        condition  = mode,
                        decision   = decision,
                        fomoScore  = fomoScore
                    )
                )

            // Write to CSV
            CsvLogger.log(
                context    = this@FocusNotificationService,
                appPackage = pkg,
                appName    = appName,
                title      = title,
                text       = text,
                score      = score,
                decision   = decision,
                mode       = mode,
                fomoScore  = fomoScore
            )
        }
    }

    private fun getAppName(pkg: String): String {
        return try {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(pkg, 0)
            ).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            pkg
        }
    }
}
