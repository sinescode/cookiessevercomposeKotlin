package com.turjaun.serverstatuscookies

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.turjaun.serverstatuscookies.data.AppDatabase
import com.turjaun.serverstatuscookies.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        // Initialize NotifierManager with proper configuration
        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
                showPushNotification = true, // This shows system notification automatically
            )
        )

        // Set up the global notification listener
        // This works for both foreground and background notifications
        setupNotificationListener()
    }

    private fun setupNotificationListener() {
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                Log.d("FCM", "New token received: $token")
                // Optionally save the new token to your database
                saveDeviceToken(token)
            }

            override fun onPushNotificationReceived(notification: com.mmk.kmpnotifier.notification.PushNotification) {
                Log.d("FCM", "Notification received in foreground: ${notification.title}")

                // Save notification to database
                saveNotificationToDatabase(
                    title = notification.title ?: "No title",
                    body = notification.body ?: "No body",
                    data = notification.data?.toString(),
                    priority = "high"
                )

                // Show local notification for foreground notifications
                showForegroundNotification(
                    title = notification.title ?: "Notification",
                    body = notification.body ?: ""
                )
            }

            override fun onPushNotification(title: String?, body: String?) {
                Log.d("FCM", "Notification received (legacy): $title")

                // Save notification to database (fallback for older API)
                saveNotificationToDatabase(
                    title = title ?: "No title",
                    body = body ?: "No body",
                    priority = "high"
                )
            }
        })
    }

    private fun saveDeviceToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = database.deviceTokenDao()
                val deviceToken = com.turjaun.serverstatuscookies.data.DeviceToken(
                    token = token,
                    deviceName = Build.MODEL
                )
                // Use the DAO directly since there's no repository for DeviceToken
                dao.insertToken(deviceToken)
                Log.d("FCM", "Device token saved successfully")
            } catch (e: Exception) {
                Log.e("FCM", "Failed to save device token", e)
            }
        }
    }

    private fun saveNotificationToDatabase(
        title: String,
        body: String,
        data: String? = null,
        priority: String = "high"
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = database.notificationDao()
                val repository = NotificationRepository(dao)

                repository.addNotification(
                    title = title,
                    body = body,
                    data = data,
                    priority = priority
                )

                Log.d("FCM", "Notification saved to database: $title")
            } catch (e: Exception) {
                Log.e("FCM", "Failed to save notification to database", e)
            }
        }
    }

    private fun showForegroundNotification(title: String, body: String) {
        try {
            val notification = NotificationCompat.Builder(this, "high_priority_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
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
        } catch (e: Exception) {
            Log.e("FCM", "Failed to show foreground notification", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "high_priority_channel",
                "High Priority Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical server status notifications"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}