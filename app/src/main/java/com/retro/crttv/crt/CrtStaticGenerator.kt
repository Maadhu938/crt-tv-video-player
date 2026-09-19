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
import androidx.compose.runtime.remember
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

@Composable
fun CrtNoSignalStatic(
    modifier: Modifier = Modifier,
    channelLabel: String = "CH 03"
) {
    val transition = rememberInfiniteTransition(label = "static_noise")

    // Animated vertical rolling bar (simulating lost vertical hold / V-SYNC roll)
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

    // OSD subtle blink
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
            .background(Color(0xFF090C0A)),
        contentAlignment = Alignment.Center
    ) {
        // Procedural Static Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val seed = (noiseTick * 1337).toInt()
            val rnd = Random(seed)

            // 1. Static grain lines
            val lineCount = 90
            val blockHeight = height / lineCount
            for (i in 0 until lineCount) {
                val y = i * blockHeight
                // Random grayscale brightness
                val gray = rnd.nextInt(20, 110)
                val alpha = rnd.nextFloat() * 0.45f + 0.15f
                drawRect(
                    color = Color(gray, gray, gray).copy(alpha = alpha),
                    topLeft = Offset(0f, y),
                    size = Size(width, blockHeight)
                )

                // Occasional white snow flecks
                if (rnd.nextFloat() < 0.25f) {
                    val fleckX = rnd.nextFloat() * width
                    val fleckW = rnd.nextFloat() * 60f + 15f
                    drawRect(
                        color = Color.White.copy(alpha = 0.5f),
                        topLeft = Offset(fleckX, y),
                        size = Size(fleckW, blockHeight)
                    )
                }
            }

            // 2. Horizontal Interference / Rolling Tear Bar
            val rollY = rollPosition * height
            val rollHeight = height * 0.14f
            val rollBrush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.5f),
                    Color.White.copy(alpha = 0.18f),
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

        // Top Left Channel indicator OSD
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = channelLabel,
                color = Color(0xFF33FF33),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
        }

        // Authentic CRT "NO SIGNAL" Box
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            val textColor = if (osdBlink > 0.15f) Color(0xFF33FF33) else Color(0xFF1B801B)
            Text(
                text = "NO SIGNAL",
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp
            )
        }
    }
}
