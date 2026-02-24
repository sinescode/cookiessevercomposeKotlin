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

        // Initialize NotifierManager - but DON'T set showPushNotification = true
        // This allows us to handle notifications manually in FirebaseMessagingService
        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
                showPushNotification = false, // IMPORTANT: Set to false, we handle notifications manually
            )
        )

        Log.d("MyApplication", "Application initialized")
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