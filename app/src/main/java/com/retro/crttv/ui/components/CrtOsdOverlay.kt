package com.retro.crttv.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retro.crttv.player.PlayerState
import com.retro.crttv.ui.theme.CrtPhosphorGreen
import kotlinx.coroutines.delay

@Composable
fun CrtOsdOverlay(
    playerState: PlayerState,
    showTimestamp: Boolean = true,
    modifier: Modifier = Modifier
) {
    var isOsdVisible by remember { mutableStateOf(false) }

    // Auto-hide OSD message after 2.8 seconds
    LaunchedEffect(playerState.osdTimestamp) {
        if (playerState.osdMessage != null) {
            isOsdVisible = true
            delay(2800)
            isOsdVisible = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // TOP-LEFT: Status message (PLAY ▶ / PAUSE ❚❚ / REW << / FFWD >>)
        val statusText = when {
            playerState.osdMessage != null && (
                playerState.osdMessage.startsWith("PLAY") ||
                playerState.osdMessage.startsWith("PAUSE") ||
                playerState.osdMessage.startsWith("REW") ||
                playerState.osdMessage.startsWith("FFWD") ||
                playerState.osdMessage.startsWith("STOP") ||
                playerState.osdMessage.startsWith("AV")
            ) -> playerState.osdMessage
            playerState.isPlaying -> "PLAY ▶"
            playerState.currentUri != null -> "PAUSE ❚❚"
            else -> null
        }

        if (statusText != null) {
            Text(
                text = statusText,
                color = Color(0xFFD6F0FF),
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.5.sp,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }

        // TOP-RIGHT: Channel announcement (e.g. "CH 02: VIDEO.MP4" or "CH 03")
        val isChannelOsd = playerState.osdMessage?.startsWith("CH ") == true
        if (isChannelOsd && isOsdVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xBB0A180E))
                    .border(1.dp, Color(0xFF2E6E38), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = playerState.osdMessage ?: "",
                    color = CrtPhosphorGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp
                )
            }
        }

        // CENTER-BOTTOM: Authentic Retro Green Phosphor Volume Bar
        val isVolumeOsd = playerState.osdMessage?.startsWith("VOL") == true
        AnimatedVisibility(
            visible = isVolumeOsd && isOsdVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            RetroVolumeBar(volume = playerState.volume)
        }

        // BOTTOM-LEFT: Authentic 90s Camcorder Timestamp (REC ● / PM 10:24)
        if (showTimestamp && playerState.currentUri != null) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFE53935))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "SP REC",
                        color = Color(0xFFD6F0FF).copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = playerState.formattedCurrentTime,
                    color = Color(0xFFD6F0FF).copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

/**
 * Authentic 1990s CRT On-Screen Display (OSD) Volume Level Bar.
 * Renders segmented green phosphor blocks like vintage Sony/Panasonic TVs.
 */
@Composable
fun RetroVolumeBar(
    volume: Float,
    modifier: Modifier = Modifier
) {
    val totalBars = 16
    val filledBars = (volume * totalBars).toInt().coerceIn(0, totalBars)
    val percentage = (volume * 100).toInt()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xDD08120B))
            .border(1.2.dp, Color(0xFF2E6E38), RoundedCornerShape(6.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.width(180.dp)
            ) {
                Text(
                    text = "VOLUME",
                    color = CrtPhosphorGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "$percentage%",
                    color = CrtPhosphorGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            // Segmented phosphor green bars: [ ■■■■■■■■□□□□□□□□ ]
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until totalBars) {
                    val isFilled = i < filledBars
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(
                                if (isFilled) CrtPhosphorGreen else Color(0xFF132B18)
                            )
                            .border(
                                0.5.dp,
                                if (isFilled) Color(0xFF55FF55) else Color(0xFF1D3D24),
                                RoundedCornerShape(1.dp)
                            )
                    )
                }
            }
        }
    }
}
