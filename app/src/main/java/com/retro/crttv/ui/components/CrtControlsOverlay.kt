package com.retro.crttv.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retro.crttv.player.PlayerState
import com.retro.crttv.ui.theme.CrtButtonBorder
import com.retro.crttv.ui.theme.CrtButtonHighlight
import com.retro.crttv.ui.theme.CrtButtonPlastic
import com.retro.crttv.ui.theme.CrtButtonPlasticPressed
import com.retro.crttv.ui.theme.CrtDarkCharcoal
import com.retro.crttv.ui.theme.CrtPhosphorGreen
import com.retro.crttv.ui.theme.CrtTextMuted
import com.retro.crttv.ui.theme.CrtTextWarm

@Composable
fun CrtControlsOverlay(
    isVisible: Boolean,
    playerState: PlayerState,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onRewind: () -> Unit,
    onFastForward: () -> Unit,
    onStop: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CrtDarkCharcoal.copy(alpha = 0.96f))
                .border(1.dp, Color(0xFF26262B), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Seekbar row: 0:12:34 [───●─────] 1:56:20
            var sliderPos by remember(playerState.currentPositionMs) {
                mutableFloatStateOf(playerState.progress)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = playerState.formattedCurrentTime,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CrtTextWarm
                )

                Slider(
                    value = sliderPos,
                    onValueChange = { sliderPos = it },
                    onValueChangeFinished = {
                        val targetMs = (sliderPos * playerState.durationMs).toLong()
                        onSeek(targetMs)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = CrtTextWarm,
                        activeTrackColor = CrtTextWarm,
                        inactiveTrackColor = Color(0xFF383842)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .height(20.dp)
                )

                Text(
                    text = playerState.formattedTotalTime,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CrtTextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // The 5 tactile transport buttons: [ REW ] [ STOP ] [ PLAY ] [ PAUSE ] [ FWD ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlaybackTransportKey(
                    icon = Icons.Default.FastRewind,
                    label = "REW",
                    onClick = onRewind
                )
                PlaybackTransportKey(
                    icon = Icons.Default.Stop,
                    label = "STOP",
                    onClick = onStop
                )
                PlaybackTransportKey(
                    icon = Icons.Default.PlayArrow,
                    label = "PLAY",
                    isActive = playerState.isPlaying,
                    onClick = onPlay
                )
                PlaybackTransportKey(
                    icon = Icons.Default.Pause,
                    label = "PAUSE",
                    isActive = !playerState.isPlaying && playerState.currentUri != null,
                    onClick = onPause
                )
                PlaybackTransportKey(
                    icon = Icons.Default.FastForward,
                    label = "FWD",
                    onClick = onFastForward
                )
            }
        }
    }
}

@Composable
fun PlaybackTransportKey(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(width = 46.dp, height = 36.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isPressed) {
                            listOf(CrtButtonPlasticPressed, CrtButtonPlastic)
                        } else {
                            listOf(CrtButtonHighlight, CrtButtonPlastic)
                        }
                    )
                )
                .border(
                    1.dp,
                    if (isActive) CrtPhosphorGreen.copy(alpha = 0.8f) else CrtButtonBorder,
                    RoundedCornerShape(4.dp)
                )
                .clickable(interactionSource = interaction, indication = null, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) CrtPhosphorGreen else CrtTextWarm,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = if (isActive) CrtPhosphorGreen else CrtTextMuted,
            letterSpacing = 0.5.sp
        )
    }
}
