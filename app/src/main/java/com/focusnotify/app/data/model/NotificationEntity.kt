package com.focusnotify.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "batched_notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val appPackage: String,
    val appName: String,
    val title: String,
    val text: String,
    val score: Float,
    val timestamp: Long,
    val condition: String,   // BASELINE, BATCHING, ADAPTIVE
    val decision: String,    // DELIVERED or BATCHED
    val fomoScore: Int
)
