package com.retro.crttv.crt

enum class AspectRatioMode(val displayName: String, val ratio: Float?) {
    FOUR_BY_THREE("4:3", 4f / 3f),
    SIXTEEN_BY_NINE("16:9", 16f / 9f),
    ORIGINAL("Original", null)
}

data class CrtSettings(
    val scanlines: Float = 0.45f,
    val curvature: Float = 0.25f,
    val noise: Float = 0.15f,
    val color: Float = 1.15f,
    val brightness: Float = 1.05f,
    val contrast: Float = 1.1f,
    val rgbSeparation: Float = 0.25f,
    val flicker: Float = 0.12f,
    val vignette: Float = 0.35f,
    val showTimestamp: Boolean = true,
    val aspectRatio: AspectRatioMode = AspectRatioMode.FOUR_BY_THREE,
    val effectsEnabled: Boolean = true
)
