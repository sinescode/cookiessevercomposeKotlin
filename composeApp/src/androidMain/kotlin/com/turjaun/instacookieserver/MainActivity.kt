package com.turjaun.instacookieserver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.turjaun.instacookieserver.di.RepositoryProvider
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(this)

        setContent {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(
                    initialIp = runBlocking { preferencesManager.getServerIp() },
                    onIpChange = { newIp ->
                        // Save IP and update repository asynchronously
                        CoroutineScope(Dispatchers.IO).launch {
                            preferencesManager.saveServerIp(newIp)
                            RepositoryProvider.updateRepository(newIp)
                        }
                    }
                )
            )
            App(viewModel)
        }
    }
}

class MainViewModelFactory(
    private val initialIp: String,
    private val onIpChange: (String) -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                repository = RepositoryProvider.getRepository(initialIp),
                onServerIpChange = onIpChange
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}