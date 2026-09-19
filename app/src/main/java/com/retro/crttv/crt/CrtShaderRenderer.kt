package com.retro.crttv.crt

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.retro.crttv.R
import kotlin.random.Random

@Composable
fun CrtEffectContainer(
    settings: CrtSettings,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "crt_effects")
    
    // Fast flicker / beam tick
    val flickerVal by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crt_flicker"
    )

    // Slow analog scanline drift
    val scanlineDrift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "crt_drift"
    )

    if (!settings.effectsEnabled) {
        Box(modifier = modifier) {
            content()
        }
        return
    }

    val hasAgsl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val runtimeShader = remember(hasAgsl) {
        if (hasAgsl) {
            try {
                RuntimeShader(CrtShaderSource.AGSL_CRT_SHADER)
            } catch (e: Throwable) {
                null
            }
        } else {
            null
        }
    }

    // Outer CRT screen container with curved tube mask
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black)
    ) {
        // Video layer: Apply RuntimeShader if available
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (runtimeShader != null) {
                        Modifier.graphicsLayer {
                            runtimeShader.setFloatUniform("uResolution", size.width, size.height)
                            runtimeShader.setFloatUniform("uTime", scanlineDrift)
                            runtimeShader.setFloatUniform("uScanlines", settings.scanlines)
                            runtimeShader.setFloatUniform("uCurvature", settings.curvature)
                            runtimeShader.setFloatUniform("uNoise", settings.noise)
                            runtimeShader.setFloatUniform("uColor", settings.color)
                            runtimeShader.setFloatUniform("uBrightness", settings.brightness)
                            runtimeShader.setFloatUniform("uContrast", settings.contrast)
                            runtimeShader.setFloatUniform("uRgbSeparation", settings.rgbSeparation)
                            runtimeShader.setFloatUniform("uFlicker", settings.flicker)
                            runtimeShader.setFloatUniform("uVignette", settings.vignette)

                            renderEffect = RenderEffect
                                .createRuntimeShaderEffect(runtimeShader, "composable")
                                .asComposeRenderEffect()
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            content()
        }

        // 1. Direct Physical Scanlines Raster Overlay (Always drawn with BlendMode.Multiply/Darken)
        CrtPhysicalScanlinesOverlay(
            scanlinesIntensity = settings.scanlines,
            driftY = scanlineDrift,
            flicker = if (settings.flicker > 0.05f) flickerVal else 1f,
            colorSaturation = settings.color,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Authentic Dust & CRT Scratches Texture Overlay
        if (settings.noise > 0.1f) {
            Image(
                painter = painterResource(id = R.drawable.crt_dust_texture),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(Color.White.copy(alpha = (settings.noise * 0.45f).coerceIn(0f, 0.7f))),
                modifier = Modifier
                    .fillMaxSize()
                    .alpha((settings.noise * 0.5f).coerceIn(0.1f, 0.75f))
            )
        }

        // 3. Radial Vignette & Tube Corner Falloff
        CrtRadialVignetteOverlay(
            vignette = settings.vignette,
            curvature = settings.curvature,
            modifier = Modifier.fillMaxSize()
        )

        // 4. Glass Reflection & Convex Bulb Glare
        CrtGlassGlareOverlay(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun CrtPhysicalScanlinesOverlay(
    scanlinesIntensity: Float,
    driftY: Float,
    flicker: Float,
    colorSaturation: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        if (scanlinesIntensity > 0.04f) {
            // Draw dark horizontal scanline beams across entire video
            val step = 4.5f
            val lineThickness = 2.0f
            val alpha = (scanlinesIntensity * 0.52f).coerceIn(0.08f, 0.78f)
            val scanColor = Color.Black.copy(alpha = alpha)

            var y = driftY % step
            while (y < height) {
                drawLine(
                    color = scanColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = lineThickness
                )
                y += step
            }
        }

        // Phosphor RGB Aperture Grille: subtle vertical RGB tint stripes
        if (colorSaturation > 0.3f) {
            val stripeStep = 6f
            val stripeAlpha = 0.035f * colorSaturation.coerceIn(0.5f, 1.8f)
            var x = 0f
            while (x < width) {
                // Red stripe
                drawLine(
                    color = Color.Red.copy(alpha = stripeAlpha),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 2f
                )
                // Green stripe
                drawLine(
                    color = Color.Green.copy(alpha = stripeAlpha),
                    start = Offset(x + 2f, 0f),
                    end = Offset(x + 2f, height),
                    strokeWidth = 2f
                )
                // Blue stripe
                drawLine(
                    color = Color.Blue.copy(alpha = stripeAlpha),
                    start = Offset(x + 4f, 0f),
                    end = Offset(x + 4f, height),
                    strokeWidth = 2f
                )
                x += stripeStep
            }
        }
    }
}

@Composable
fun CrtRadialVignetteOverlay(
    vignette: Float,
    curvature: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Deep cathode tube corner shadows
        val vigAmount = (vignette * 0.8f + curvature * 0.3f).coerceIn(0.15f, 0.95f)
        val vignetteBrush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = vigAmount * 0.4f),
                Color.Black.copy(alpha = vigAmount * 0.92f)
            ),
            center = Offset(width / 2f, height / 2f),
            radius = (width.coerceAtLeast(height) * 0.68f)
        )
        drawRect(brush = vignetteBrush)

        // Outer curved tube inner bevel shadow
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.8f),
            size = size,
            style = Stroke(width = 8f)
        )
    }
}

@Composable
fun CrtGlassGlareOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Upper glass convex reflection arch
        val glareBrush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.12f),
                Color.White.copy(alpha = 0.04f),
                Color.Transparent
            ),
            start = Offset(width * 0.15f, 0f),
            end = Offset(width * 0.55f, height * 0.4f)
        )

        val path = Path().apply {
            moveTo(0f, 0f)
            cubicTo(
                width * 0.3f, height * 0.14f,
                width * 0.7f, height * 0.14f,
                width, 0f
            )
            close()
        }
        drawPath(path, brush = glareBrush)

        // Subtle bottom cathode reflection
        val bottomReflection = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.03f)
            ),
            startY = height * 0.88f,
            endY = height
        )
        drawRect(brush = bottomReflection)
    }
}
