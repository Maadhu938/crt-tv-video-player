package com.retro.crttv.ui.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retro.crttv.R
import com.retro.crttv.crt.AspectRatioMode
import com.retro.crttv.crt.CrtPreset
import com.retro.crttv.ui.theme.CrtButtonBorder
import com.retro.crttv.ui.theme.CrtPhosphorGreen
import com.retro.crttv.ui.theme.CrtTextMuted
import com.retro.crttv.ui.theme.CrtTextWarm
import com.retro.crttv.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    onNavigateToPresets: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val crtSettings by viewModel.crtSettings.collectAsState()
    val activePreset by viewModel.activePreset.collectAsState()
    var aspectMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F11))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Header: ← Settings
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
                text = "Settings",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = CrtTextWarm
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Section Header: "CRT Preset"
            Text(
                text = "CRT Preset",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = CrtTextWarm
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2x3 Grid of Presets
            val presets = CrtPreset.entries
            val row1 = presets.take(3)
            val row2 = presets.drop(3)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row1.forEach { preset ->
                    PresetCardItem(
                        preset = preset,
                        isSelected = activePreset == preset,
                        onClick = { viewModel.applyPreset(preset) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row2.forEach { preset ->
                    PresetCardItem(
                        preset = preset,
                        isSelected = activePreset == preset,
                        onClick = { viewModel.applyPreset(preset) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive CRT Test Tube Preview Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF142418))
                    .border(1.dp, Color(0xFF2E633B), RoundedCornerShape(6.dp))
                    .clickable { onNavigateToPresets() }
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = "Test Tube",
                        tint = com.retro.crttv.ui.theme.CrtPhosphorGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PREVIEW & TEST PRESETS IN CRT TUBE ▶",
                        fontSize = 11.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = com.retro.crttv.ui.theme.CrtPhosphorGreen,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sliders matching mockup Screen 6
            MockupSliderRow(
                label = "Scanlines",
                percentage = (crtSettings.scanlines * 100).toInt(),
                value = crtSettings.scanlines,
                onValueChange = { viewModel.updateScanlines(it) }
            )

            MockupSliderRow(
                label = "Curvature",
                percentage = (crtSettings.curvature * 100).toInt(),
                value = crtSettings.curvature,
                onValueChange = { viewModel.updateCurvature(it) }
            )

            MockupSliderRow(
                label = "Noise",
                percentage = (crtSettings.noise * 100).toInt(),
                value = crtSettings.noise,
                onValueChange = { viewModel.updateNoise(it) }
            )

            MockupSliderRow(
                label = "Color",
                percentage = ((crtSettings.color / 2f) * 100).toInt(),
                value = crtSettings.color / 2f,
                onValueChange = { viewModel.updateColor(it * 2f) }
            )

            MockupSliderRow(
                label = "Brightness",
                percentage = ((crtSettings.brightness - 0.5f) * 100).toInt(),
                value = (crtSettings.brightness - 0.5f).coerceIn(0f, 1f),
                onValueChange = { viewModel.updateBrightness(0.5f + it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // "Show Timestamp" Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show Timestamp",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CrtTextWarm
                )
                Switch(
                    checked = crtSettings.showTimestamp,
                    onCheckedChange = { viewModel.setShowTimestamp(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF388E3C),
                        uncheckedThumbColor = CrtTextMuted,
                        uncheckedTrackColor = Color(0xFF26262C)
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // "Default Aspect Ratio" Dropdown selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Default Aspect Ratio",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CrtTextWarm
                )

                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF19191D))
                            .border(1.dp, Color(0xFF2B2B32), RoundedCornerShape(6.dp))
                            .clickable { aspectMenuExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = crtSettings.aspectRatio.displayName,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = CrtTextWarm
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = CrtTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = aspectMenuExpanded,
                        onDismissRequest = { aspectMenuExpanded = false },
                        modifier = Modifier.background(Color(0xFF1E1E24))
                    ) {
                        AspectRatioMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = mode.displayName,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (mode == crtSettings.aspectRatio) CrtPhosphorGreen else CrtTextWarm
                                    )
                                },
                                onClick = {
                                    viewModel.setAspectRatio(mode)
                                    aspectMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PresetCardItem(
    preset: CrtPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawableRes = when (preset) {
        CrtPreset.CLASSIC -> R.drawable.tv_skin_classic
        CrtPreset.VHS -> R.drawable.tv_skin_vhs
        CrtPreset.RETRO -> R.drawable.tv_skin_retro
        CrtPreset.ARCADE -> R.drawable.tv_skin_arcade
        CrtPreset.NEWS -> R.drawable.tv_skin_news
        CrtPreset.BROKEN -> R.drawable.tv_skin_broken
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF18181D))
                .border(
                    width = if (isSelected) 1.8.dp else 1.dp,
                    color = if (isSelected) Color(0xFF4DD0E1) else Color(0xFF26262D),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = preset.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = preset.title,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) CrtTextWarm else CrtTextMuted
        )
    }
}

@Composable
fun MockupSliderRow(
    label: String,
    percentage: Int,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = CrtTextWarm,
            modifier = Modifier.width(86.dp)
        )

        Slider(
            value = value.coerceIn(0f, 1f),
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = CrtTextWarm,
                activeTrackColor = Color(0xFF5A5860),
                inactiveTrackColor = Color(0xFF25252C)
            ),
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "$percentage%",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = CrtTextMuted,
            modifier = Modifier.width(36.dp)
        )
    }
}
