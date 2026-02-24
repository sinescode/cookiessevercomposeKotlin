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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        requestNotificationPermission()

        // Check for existing notification permission
        checkNotificationPermission()

        setContent {
            val viewModel: NotificationViewModel = viewModel()
            var fcmToken by remember { mutableStateOf<String?>(null) }
            var isTokenLoading by remember { mutableStateOf(true) }

            // Get FCM token
            LaunchedEffect(Unit) {
                try {
                    val token = NotifierManager.getPushNotifier().getToken()
                    fcmToken = token
                    Log.d("MainActivity", "FCM Token: ${token?.take(20)}...")
                    if (token == null) {
                        Log.w("MainActivity", "Failed to get FCM token")
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error getting FCM token", e)
                } finally {
                    isTokenLoading = false
                }
            }

            // Handle notification clicks from intent
            LaunchedEffect(intent) {
                handleNotificationIntent()
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
                    Log.d("MainActivity", "Notification permission already granted")
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

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permission == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Notifications are enabled")
            } else {
                Log.d("MainActivity", "Notifications are not enabled")
            }
        }
    }

    private fun handleNotificationIntent() {
        // Handle notification tap if app was started from notification
        intent?.let { intent ->
            val extras = intent.extras
            if (extras != null) {
                for (key in extras.keySet()) {
                    Log.d("MainActivity", "Notification extra: $key = ${extras.get(key)}")
                }
            }
        }
    }
}