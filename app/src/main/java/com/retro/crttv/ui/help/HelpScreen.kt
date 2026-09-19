package com.retro.crttv.ui.help

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retro.crttv.ui.theme.CrtDarkCharcoal
import com.retro.crttv.ui.theme.CrtPhosphorGreen
import com.retro.crttv.ui.theme.CrtTextMuted
import com.retro.crttv.ui.theme.CrtTextWarm

@Composable
fun HelpScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F12))
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
            Column {
                Text(
                    text = "OPERATING MANUAL",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = CrtTextWarm,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "CRT TV INSTRUCTION & USER GUIDE",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CrtPhosphorGreen,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable Guide Sections
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpSectionCard(
                icon = Icons.Default.Tv,
                title = "CHANNEL CONTROLS & TEST SIGNALS",
                content = "Use the physical CHANNEL ▼ and ▲ buttons on the TV frame to surf between your saved videos. If no video is playing, channel buttons cycle through authentic vintage test patterns:\n" +
                        "• CH 01: SMPTE 7-Color Broadcast Bars\n" +
                        "• CH 02: RF White Noise Snow (Rolling V-SYNC)\n" +
                        "• CH 03: Vintage Navy Blue Screen (AV 1)\n" +
                        "• CH 04: Alignment & Static Noise"
            )

            HelpSectionCard(
                icon = Icons.Default.VolumeUp,
                title = "VOLUME & PHOSPHOR OSD",
                content = "Adjust the audio level using the physical VOLUME − and + buttons. An authentic 16-segment green phosphor bar (VOL [ ▰▰▰▱▱ ]) illuminates on-screen for 2.8 seconds. You can also mute/unmute or fine-tune volume from Settings."
            )

            HelpSectionCard(
                icon = Icons.Default.VideoLibrary,
                title = "PLAYING LOCAL PHONE VIDEOS",
                content = "Tap the [ INPUT ] button on the bottom control deck, select 'Device Videos', and choose any video file (.mp4, .mkv, .avi, etc.). You can also open 'Video Library' from the drawer menu to view and filter all device videos by folders and favorites."
            )

            HelpSectionCard(
                icon = Icons.Default.Language,
                title = "YOUTUBE & WEB STREAMING",
                content = "Tap [ INPUT ] -> 'YouTube & Online Stream' to paste any YouTube URL (youtube.com/watch?v=..., youtu.be/..., shorts) or direct MP4/HLS streams. Quick presets are available for 80s Synthwave, Anime Lo-Fi, and 90s Commercials."
            )

            HelpSectionCard(
                icon = Icons.Default.DisplaySettings,
                title = "PRESETS & CRT TV SKINS",
                content = "Choose from 6 unique handcrafted TV skins:\n" +
                        "• Classic: Charcoal 90s chassis with speaker slats\n" +
                        "• VHS: Walnut woodgrain with 3 front RCA jacks\n" +
                        "• Retro: 1986 ivory monitor with dot-matrix grille\n" +
                        "• Arcade: Deep black with glowing red T-molding\n" +
                        "• News: Sony PVM broadcast master with rack ears\n" +
                        "• Broken: Weathered casing with duct tape & cracked glass\n" +
                        "Adjust Scanlines, Curvature, Noise, and Glow in Settings."
            )

            HelpSectionCard(
                icon = Icons.Default.TouchApp,
                title = "TOUCH GESTURES & CONTROLS",
                content = "• Single Tap Screen: Reveal/hide playback transport deck (Seek bar, REW, STOP, PLAY, PAUSE, FWD).\n" +
                        "• Double Tap Left Half: Rewind 10 seconds.\n" +
                        "• Double Tap Right Half: Fast-Forward 10 seconds.\n" +
                        "• Swipe From Left Edge: Open retro navigation drawer."
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HelpSectionCard(
    icon: ImageVector,
    title: String,
    content: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF141418))
            .border(1.dp, Color(0xFF262630), RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1B261D))
                        .border(1.dp, Color(0xFF2C4A30), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = CrtPhosphorGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = CrtPhosphorGreen,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = CrtTextWarm,
                lineHeight = 17.sp
            )
        }
    }
}
