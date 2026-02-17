package com.turjaun.instacookieserver

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App(viewModel: MainViewModel) {
    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsScreen(
            currentIp = viewModel.serverIp.collectAsState().value,
            onIpChange = { viewModel.setServerIp(it) },
            onSave = { showSettings = false }
        )
    } else {
        ServerStatusScreen(
            viewModel = viewModel,
            onOpenSettings = { showSettings = true }
        )
    }
}

@Composable
fun ServerStatusScreen(
    viewModel: MainViewModel,
    onOpenSettings: () -> Unit
) {
    val status by viewModel.status.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Server Monitor") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                error?.let {
                    Text("Error: $it", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.fetchStatus() }) {
                        Text("Retry")
                    }
                } ?: run {
                    status?.let {
                        StatusCard(status = it.status, lastChecked = it.last_checked)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchStatus() }) {
                            Text("Refresh")
                        }
                    } ?: Text("No data available")
                }
            }
        }
    }
}

@Composable
fun StatusCard(status: String, lastChecked: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Server Status",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = status,
                style = MaterialTheme.typography.headlineLarge,
                color = when (status) {
                    "ON" -> MaterialTheme.colorScheme.primary
                    "OFF" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Last checked: $lastChecked",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SettingsScreen(
    currentIp: String,
    onIpChange: (String) -> Unit,
    onSave: () -> Unit
) {
    var ipInput by remember { mutableStateOf(currentIp) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Server IP Address", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = ipInput,
            onValueChange = { ipInput = it },
            label = { Text("http://example.com:5000/") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onIpChange(ipInput)
                onSave()
            },
            enabled = ipInput.isNotBlank()
        ) {
            Text("Save")
        }
    }
}