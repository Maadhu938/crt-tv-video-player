package com.retro.crttv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retro.crttv.ui.theme.CrtSpeakerSlit
import com.retro.crttv.ui.theme.CrtTextMuted
import com.retro.crttv.ui.theme.CrtTextWarm
import com.retro.crttv.ui.theme.LedGreenActive
import com.retro.crttv.ui.theme.LedRedStandby

@Composable
fun TvControlPanel(
    isPoweredOn: Boolean,
    onPowerClick: () -> Unit,
    onChannelDown: () -> Unit,
    onChannelUp: () -> Unit,
    onVolumeDown: () -> Unit,
    onVolumeUp: () -> Unit,
    onInputClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Control Wells Row: POWER (with LED) | CHANNEL (▼ / ▲) | VOLUME (− / +)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // 1. POWER SECTION
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "POWER",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CrtTextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RealPowerButton(
                        isPoweredOn = isPoweredOn,
                        onClick = onPowerClick
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    // Real Jewel LED Indicator
                    RealJewelLed(isActive = isPoweredOn)
                }
            }

            // 2. CHANNEL SECTION (Realistic split rocker button: ▼ / ▲)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CHANNEL",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CrtTextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                RealRockerButton(
                    leftSymbol = "▼",
                    rightSymbol = "▲",
                    onLeftClick = onChannelDown,
                    onRightClick = onChannelUp
                )
            }

            // 3. VOLUME SECTION (Realistic split rocker button: − / +)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "VOLUME",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CrtTextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                RealRockerButton(
                    leftSymbol = "−",
                    rightSymbol = "+",
                    onLeftClick = onVolumeDown,
                    onRightClick = onVolumeUp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row of 2 Function Buttons: [ INPUT ] and [ SETTINGS ] (Swipe used for Menu)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RealPushRectButton(
                label = "INPUT",
                onClick = onInputClick,
                modifier = Modifier.weight(1f)
            )
            RealPushRectButton(
                label = "SETTINGS",
                onClick = onSettingsClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Row: Speaker Grille + Vintage Plate
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Molded horizontal speaker slits
            Column(
                modifier = Modifier.width(160.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                repeat(6) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(CrtSpeakerSlit)
                            .border(0.5.dp, Color(0xFF070709), RoundedCornerShape(1.5.dp))
                    )
                }
            }

            // Molded Vintage Metal Tag: "GOOD VIDEOS NEVER GET OLD"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF141416))
                    .border(1.dp, Color(0xFF222228), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "GOOD VIDEOS",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5A5852),
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "NEVER GET OLD",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5A5852),
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RealPowerButton(
    isPoweredOn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Sunken socket/well around button
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0B0B0E))
            .border(1.2.dp, Color(0xFF1A1A20), RoundedCornerShape(8.dp))
            .padding(2.5.dp),
        contentAlignment = Alignment.Center
    ) {
        // Physical button cap with tactile 3D relief
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = if (isPressed) 1.5.dp else 0.dp)
                .shadow(if (isPressed) 1.dp else 4.dp, RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isPressed) {
                            listOf(Color(0xFF18181D), Color(0xFF202026))
                        } else {
                            listOf(Color(0xFF383842), Color(0xFF202025), Color(0xFF17171C))
                        }
                    )
                )
                .border(
                    1.dp,
                    if (isPressed) Color(0xFF101014) else Color(0xFF484854),
                    RoundedCornerShape(6.dp)
                )
                .clickable(interactionSource = interaction, indication = null, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = "Power",
                tint = if (isPoweredOn) LedGreenActive else Color(0xFFE53935),
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Composable
fun RealJewelLed(isActive: Boolean) {
    // Physical faceted jewel LED
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(Color(0xFF09090C))
            .border(1.dp, Color(0xFF222228), CircleShape)
            .padding(1.5.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (isActive) {
                            listOf(Color(0xFFE8F5E9), Color(0xFF2CD44A), Color(0xFF157A28))
                        } else {
                            listOf(Color(0xFFFFCDD2), Color(0xFFE53935), Color(0xFF5A0E12))
                        }
                    )
                )
                .shadow(if (isActive) 8.dp else 1.dp, CircleShape)
        )
    }
}

@Composable
fun RealRockerButton(
    leftSymbol: String,
    rightSymbol: String,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Outer sunken recess well
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0A0A0D))
            .border(1.2.dp, Color(0xFF1B1B22), RoundedCornerShape(8.dp))
            .padding(2.5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RealRockerHalf(symbol = leftSymbol, onClick = onLeftClick)
            // Center tactile division seam
            Box(
                modifier = Modifier
                    .width(1.5.dp)
                    .height(24.dp)
                    .background(Color(0xFF0B0B0E))
            )
            RealRockerHalf(symbol = rightSymbol, onClick = onRightClick)
        }
    }
}

@Composable
fun RealRockerHalf(
    symbol: String,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Box(
        modifier = Modifier
            .width(38.dp)
            .height(34.dp)
            .offset(y = if (isPressed) 1.5.dp else 0.dp)
            .shadow(if (isPressed) 1.dp else 3.dp, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isPressed) {
                        listOf(Color(0xFF18181D), Color(0xFF222228))
                    } else {
                        listOf(Color(0xFF34343E), Color(0xFF212126), Color(0xFF18181D))
                    }
                )
            )
            .border(
                1.dp,
                if (isPressed) Color(0xFF121216) else Color(0xFF444450),
                RoundedCornerShape(4.dp)
            )
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPressed) Color.White else CrtTextWarm
        )
    }
}

@Composable
fun RealPushRectButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Outer recessed bezel
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF09090C))
            .border(1.2.dp, Color(0xFF181820), RoundedCornerShape(6.dp))
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .offset(y = if (isPressed) 1.5.dp else 0.dp)
                .shadow(if (isPressed) 1.dp else 3.dp, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isPressed) {
                            listOf(Color(0xFF16161B), Color(0xFF202026))
                        } else {
                            listOf(Color(0xFF34343E), Color(0xFF202025), Color(0xFF16161B))
                        }
                    )
                )
                .border(
                    1.dp,
                    if (isPressed) Color(0xFF0F0F14) else Color(0xFF40404C),
                    RoundedCornerShape(4.dp)
                )
                .clickable(interactionSource = interaction, indication = null, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (isPressed) Color.White else CrtTextWarm,
                letterSpacing = 1.sp
            )
        }
    }
}
