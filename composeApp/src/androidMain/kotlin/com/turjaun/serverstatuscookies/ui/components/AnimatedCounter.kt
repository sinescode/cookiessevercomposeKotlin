package com.turjaun.serverstatuscookies.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turjaun.serverstatuscookies.ui.theme.Accent
import com.turjaun.serverstatuscookies.ui.theme.OnSurface

@Composable
fun AnimatedCounter(count: Int) {
    var oldCount by remember { mutableStateOf(count) }
    
    SideEffect {
        oldCount = count
    }
    
    val scale by animateFloatAsState(
        targetValue = if (count > oldCount) 1.3f else 1f,
        animationSpec = keyframes {
            durationMillis = 300
            1f at 0
            1.3f at 100
            1f at 300
        },
        label = "scale"
    )
    
    Box(
        modifier = Modifier
            .size(24.dp)
            .scale(scale)
            .background(Accent, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            color = OnSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}