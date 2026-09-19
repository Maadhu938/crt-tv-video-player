package com.retro.crttv.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retro.crttv.player.PlayerState

@Composable
fun CrtOsdOverlay(
    playerState: PlayerState,
    showTimestamp: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Left: PLAY ▶ or PAUSED ❚❚ in classic camcorder white/cyan font
        val osdText = when {
            playerState.isPlaying -> "PLAY ▶"
            playerState.currentUri != null -> "PAUSED ❚❚"
            else -> null
        }

        if (osdText != null) {
            Text(
                text = osdText,
                color = Color(0xFFD6F0FF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.5.sp,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }

        // Bottom Left: Authentic 90s Camcorder Timestamp (PM 10:24 / SEP 16 2026)
        if (showTimestamp && playerState.currentUri != null) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "PM 10:24",
                    color = Color(0xFFD6F0FF).copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "SEP 16 2026",
                    color = Color(0xFFD6F0FF).copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
