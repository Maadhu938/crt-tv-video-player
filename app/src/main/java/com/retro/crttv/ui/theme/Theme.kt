package com.retro.crttv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RetroDarkColorScheme = darkColorScheme(
    primary = CrtPhosphorGreen,
    onPrimary = CrtDarkCharcoal,
    primaryContainer = CrtMidCharcoal,
    onPrimaryContainer = CrtPhosphorGreen,
    secondary = CrtAmberIndicator,
    onSecondary = CrtDarkCharcoal,
    background = CrtDarkCharcoal,
    onBackground = CrtTextWarm,
    surface = CrtMidCharcoal,
    onSurface = CrtTextWarm,
    surfaceVariant = CrtLightCharcoal,
    onSurfaceVariant = CrtTextMuted
)

@Composable
fun CrtTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RetroDarkColorScheme,
        typography = RetroTypography,
        content = content
    )
}
