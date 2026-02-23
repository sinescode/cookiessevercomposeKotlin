package com.turjaun.serverstatuscookies.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turjaun.serverstatuscookies.data.NotificationEntity
import com.turjaun.serverstatuscookies.ui.theme.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCard(
    notification: NotificationEntity,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateTime = remember(notification.timestamp) {
        Instant.fromEpochMilliseconds(notification.timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }
    
    val priorityColor = when (notification.priority) {
        "high" -> HighPriority
        "normal" -> NormalPriority
        else -> LowPriority
    }
    
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            SurfaceDark,
            SurfaceVariant.copy(alpha = 0.5f)
        )
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Box(
            modifier = Modifier
                .background(gradientBrush)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Priority indicator & Icon
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    priorityColor.copy(alpha = 0.3f),
                                    priorityColor.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (notification.priority == "high") 
                            Icons.Default.PriorityHigh else Icons.Default.Notifications,
                        contentDescription = null,
                        tint = priorityColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = notification.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (!notification.isRead) {
                            Badge(
                                containerColor = Accent,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = notification.body,
                        fontSize = 14.sp,
                        color = OnSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Timestamp
                    Text(
                        text = "${dateTime.date} • ${dateTime.time.hour.toString().padStart(2, '0')}:${dateTime.time.minute.toString().padStart(2, '0')}",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                
                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}