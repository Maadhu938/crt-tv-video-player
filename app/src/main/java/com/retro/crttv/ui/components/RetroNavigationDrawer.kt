package com.retro.crttv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retro.crttv.ui.theme.CrtPhosphorGreen
import com.retro.crttv.ui.theme.CrtTextMuted
import com.retro.crttv.ui.theme.CrtTextWarm

@Composable
fun RetroDrawerContent(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(270.dp)
            .background(Color(0xFF131316))
            .border(1.dp, Color(0xFF202026))
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        // Top Header: CRT TV, v1.0.0, Close ✕ button (Screen 8)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CRT TV",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = CrtTextWarm,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "v1.0.0",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CrtTextMuted
                )
            }

            IconButton(onClick = onCloseDrawer) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = CrtTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation Items
        MockupDrawerRow(
            icon = Icons.Default.Home,
            label = "Home",
            selected = currentRoute == "home",
            onClick = {
                onNavigate("home")
                onCloseDrawer()
            }
        )

        MockupDrawerRow(
            icon = Icons.Default.Folder,
            label = "Video Library",
            selected = currentRoute == "library",
            onClick = {
                onNavigate("library")
                onCloseDrawer()
            }
        )

        MockupDrawerRow(
            icon = Icons.Default.Settings,
            label = "Settings",
            selected = currentRoute == "settings",
            onClick = {
                onNavigate("settings")
                onCloseDrawer()
            }
        )

        MockupDrawerRow(
            icon = Icons.Default.Favorite,
            label = "Favorites",
            selected = false,
            onClick = {
                onNavigate("library_favorites")
                onCloseDrawer()
            }
        )

        MockupDrawerRow(
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            label = "Help",
            selected = false,
            onClick = {
                onNavigate("help")
                onCloseDrawer()
            }
        )

        MockupDrawerRow(
            icon = Icons.Default.Info,
            label = "About",
            selected = false,
            onClick = {
                onNavigate("about")
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Quote (Screen 8)
        Column(
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                text = "“Same videos.",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF6E6D75)
            )
            Text(
                text = " A different feeling.”",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF6E6D75)
            )
        }
    }
}

@Composable
fun MockupDrawerRow(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) CrtPhosphorGreen else CrtTextMuted,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) CrtPhosphorGreen else CrtTextWarm
        )
    }
}
