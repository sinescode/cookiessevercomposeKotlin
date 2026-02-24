package com.turjaun.serverstatuscookies

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.turjaun.serverstatuscookies.data.AppDatabase
import com.turjaun.serverstatuscookies.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "=== onMessageReceived CALLED ===")
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        // Check if message contains notification payload
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Message notification payload: ${notification.title} - ${notification.body}")
            handleNotificationMessage(
                title = notification.title,
                body = notification.body,
                data = remoteMessage.data
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "=== onNewToken CALLED ===")
        Log.d(TAG, "New FCM token: $token")

        // Save the new token to database
        saveDeviceToken(token)
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "Notification"
        val body = data["body"] ?: ""

        Log.d(TAG, "Handling data message: $title - $body")

        // Save to database
        saveNotificationToDatabase(title, body, data.toString(), "high")

        // Show local notification
        showNotification(title, body)
    }

    private fun handleNotificationMessage(title: String?, body: String?, data: Map<String, String>) {
        val notificationTitle = title ?: "Notification"
        val notificationBody = body ?: ""

        Log.d(TAG, "Handling notification message: $notificationTitle - $notificationBody")

        // Save to database
        saveNotificationToDatabase(
            notificationTitle,
            notificationBody,
            if (data.isNotEmpty()) data.toString() else null,
            "high"
        )

        // Show local notification
        showNotification(notificationTitle, notificationBody)
    }

    private fun saveNotificationToDatabase(
        title: String,
        body: String,
        data: String?,
        priority: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(applicationContext)
                val dao = database.notificationDao()
                val repository = NotificationRepository(dao)

                repository.addNotification(
                    title = title,
                    body = body,
                    data = data,
                    priority = priority
                )

                Log.d(TAG, "Notification saved to database: $title")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save notification to database", e)
            }
        }
    }

    private fun saveDeviceToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(applicationContext)
                val dao = database.deviceTokenDao()
                val deviceToken = com.turjaun.serverstatuscookies.data.DeviceToken(
                    token = token,
                    deviceName = Build.MODEL
                )
                dao.insertToken(deviceToken)
                Log.d(TAG, "Device token saved successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save device token", e)
            }
        }
    }

    private fun showNotification(title: String, body: String) {
        try {
            val notification = NotificationCompat.Builder(this, "high_priority_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                    NotificationManagerCompat.from(this).notify(
                        System.currentTimeMillis().toInt(),
                        notification
                    )
                }
            } else {
                NotificationManagerCompat.from(this).notify(
                    System.currentTimeMillis().toInt(),
                    notification
                )
            }

            Log.d(TAG, "Notification shown successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show notification", e)
        }
    }
}