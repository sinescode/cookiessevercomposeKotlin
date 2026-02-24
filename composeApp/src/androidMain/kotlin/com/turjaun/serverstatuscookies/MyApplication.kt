package com.turjaun.serverstatuscookies

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.lifecycleScope // optional, but we'll use CoroutineScope directly
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

        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
                showPushNotification = true,
            )
        )

        // 🔥 Add this listener to save notifications to Room
        NotifierManager.addListener(
            onPushNotification = { data: Map<String, String> ->
                // Run database insertion on a background thread
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val dao = database.notificationDao()
                        val repository = NotificationRepository(dao)

                        repository.addNotification(
                            title = data["title"] ?: "No title",
                            body = data["body"] ?: "No body",
                            data = data.toString(),        // store the whole map as JSON string
                            priority = data["priority"] ?: "high"
                        )

                        // Optional: Log success
                        android.util.Log.d("FCM", "Notification saved to database")
                    } catch (e: Exception) {
                        android.util.Log.e("FCM", "Failed to save notification", e)
                    }
                }
            },
            onNewToken = { token ->
                // You can save the token here if needed (e.g., to DeviceToken table)
                android.util.Log.d("FCM", "New token: $token")
            }
        )
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