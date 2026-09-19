package com.retro.crttv.ui.presets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retro.crttv.R
import com.retro.crttv.crt.CrtEffectContainer
import com.retro.crttv.crt.CrtPreset
import com.retro.crttv.ui.theme.CrtBezelInner
import com.retro.crttv.ui.theme.CrtBezelOuter
import com.retro.crttv.ui.theme.CrtMidCharcoal
import com.retro.crttv.ui.theme.CrtPhosphorGreen
import com.retro.crttv.ui.theme.CrtTextMuted
import com.retro.crttv.ui.theme.CrtTextWarm

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PresetPreviewScreen(
    currentPreset: CrtPreset,
    onApplyPreset: (CrtPreset) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = CrtPreset.entries
    val initialPage = presets.indexOf(currentPreset).coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { presets.size })

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F11))
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header: ← CRT Preset
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
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "CRT Preset",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = CrtTextWarm
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pager with live CRT TV Preview (Screen 7)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            val preset = presets[page]

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large TV Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .aspectRatio(1.05f)
                        .shadow(16.dp, RoundedCornerShape(26.dp))
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(CrtMidCharcoal, CrtBezelOuter, Color(0xFF101012))
                            )
                        )
                        .border(3.dp, CrtBezelInner, RoundedCornerShape(26.dp))
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(CrtBezelInner)
                            .border(2.dp, Color(0xFF1B1B20), RoundedCornerShape(20.dp))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black)
                        ) {
                            CrtEffectContainer(settings = preset.defaultSettings) {
                                Image(
                                    painter = painterResource(id = R.drawable.sample_anime_scene),
                                    contentDescription = preset.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title (e.g. VHS)
                Text(
                    text = preset.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = CrtTextWarm,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Description
                Text(
                    text = preset.description,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CrtTextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Page Indicator Dots
        Row(
            modifier = Modifier.padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(presets.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 6.dp else 5.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else Color(0xFF383842))
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Large Bottom Button: "Apply Preset" (Screen 7)
        val selectedPreset = presets[pagerState.currentPage]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2E633B))
                .border(1.dp, Color(0xFF438A54), RoundedCornerShape(8.dp))
                .clickable {
                    onApplyPreset(selectedPreset)
                    onBackClick()
                }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Apply Preset",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }
    }
}
