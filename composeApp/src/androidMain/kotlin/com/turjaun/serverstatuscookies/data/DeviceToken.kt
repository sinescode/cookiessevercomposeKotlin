package com.turjaun.serverstatuscookies.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_tokens")
data class DeviceToken(
    @PrimaryKey val token: String,
    val deviceName: String,
    val addedAt: Long = System.currentTimeMillis()
)