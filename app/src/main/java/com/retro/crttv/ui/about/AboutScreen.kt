package com.retro.crttv.ui.about

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retro.crttv.ui.theme.CrtPhosphorGreen
import com.retro.crttv.ui.theme.CrtTextMuted
import com.retro.crttv.ui.theme.CrtTextWarm

@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D10))
            .statusBarsPadding()
            .displayCutoutPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CrtTextWarm
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ABOUT SYSTEM",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = CrtTextWarm,
                letterSpacing = 1.5.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TV Brand Badge Card (Matches Screen 8 and UI Skin Pack Spec)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141418))
                    .border(1.2.dp, Color(0xFF252530), RoundedCornerShape(10.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Vintage 3 RGB Slanted Stripes
                    Canvas(modifier = Modifier.size(width = 44.dp, height = 18.dp)) {
                        val stripeWidth = 9f
                        val spacing = 13f
                        val slantOffset = 8f
                        val h = size.height
                        val colors = listOf(
                            Color(0xFFE53935),
                            Color(0xFF43A047),
                            Color(0xFF1E88E5)
                        )
                        for (i in colors.indices) {
                            val startX = i * spacing + slantOffset
                            drawLine(
                                color = colors[i],
                                start = Offset(startX, 0f),
                                end = Offset(startX - slantOffset, h),
                                strokeWidth = stripeWidth
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "CRT TV",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = CrtTextWarm,
                        letterSpacing = 3.sp
                    )

                    Text(
                        text = "VINTAGE TELEVISION VIDEO PLAYER",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CrtPhosphorGreen,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Masking Tape Label (Spec Sheet)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFC7B69C).copy(alpha = 0.85f))
                            .border(0.8.dp, Color(0xFF9E8B70), RoundedCornerShape(2.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "/// CRT TV v1.0.0",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF261D16)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "“Videos feel better in the old days.”",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CrtTextMuted
                    )
                }
            }

            // Technical Specifications Sheet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF111114))
                    .border(1.dp, Color(0xFF202028), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SYSTEM ARCHITECTURE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = CrtPhosphorGreen,
                        letterSpacing = 1.sp
                    )

                    SpecRow(label = "Platform", value = "Android 15 Native (SDK 35)")
                    SpecRow(label = "UI Toolkit", value = "Jetpack Compose + Material3")
                    SpecRow(label = "Media Core", value = "AndroidX Media3 / ExoPlayer")
                    SpecRow(label = "Online Stream", value = "Chromeless Web Tube Engine")
                    SpecRow(label = "GPU Shaders", value = "AGSL RuntimeShader + Canvas")
                    SpecRow(label = "Aspect Ratios", value = "4:3 CRT, 16:9 Wide, 1:1, Fill")
                    SpecRow(label = "Presets", value = "Classic, VHS, Retro, Arcade, News, Broken")
                    SpecRow(label = "License", value = "MIT Open Source")
                }
            }

            // GitHub & Open Source Information
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF111114))
                    .border(1.dp, Color(0xFF202028), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "REPOSITORY & CREDITS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = CrtPhosphorGreen,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "GitHub: github.com/Maadhu938/crt-tv-video-player",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CrtTextWarm
                    )
                    Text(
                        text = "Built with passion for analog aesthetics, cathode ray tube nostalgia, and high-fidelity retro design.",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CrtTextMuted,
                        lineHeight = 16.sp
                    )
                }
            }

            // Bottom Quote (Screen 8)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = "“Same videos. A different feeling.”",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF5A5862)
                )
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = CrtTextMuted
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = CrtTextWarm
        )
    }
}
