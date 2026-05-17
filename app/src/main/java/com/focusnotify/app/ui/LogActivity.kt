package com.focusnotify.app.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.focusnotify.app.data.db.AppDatabase
import com.focusnotify.app.databinding.ActivityLogBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogBinding
    private val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Notification Log"

        loadLog()
    }

    private fun loadLog() {
        lifecycleScope.launch {
            val notifications = AppDatabase.getDatabase(this@LogActivity)
                .notificationDao()
                .getAllNotifications()

            val items = notifications.map { n ->
                val time    = timeFmt.format(Date(n.timestamp))
                val icon    = if (n.decision == "DELIVERED") "✅" else "📦"
                val score   = "%.1f".format(n.score)
                "$icon [$time] ${n.appName}\n   ${n.title.take(50)} — Score: $score | ${n.decision}"
            }

            if (items.isEmpty()) {
                binding.listView.adapter = ArrayAdapter(
                    this@LogActivity,
                    android.R.layout.simple_list_item_1,
                    listOf("No notifications logged yet. Start a study session.")
                )
            } else {
                binding.listView.adapter = ArrayAdapter(
                    this@LogActivity,
                    android.R.layout.simple_list_item_1,
                    items
                )
            }

            binding.tvCount.text = "Total: ${notifications.size} notifications | " +
                "Delivered: ${notifications.count { it.decision == "DELIVERED" }} | " +
                "Batched: ${notifications.count { it.decision == "BATCHED" }}"
        }
    }
}
