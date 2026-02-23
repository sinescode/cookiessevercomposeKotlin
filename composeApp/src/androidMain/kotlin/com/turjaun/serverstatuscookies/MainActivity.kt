package com.turjaun.serverstatuscookies

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.permission.PermissionUtil
import com.turjaun.serverstatuscookies.ui.screens.NotificationScreen
import com.turjaun.serverstatuscookies.viewmodel.NotificationViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle permission result
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == 
                    PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
        
        setContent {
            val viewModel: NotificationViewModel = viewModel()
            var fcmToken by remember { mutableStateOf("Loading...") }
            val scope = rememberCoroutineScope()
            
            // Get FCM token - fixed API
            LaunchedEffect(Unit) {
                val token = NotifierManager.getPushNotifier().getToken()
                fcmToken = token ?: "Failed to get token"
            }
            
            // Listen for FCM messages and save to local DB - fixed API
            LaunchedEffect(Unit) {
                NotifierManager.addListener(object : NotifierManager.Listener {
                    override fun onNewToken(token: String) {
                        fcmToken = token
                    }
                    
                    override fun onPushNotification(title: String?, body: String?) {
                        // Save to local database
                        scope.launch {
                            viewModel.addNotification(
                                title = title ?: "New Notification",
                                body = body ?: "",
                                priority = "high"
                            )
                        }
                    }
                    
                    // Remove onPayloadData - it doesn't exist in this version
                })
            }
            
            NotificationScreen(
                viewModel = viewModel,
                fcmToken = fcmToken
            )
        }
    }
}