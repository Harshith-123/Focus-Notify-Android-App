package com.focusnotify.app.data.db

import androidx.room.*
import com.focusnotify.app.data.model.NotificationEntity

@Dao
interface NotificationDao {

    @Insert
    suspend fun insert(notification: NotificationEntity)

    @Query("SELECT * FROM batched_notifications WHERE decision = 'BATCHED' ORDER BY timestamp DESC")
    suspend fun getBatchedNotifications(): List<NotificationEntity>

    @Query("SELECT * FROM batched_notifications ORDER BY timestamp DESC LIMIT 200")
    suspend fun getAllNotifications(): List<NotificationEntity>

    @Query("DELETE FROM batched_notifications WHERE decision = 'BATCHED'")
    suspend fun clearBatched()

    @Query("SELECT COUNT(*) FROM batched_notifications WHERE decision = 'BATCHED'")
    suspend fun getBatchedCount(): Int
}
