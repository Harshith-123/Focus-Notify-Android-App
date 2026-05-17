package com.focusnotify.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.work.*
import com.focusnotify.app.R
import com.focusnotify.app.databinding.ActivityMainBinding
import com.focusnotify.app.util.CsvLogger
import com.focusnotify.app.util.PrefsHelper
import com.focusnotify.app.worker.DigestWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateUI()

        binding.btnSession.setOnClickListener {
            if (PrefsHelper.isSessionActive(this)) {
                stopSession()
            } else {
                if (!isNotificationServiceEnabled()) {
                    Toast.makeText(this,
                        "⚠ Please enable FocusNotify in Notification Access first",
                        Toast.LENGTH_LONG).show()
                    startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                } else {
                    startSession()
                }
            }
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnViewLog.setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }

        binding.btnShareLog.setOnClickListener {
            shareLog()
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun startSession() {
        PrefsHelper.setSessionActive(this, true)

        val request = PeriodicWorkRequestBuilder<DigestWorker>(30, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DigestWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )

        updateUI()
        Toast.makeText(this, "✅ Study session started!", Toast.LENGTH_SHORT).show()
    }

    private fun stopSession() {
        PrefsHelper.setSessionActive(this, false)
        WorkManager.getInstance(this).cancelUniqueWork(DigestWorker.WORK_NAME)
        updateUI()
        Toast.makeText(this, "Session ended. Great work!", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        val active = PrefsHelper.isSessionActive(this)
        val mode   = PrefsHelper.getMode(this)
        val fomo   = PrefsHelper.getFomoScore(this)
        val pid    = PrefsHelper.getParticipantId(this)

        if (active) {
            binding.btnSession.text = "⏹ STOP SESSION"
            binding.btnSession.setBackgroundColor(getColor(R.color.red_stop))
            binding.tvStatus.text = "🔴 Session active — tracking notifications"
        } else {
            binding.btnSession.text = "▶ START SESSION"
            binding.btnSession.setBackgroundColor(getColor(R.color.blue_start))
            binding.tvStatus.text = "⚪ Session not active"
        }

        binding.tvInfo.text = "Mode: $mode   |   FoMO: $fomo   |   ID: $pid"
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners") ?: return false
        return flat.contains(packageName)
    }

    private fun shareLog() {
        val files = CsvLogger.getAllLogFiles(this)
        if (files.isEmpty()) {
            Toast.makeText(this, "No log files yet. Start a session first.", Toast.LENGTH_SHORT).show()
            return
        }
        val file = files.first()
        val uri: Uri = FileProvider.getUriForFile(this, "com.focusnotify.app.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "FocusNotify Log — ${PrefsHelper.getParticipantId(this@MainActivity)}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Log File"))
    }
}
