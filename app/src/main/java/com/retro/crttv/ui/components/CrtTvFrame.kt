package com.retro.crttv.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retro.crttv.crt.AspectRatioMode
import com.retro.crttv.crt.TvSkinStyle
import com.retro.crttv.ui.theme.CrtTextMuted
import com.retro.crttv.ui.theme.CrtTextWarm

@Composable
fun CrtTvChassis(
    aspectRatioMode: AspectRatioMode,
    skinStyle: TvSkinStyle,
    screenContent: @Composable () -> Unit,
    bottomControls: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = skinStyle.chassisBackground))
            .statusBarsPadding() // Clear Android system status bar
            .displayCutoutPadding() // Clear camera cutouts / notches
            .padding(top = 10.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top TV Brand Header with Slanted RGB Stripes & Skin Title
        CrtTvTopHeader(
            skinStyle = skinStyle,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Center CRT Screen Cabinet (Authentic Physical TV Skin Frame)
        Box(
            modifier = Modifier
                .weight(1f, fill = true)
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            val ratio = aspectRatioMode.ratio ?: (4f / 3.4f)

            // Outer molded TV cabinet body (The TV Skin)
            Box(
                modifier = Modifier
                    .aspectRatio(ratio)
                    .fillMaxSize()
                    .then(
                        if (skinStyle.hasRedArcadeBorder) {
                            Modifier.border(5.dp, Color(0xFFD50000), RoundedCornerShape(26.dp))
                        } else if (skinStyle.hasWoodgrainFinish) {
                            Modifier.border(3.5.dp, Color(0xFF3E2723), RoundedCornerShape(24.dp))
                        } else {
                            Modifier.border(2.dp, Color(0xFF2A2A32), RoundedCornerShape(24.dp))
                        }
                    )
                    .shadow(24.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = if (skinStyle.hasWoodgrainFinish) {
                            // Rich walnut woodgrain cabinet finish for VHS skin
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF3E2723),
                                    Color(0xFF4E342E),
                                    Color(0xFF2E1B15),
                                    Color(0xFF4E342E),
                                    Color(0xFF2E1B15)
                                )
                            )
                        } else {
                            // Molded matte cabinet for Classic, Arcade, News, Retro, Broken
                            Brush.radialGradient(
                                colors = listOf(
                                    skinStyle.bezelOuterColor.copy(alpha = 0.98f),
                                    skinStyle.bezelOuterColor,
                                    Color(0xFF09090C)
                                ),
                                radius = 850f
                            )
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                // Realistic Chassis Vent Slots along top edge
                TopVentilationSlots(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 1.dp)
                )

                // 4 Heavy Corner Fasteners / Screws
                ChassisCornerScrew(modifier = Modifier.align(Alignment.TopStart).padding(4.dp))
                ChassisCornerScrew(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp))
                ChassisCornerScrew(modifier = Modifier.align(Alignment.BottomStart).padding(4.dp))
                ChassisCornerScrew(modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp))

                // Left & Right Vertical Stereo Speaker Grilles
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 2.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Speaker Column
                    SpeakerGrilleColumn(
                        modifier = Modifier
                            .width(10.dp)
                            .padding(end = 4.dp)
                    )

                    // Inner Cathode Tube Molded Bezel (Recessed)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(18.dp))
                            .background(skinStyle.bezelInnerColor)
                            .border(2.dp, Color(0xFF111115), RoundedCornerShape(18.dp))
                            .padding(4.dp)
                    ) {
                        // Glass CRT Display surface
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            screenContent()

                            // Cracked glass overlay for Broken TV skin
                            if (skinStyle.hasCrackedGlass) {
                                CrackedGlassCanvas(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }

                    // Right Speaker Column
                    SpeakerGrilleColumn(
                        modifier = Modifier
                            .width(10.dp)
                            .padding(start = 4.dp)
                    )
                }

                // RCA A/V Ports (Yellow, White, Red) for VHS Skin
                if (skinStyle.hasRcaPorts) {
                    RcaJackRow(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 2.dp, end = 16.dp)
                    )
                }

                // Peeling Metallic Duct Tape for Broken TV Skin
                if (skinStyle.hasCrackedGlass) {
                    PeelingDuctTape(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 2.dp, top = 2.dp)
                    )
                }

                // Broadcast Rack Mount Ear Brackets for News Skin
                if (skinStyle.skinDrawableRes == com.retro.crttv.R.drawable.tv_skin_news) {
                    RackMountBrackets(modifier = Modifier.fillMaxSize())
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Bottom Physical Controls
        bottomControls()
    }
}

@Composable
fun SpeakerGrilleColumn(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(14) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Color(0xFF08080A))
                    .border(0.4.dp, Color(0xFF1A1A20), RoundedCornerShape(1.dp))
            )
        }
    }
}

@Composable
fun CrtTvTopHeader(
    skinStyle: TvSkinStyle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF101014).copy(alpha = 0.85f))
            .border(1.dp, Color(0xFF22222A), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "CRT TV" in clean retro bold font + subtitle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = skinStyle.badgeTitle,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = CrtTextWarm,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = skinStyle.badgeSubtitle,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CrtTextMuted,
                    letterSpacing = 0.8.sp
                )
            }

            // The 3 Slanted Diagonal RGB Stripes (Red, Green, Blue)
            Canvas(modifier = Modifier.size(width = 30.dp, height = 13.dp)) {
                val stripeWidth = 6f
                val spacing = 9f
                val slantOffset = 5.5f
                val h = size.height

                val colors = listOf(
                    Color(0xFFE53935), // Red
                    Color(0xFF43A047), // Green
                    Color(0xFF1E88E5)  // Blue
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
        }
    }
}

@Composable
fun ChassisCornerScrew(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(Color(0xFF222228))
            .border(0.6.dp, Color(0xFF0D0D10), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(4.dp)) {
            // Phillips screw cross
            drawLine(Color(0xFF0D0D10), Offset(2f, 0f), Offset(2f, 4f), strokeWidth = 0.9f)
            drawLine(Color(0xFF0D0D10), Offset(0f, 2f), Offset(4f, 2f), strokeWidth = 0.9f)
        }
    }
}

@Composable
fun TopVentilationSlots(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        repeat(8) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Color(0xFF09090C))
            )
        }
    }
}

@Composable
fun RcaJackRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Video (Yellow)
        RcaPort(outerColor = Color(0xFFFDD835))
        // Audio L (White)
        RcaPort(outerColor = Color(0xFFEEEEEE))
        // Audio R (Red)
        RcaPort(outerColor = Color(0xFFE53935))
    }
}

@Composable
fun RcaPort(outerColor: Color) {
    Box(
        modifier = Modifier
            .size(9.dp)
            .clip(CircleShape)
            .background(outerColor)
            .border(0.8.dp, Color(0xFF222226), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(3.5.dp)
                .clip(CircleShape)
                .background(Color.Black)
        )
    }
}

@Composable
fun CrackedGlassCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val crackColor = Color.White.copy(alpha = 0.45f)
        drawLine(crackColor, Offset(w * 0.95f, h * 0.05f), Offset(w * 0.72f, h * 0.28f), strokeWidth = 1.6f)
        drawLine(crackColor, Offset(w * 0.72f, h * 0.28f), Offset(w * 0.58f, h * 0.24f), strokeWidth = 1.2f)
        drawLine(crackColor, Offset(w * 0.78f, h * 0.45f), Offset(w * 0.65f, h * 0.62f), strokeWidth = 1.0f)
    }
}

@Composable
fun PeelingDuctTape(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 38.dp, height = 15.dp)
            .clip(RoundedCornerShape(1.dp))
            .background(Color(0xFF908A7C).copy(alpha = 0.9f))
            .border(0.8.dp, Color(0xFF5C574E), RoundedCornerShape(1.dp))
    )
}

@Composable
fun RackMountBrackets(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        // Left rack ear screw
        Box(
            modifier = Modifier
                .size(6.dp)
                .align(Alignment.CenterStart)
                .clip(CircleShape)
                .background(Color(0xFF383C42))
        )
        // Right rack ear screw
        Box(
            modifier = Modifier
                .size(6.dp)
                .align(Alignment.CenterEnd)
                .clip(CircleShape)
                .background(Color(0xFF383C42))
        )
    }
}
