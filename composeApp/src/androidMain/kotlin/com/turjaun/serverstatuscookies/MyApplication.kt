package com.turjaun.serverstatuscookies

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
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
                // This ensures the library shows the system notification automatically
                showPushNotification = true, 
            )
        )

        // Global Listener: Works even if the app is in the background/killed
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                Log.d("FCM", "New token: $token")
            }

            // Called when a standard notification is received (usually in foreground)
            override fun onPushNotification(title: String?, body: String?) {
                Log.d("FCM", "Notification received: $title")
                saveToDatabase(title ?: "No title", body ?: "No body")
            }

            // Called when a DATA message is received (works in the background!)
            override fun onPayloadData(data: Map<String, String>) {
                Log.d("FCM", "Data payload received: $data")
                // Extract the title and body we sent from Python
                val title = data["title"] ?: "No title"
                val body = data["body"] ?: "No body"
                saveToDatabase(title, body)
            }

            // Helper function to handle the database insertion
            private fun saveToDatabase(title: String, body: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val dao = database.notificationDao()
                        val repository = NotificationRepository(dao)

                        repository.addNotification(
                            title = title,
                            body = body,
                            priority = "high"
                        )

                        Log.d("FCM", "Saved to database successfully")
                    } catch (e: Exception) {
                        Log.e("FCM", "Failed to save notification", e)
                    }
                }
            }
        })
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