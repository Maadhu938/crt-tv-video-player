package com.retro.crttv.crt

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun CrtPowerTransition(
    isPoweredOn: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val animProgress = remember { Animatable(if (isPoweredOn) 1f else 0f) }

    LaunchedEffect(isPoweredOn) {
        if (isPoweredOn) {
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
            )
        } else {
            animProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 500, easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1f))
            )
        }
    }

    val p = animProgress.value

    if (p <= 0.001f) {
        // TV is fully powered off - pitch black screen
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF070709))
        )
        return
    }

    // Power collapse/expansion calculation
    // Scale X and Scale Y transitions
    val scaleX: Float
    val scaleY: Float
    val brightnessMultiplier: Float

    if (p >= 0.5f) {
        // Vertical expansion / collapse phase
        val subP = (p - 0.5f) / 0.5f // 0 to 1
        scaleX = 1f
        scaleY = (subP * 0.995f + 0.005f).coerceIn(0.005f, 1f)
        // Flash brightness spike as beam contracts
        brightnessMultiplier = 1f + (1f - subP) * 0.8f
    } else {
        // Horizontal collapse into dot / expansion from dot
        val subP = p / 0.5f // 0 to 1
        scaleX = (subP * 0.98f + 0.02f).coerceIn(0.01f, 1f)
        scaleY = 0.004f
        brightnessMultiplier = 1.8f * subP
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.scaleX = scaleX
                    this.scaleY = scaleY
                    this.alpha = if (p < 0.08f) p / 0.08f else 1f
                }
                .drawWithContent {
                    drawContent()
                    if (brightnessMultiplier > 1.05f) {
                        // White phosphor collapse flare
                        val flareAlpha = ((brightnessMultiplier - 1f) * 0.5f).coerceIn(0f, 0.85f)
                        drawRect(
                            color = Color(0xFFE8F5E9).copy(alpha = flareAlpha),
                            size = size
                        )
                    }
                }
        ) {
            content()
        }

        // Phosphor central persistent spark if collapsing
        if (p in 0.01f..0.35f) {
            val dotAlpha = (1f - (p / 0.35f)).coerceIn(0f, 0.9f)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawCircle(
                            color = Color.White.copy(alpha = dotAlpha),
                            radius = 4f,
                            center = Offset(size.width / 2f, size.height / 2f)
                        )
                    }
            )
        }
    }
}
