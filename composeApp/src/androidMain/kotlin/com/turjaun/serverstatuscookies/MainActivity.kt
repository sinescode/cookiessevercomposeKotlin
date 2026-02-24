package com.turjaun.serverstatuscookies

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mmk.kmpnotifier.notification.NotifierManager
import com.turjaun.serverstatuscookies.ui.screens.NotificationScreen
import com.turjaun.serverstatuscookies.viewmodel.NotificationViewModel

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
        } else {
            Log.d(TAG, "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        requestNotificationPermission()

        // Handle notification data from intent (when app was started from notification)
        handleNotificationIntent()

        setContent {
            val viewModel: NotificationViewModel = viewModel()
            var fcmToken by remember { mutableStateOf<String?>(null) }
            var isTokenLoading by remember { mutableStateOf(true) }

            // Get FCM token
            LaunchedEffect(Unit) {
                try {
                    val token = NotifierManager.getPushNotifier().getToken()
                    fcmToken = token
                    Log.d(TAG, "FCM Token: ${token?.take(20)}...")
                    if (token == null) {
                        Log.w(TAG, "Failed to get FCM token")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting FCM token", e)
                } finally {
                    isTokenLoading = false
                }
            }

            NotificationScreen(
                viewModel = viewModel,
                fcmToken = fcmToken ?: "Unable to get token"
            )
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "Notification permission already granted")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale if needed, then request
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun handleNotificationIntent() {
        // Handle notification tap if app was started from notification
        intent?.let { intent ->
            val extras = intent.extras
            if (extras != null && extras.size() > 0) {
                Log.d(TAG, "=== Notification Intent Received ===")
                for (key in extras.keySet()) {
                    Log.d(TAG, "Notification extra: $key = ${extras.get(key)}")
                }

                // Extract notification data from intent
                val title = extras.getString("title") ?: extras.getString("gcm.notification.title")
                val body = extras.getString("body") ?: extras.getString("gcm.notification.body")

                if (title != null || body != null) {
                    Log.d(TAG, "Saving notification from intent: $title - $body")
                    // The notification should already be saved by FirebaseMessagingService
                    // But we can verify it here if needed
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        // Handle new intent when app is already running
        this.intent = intent
        handleNotificationIntent()
    }
}