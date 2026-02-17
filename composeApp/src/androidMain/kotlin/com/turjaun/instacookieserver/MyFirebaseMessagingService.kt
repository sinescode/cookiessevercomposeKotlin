package com.turjaun.instacookieserver

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.turjaun.instacookieserver.di.RepositoryProvider
import kotlinx.coroutines.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val prefs by lazy { PreferencesManager(applicationContext) }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            sendTokenToServer(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle foreground messages if needed
    }

    private suspend fun sendTokenToServer(token: String) {
        val ip = prefs.getServerIp()
        val repository = RepositoryProvider.getRepository(ip)
        repository.registerToken(token)
    }
}