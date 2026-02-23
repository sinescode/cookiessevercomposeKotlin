package com.turjaun.serverstatuscookies.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val body: String,
    val data: String?, // JSON string for custom data
    val timestamp: Long, // Epoch milliseconds
    val isRead: Boolean = false,
    val priority: String = "high" // high, normal, low
)