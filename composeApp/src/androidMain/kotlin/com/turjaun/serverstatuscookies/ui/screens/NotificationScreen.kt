package com.turjaun.serverstatuscookies.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.turjaun.serverstatuscookies.data.NotificationEntity
import com.turjaun.serverstatuscookies.ui.components.*
import com.turjaun.serverstatuscookies.ui.theme.*
import com.turjaun.serverstatuscookies.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = viewModel(),
    fcmToken: String
) {
    val notifications by viewModel.allNotifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    var showTokenDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Notifications",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Text(
                            "${notifications.size} total • $unreadCount unread",
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryDark,
                    titleContentColor = OnSurface
                ),
                actions = {
                    // Token button
                    IconButton(onClick = { showTokenDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Token,
                            contentDescription = "FCM Token",
                            tint = Secondary
                        )
                    }

                    // Mark all read
                    if (unreadCount > 0) {
                        IconButton(onClick = { viewModel.markAllAsRead() }) {
                            Icon(
                                imageVector = Icons.Default.MarkEmailRead,
                                contentDescription = "Mark all read",
                                tint = Success
                            )
                        }
                    }

                    // Clear all
                    if (notifications.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear all",
                                tint = Error
                            )
                        }
                    }
                }
            )
        },
        containerColor = PrimaryDark
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryDark, PrimaryVariant)
                    )
                )
        ) {
            if (notifications.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = notifications,
                        key = { it.id }
                    ) { notification ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            NotificationCard(
                                notification = notification,
                                onDelete = { viewModel.deleteNotification(notification) },
                                onClick = {
                                    viewModel.markAsRead(notification.id)
                                    viewModel.selectNotification(notification)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Clear All Dialog
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                containerColor = SurfaceDark,
                title = {
                    Text(
                        "Clear All Notifications?",
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "This will permanently delete all ${notifications.size} notifications.",
                        color = OnSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearAll()
                            showClearDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Error)
                    ) {
                        Text("Clear All")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Cancel", color = Secondary)
                    }
                }
            )
        }

        // Token Dialog with Copy functionality
        if (showTokenDialog) {
            AlertDialog(
                onDismissRequest = { showTokenDialog = false },
                containerColor = SurfaceDark,
                title = {
                    Text(
                        "FCM Device Token",
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            "Copy this token to send notifications to this device:",
                            color = OnSurfaceVariant,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            fcmToken,
                            color = Secondary,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceVariant)
                                .padding(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Copy button
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("FCM Token", fcmToken)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Token copied!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy")
                        }
                        // Close button
                        TextButton(
                            onClick = { showTokenDialog = false }
                        ) {
                            Text("Close", color = Secondary)
                        }
                    }
                }
            )
        }
    }
}