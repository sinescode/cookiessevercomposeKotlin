package com.turjaun.serverstatuscookies.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turjaun.serverstatuscookies.ui.theme.*

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryDark, PrimaryVariant)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsOff,
                contentDescription = null,
                tint = OnSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "No Notifications",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "You're all caught up!",
                fontSize = 16.sp,
                color = OnSurfaceVariant
            )
        }
    }
}