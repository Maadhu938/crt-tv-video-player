package com.retro.crttv.crt

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

/**
 * Authentic 1990s Analog Television Test Channel Generator.
 * Displays real SMPTE Color Bars, RF White Noise Static, or Vintage Blue Screen depending on tuned channel.
 */
@Composable
fun CrtNoSignalStatic(
    modifier: Modifier = Modifier,
    channel: Int = 2,
    channelLabel: String = "CH 02"
) {
    val transition = rememberInfiniteTransition(label = "static_noise")

    // Rolling interference / V-SYNC bar
    val rollPosition by transition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "roll_anim"
    )

    // Fast static grain tick
    val noiseTick by transition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "noise_tick"
    )

    // Subtle OSD blink
    val osdBlink by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "osd_blink"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        when (channel % 4) {
            1 -> {
                // CHANNEL 1: SMPTE Color Bars (Iconic 7 Vintage Broadcast Color Stripes)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val barColors = listOf(
                        Color(0xFFC0C0C0), // 75% White
                        Color(0xFFC0C000), // Yellow
                        Color(0xFF00C0C0), // Cyan
                        Color(0xFF00C000), // Green
                        Color(0xFFC000C0), // Magenta
                        Color(0xFFC00000), // Red
                        Color(0xFF0000C0)  // Blue
                    )
                    val barWidth = w / barColors.size
                    for (i in barColors.indices) {
                        drawRect(
                            color = barColors[i],
                            topLeft = Offset(i * barWidth, 0f),
                            size = Size(barWidth + 1f, h * 0.75f)
                        )
                    }

                    // Bottom Cast Plinth Bars (Castellation)
                    val bottomColors = listOf(
                        Color(0xFF0000C0),
                        Color(0xFF131313),
                        Color(0xFFC000C0),
                        Color(0xFF131313),
                        Color(0xFF00C0C0),
                        Color(0xFF131313),
                        Color(0xFFC0C0C0)
                    )
                    for (i in bottomColors.indices) {
                        drawRect(
                            color = bottomColors[i],
                            topLeft = Offset(i * barWidth, h * 0.75f),
                            size = Size(barWidth + 1f, h * 0.25f)
                        )
                    }
                }

                // Color Bars Label
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "SMPTE COLOR BARS",
                        color = Color(0xFF33FF33),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                }
            }

            3 -> {
                // CHANNEL 3: Vintage CRT Deep Blue Screen ("VIDEO 1 - NO INPUT")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0000B0)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "AV 1 : NO INPUT",
                            color = Color(0xFFFFFF33),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            else -> {
                // CHANNEL 2 & OTHERS: Authentic RF Static / White Noise Snow
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val seed = (noiseTick * 1337).toInt()
                    val rnd = Random(seed)

                    val lineCount = 80
                    val blockHeight = height / lineCount
                    for (i in 0 until lineCount) {
                        val y = i * blockHeight
                        val gray = rnd.nextInt(25, 120)
                        val alpha = rnd.nextFloat() * 0.5f + 0.2f
                        drawRect(
                            color = Color(gray, gray, gray).copy(alpha = alpha),
                            topLeft = Offset(0f, y),
                            size = Size(width, blockHeight)
                        )

                        if (rnd.nextFloat() < 0.3f) {
                            val fleckX = rnd.nextFloat() * width
                            val fleckW = rnd.nextFloat() * 50f + 15f
                            drawRect(
                                color = Color.White.copy(alpha = 0.55f),
                                topLeft = Offset(fleckX, y),
                                size = Size(fleckW, blockHeight)
                            )
                        }
                    }

                    // Rolling Interference Bar
                    val rollY = rollPosition * height
                    val rollHeight = height * 0.12f
                    val rollBrush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        startY = rollY - rollHeight,
                        endY = rollY + rollHeight
                    )
                    drawRect(
                        brush = rollBrush,
                        topLeft = Offset(0f, (rollY - rollHeight).coerceAtLeast(0f)),
                        size = Size(width, (rollHeight * 2f).coerceAtMost(height))
                    )
                }

                // CRT "NO SIGNAL" Box
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    val textColor = if (osdBlink > 0.15f) Color(0xFF33FF33) else Color(0xFF1A801A)
                    Text(
                        text = "NO SIGNAL",
                        color = textColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 3.sp
                    )
                }
            }
        }

        // Top Left Channel indicator OSD
        if (channelLabel.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = channelLabel,
                    color = Color(0xFF33FF33),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
