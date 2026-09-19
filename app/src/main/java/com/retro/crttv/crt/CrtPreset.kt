package com.retro.crttv.crt

import androidx.compose.ui.graphics.Color
import com.retro.crttv.R

data class TvSkinStyle(
    val chassisBackground: List<Color>,
    val bezelOuterColor: Color,
    val bezelInnerColor: Color,
    val badgeTitle: String,
    val badgeSubtitle: String,
    val hasRedArcadeBorder: Boolean = false,
    val hasRcaPorts: Boolean = false,
    val hasRetroSpeakerGrid: Boolean = false,
    val hasCrackedGlass: Boolean = false,
    val hasWoodgrainFinish: Boolean = false,
    val accentColor: Color = Color(0xFF33FF33),
    val skinDrawableRes: Int
)

enum class CrtPreset(
    val id: String,
    val title: String,
    val description: String,
    val defaultSettings: CrtSettings,
    val skinStyle: TvSkinStyle
) {
    CLASSIC(
        id = "classic",
        title = "Classic",
        description = "Standard 90s CRT television tube with balanced scanlines, warm phosphor glow, and subtle curvature.",
        defaultSettings = CrtSettings(
            scanlines = 0.40f,
            curvature = 0.22f,
            noise = 0.12f,
            color = 1.10f,
            brightness = 1.05f,
            contrast = 1.10f,
            rgbSeparation = 0.20f,
            flicker = 0.10f,
            vignette = 0.35f
        ),
        skinStyle = TvSkinStyle(
            chassisBackground = listOf(Color(0xFF141416), Color(0xFF1F1F23), Color(0xFF141416)),
            bezelOuterColor = Color(0xFF19191C),
            bezelInnerColor = Color(0xFF0C0C0E),
            badgeTitle = "CRT TV",
            badgeSubtitle = "RETROVISION-21",
            accentColor = Color(0xFF33FF33),
            skinDrawableRes = R.drawable.tv_skin_classic
        )
    ),
    VHS(
        id = "vhs",
        title = "VHS",
        description = "Authentic magnetic home video tape look with tracking distortion, color bleed, tape hiss, and jitter.",
        defaultSettings = CrtSettings(
            scanlines = 0.55f,
            curvature = 0.28f,
            noise = 0.38f,
            color = 1.25f,
            brightness = 1.12f,
            contrast = 1.20f,
            rgbSeparation = 0.45f,
            flicker = 0.22f,
            vignette = 0.45f
        ),
        skinStyle = TvSkinStyle(
            chassisBackground = listOf(Color(0xFF1E1712), Color(0xFF281F19), Color(0xFF16120E)),
            bezelOuterColor = Color(0xFF261D16),
            bezelInnerColor = Color(0xFF0F0C09),
            badgeTitle = "CRT TV",
            badgeSubtitle = "VIDEO SYSTEM • AUTO TRACKING",
            hasWoodgrainFinish = true,
            hasRcaPorts = true,
            accentColor = Color(0xFFFFA726),
            skinDrawableRes = R.drawable.tv_skin_vhs
        )
    ),
    RETRO(
        id = "retro",
        title = "Retro",
        description = "Muted vintage 80s color saturation with heavy cathode curvature and authentic aperture grille lines.",
        defaultSettings = CrtSettings(
            scanlines = 0.65f,
            curvature = 0.35f,
            noise = 0.20f,
            color = 1.35f,
            brightness = 1.00f,
            contrast = 1.25f,
            rgbSeparation = 0.35f,
            flicker = 0.15f,
            vignette = 0.50f
        ),
        skinStyle = TvSkinStyle(
            chassisBackground = listOf(Color(0xFFC7B69C), Color(0xFFB8A68B), Color(0xFFA8967B)),
            bezelOuterColor = Color(0xFF80715D),
            bezelInnerColor = Color(0xFF1A1713),
            badgeTitle = "CRT TV",
            badgeSubtitle = "SOLID STATE 1986",
            hasRetroSpeakerGrid = true,
            accentColor = Color(0xFFFFB300),
            skinDrawableRes = R.drawable.tv_skin_retro
        )
    ),
    ARCADE(
        id = "arcade",
        title = "Arcade",
        description = "Vivid, high-contrast arcade cabinet CRT monitor with sharp RGB phosphor masks and deep blacks.",
        defaultSettings = CrtSettings(
            scanlines = 0.70f,
            curvature = 0.15f,
            noise = 0.08f,
            color = 1.40f,
            brightness = 1.10f,
            contrast = 1.35f,
            rgbSeparation = 0.40f,
            flicker = 0.08f,
            vignette = 0.30f
        ),
        skinStyle = TvSkinStyle(
            chassisBackground = listOf(Color(0xFF0D0D10), Color(0xFF15151A), Color(0xFF0A0A0D)),
            bezelOuterColor = Color(0xFF16161B),
            bezelInnerColor = Color(0xFF08080A),
            badgeTitle = "CRT TV",
            badgeSubtitle = "ARCADE CABINET • COIN-OP",
            hasRedArcadeBorder = true,
            accentColor = Color(0xFFFF1744),
            skinDrawableRes = R.drawable.tv_skin_arcade
        )
    ),
    NEWS(
        id = "news",
        title = "News",
        description = "Broadcast studio monitor profile with clean high-definition scanlines and true-to-life broadcast color temperature.",
        defaultSettings = CrtSettings(
            scanlines = 0.28f,
            curvature = 0.12f,
            noise = 0.05f,
            color = 1.05f,
            brightness = 1.02f,
            contrast = 1.08f,
            rgbSeparation = 0.12f,
            flicker = 0.05f,
            vignette = 0.20f
        ),
        skinStyle = TvSkinStyle(
            chassisBackground = listOf(Color(0xFF1B1D21), Color(0xFF24272D), Color(0xFF16181C)),
            bezelOuterColor = Color(0xFF2B3037),
            bezelInnerColor = Color(0xFF0E1012),
            badgeTitle = "CRT TV",
            badgeSubtitle = "BROADCAST MONITOR MASTER",
            accentColor = Color(0xFF00E5FF),
            skinDrawableRes = R.drawable.tv_skin_news
        )
    ),
    BROKEN(
        id = "broken",
        title = "Broken",
        description = "Failing electron gun with strong signal degradation, heavy tracking rolls, ghosting, and noisy interference.",
        defaultSettings = CrtSettings(
            scanlines = 0.85f,
            curvature = 0.45f,
            noise = 0.65f,
            color = 1.50f,
            brightness = 1.25f,
            contrast = 1.45f,
            rgbSeparation = 0.80f,
            flicker = 0.45f,
            vignette = 0.60f
        ),
        skinStyle = TvSkinStyle(
            chassisBackground = listOf(Color(0xFF221C17), Color(0xFF2E251E), Color(0xFF18130F)),
            bezelOuterColor = Color(0xFF382C22),
            bezelInnerColor = Color(0xFF0D0A08),
            badgeTitle = "CRT TV",
            badgeSubtitle = "FAULTY DEFLECTION • SIGNAL LOSS",
            hasCrackedGlass = true,
            accentColor = Color(0xFFFF9800),
            skinDrawableRes = R.drawable.tv_skin_broken
        )
    );

    companion object {
        fun fromId(id: String): CrtPreset = entries.firstOrNull { it.id == id } ?: CLASSIC
    }
}
