package com.retro.crttv.ui.library

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.retro.crttv.R
import com.retro.crttv.data.model.VideoItem
import com.retro.crttv.ui.theme.CrtButtonBorder
import com.retro.crttv.ui.theme.CrtDarkCharcoal
import com.retro.crttv.ui.theme.CrtPhosphorGreen
import com.retro.crttv.ui.theme.CrtTextMuted
import com.retro.crttv.ui.theme.CrtTextWarm
import com.retro.crttv.viewmodel.LibraryViewModel

@Composable
fun VideoLibraryScreen(
    viewModel: LibraryViewModel,
    onVideoSelected: (Uri) -> Unit,
    onNavigateToSettings: () -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allVideos by viewModel.allVideos.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    // Storage permission string based on API level
    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_VIDEO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var hasStoragePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, storagePermission) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Storage Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasStoragePermission = granted
        if (granted) {
            viewModel.scanDeviceVideos()
        }
    }

    // Auto-scan on load if permission already granted
    LaunchedEffect(hasStoragePermission) {
        if (hasStoragePermission) {
            viewModel.scanDeviceVideos()
        }
    }

    // SAF File Picker Launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onVideoSelected(uri)
        }
    }

    val displayVideos = when (selectedTab) {
        0 -> allVideos
        1 -> allVideos
        2 -> allVideos.filter { favorites.contains(it.uri.toString()) }
        else -> allVideos
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F11))
            .statusBarsPadding()
            .displayCutoutPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Bar: Back arrow + "VIDEO LIBRARY", Rescan + Settings gear icon on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to TV",
                        tint = CrtTextWarm
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "VIDEO LIBRARY",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = CrtTextWarm,
                    letterSpacing = 1.5.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (hasStoragePermission) {
                        viewModel.scanDeviceVideos()
                    } else {
                        permissionLauncher.launch(storagePermission)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Scan Device Videos",
                        tint = CrtPhosphorGreen
                    )
                }

                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = CrtTextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Storage Permission Notification Banner if not granted yet
        if (!hasStoragePermission) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2B1D0E))
                    .border(1.dp, Color(0xFFE65100), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DEVICE STORAGE ACCESS",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFFFB74D)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Grant access to automatically load all videos on your device",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = CrtTextMuted
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE65100))
                            .clickable { permissionLauncher.launch(storagePermission) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "ALLOW",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Tabs: [ Videos ] [ Folders ] [ Favorites ] as rounded pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LibraryPillTab(
                title = "Videos (${allVideos.size})",
                isSelected = selectedTab == 0,
                onClick = { viewModel.selectTab(0) }
            )
            LibraryPillTab(
                title = "Folders",
                isSelected = selectedTab == 1,
                onClick = { viewModel.selectTab(1) }
            )
            LibraryPillTab(
                title = "Favorites (${favorites.size})",
                isSelected = selectedTab == 2,
                onClick = { viewModel.selectTab(2) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Video Items List
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (displayVideos.isEmpty()) {
                // Empty state or sample items prompt
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = Color(0xFF323238),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (!hasStoragePermission) "STORAGE ACCESS REQUIRED" else "NO VIDEOS FOUND",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CrtTextMuted,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (!hasStoragePermission)
                                "Tap 'ALLOW' above or pick a file below"
                            else
                                "Tap 'SCAN DEVICE' or '+ PICK FILES' below",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF5A5852),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayVideos, key = { it.uri.toString() }) { video ->
                        RetroVideoRowItem(
                            video = video,
                            onClick = { onVideoSelected(video.uri) },
                            onToggleFavorite = { viewModel.toggleFavorite(video.uri) },
                            onDelete = { viewModel.removeVideo(video.uri) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Action Buttons: [ SCAN DEVICE ] and [ + PICK FILES ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Auto-scan device videos button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1B231D))
                    .border(1.2.dp, Color(0xFF2E6E38), RoundedCornerShape(8.dp))
                    .clickable {
                        if (hasStoragePermission) {
                            viewModel.scanDeviceVideos()
                        } else {
                            permissionLauncher.launch(storagePermission)
                        }
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SCAN DEVICE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = CrtPhosphorGreen,
                    letterSpacing = 1.sp
                )
            }

            // SAF Document Picker button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1F1F26))
                    .border(1.2.dp, Color(0xFF383848), RoundedCornerShape(8.dp))
                    .clickable {
                        videoPickerLauncher.launch(arrayOf("video/*"))
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ PICK FILES",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = CrtTextWarm,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun LibraryPillTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) Color(0xFF162719) else Color(0xFF18181C))
            .border(
                1.dp,
                if (isSelected) Color(0xFF2E6E38) else Color(0xFF24242A),
                RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) CrtPhosphorGreen else CrtTextMuted
        )
    }
}

@Composable
fun RetroVideoRowItem(
    video: VideoItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail box with rounded corners
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 44.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF1B1B20))
                .border(1.dp, Color(0xFF282830), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.sample_anime_scene),
                contentDescription = "Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Duration
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.name,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = CrtTextWarm,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = video.formattedDuration,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = CrtTextMuted
            )
        }

        // More options button (⋮)
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = CrtTextMuted
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(Color(0xFF1C1C22))
            ) {
                DropdownMenuItem(
                    text = { Text("Play Video", color = CrtTextWarm, fontFamily = FontFamily.Monospace) },
                    onClick = {
                        menuExpanded = false
                        onClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Favorite", color = CrtTextWarm, fontFamily = FontFamily.Monospace) },
                    onClick = {
                        menuExpanded = false
                        onToggleFavorite()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Remove", color = Color(0xFFE57373), fontFamily = FontFamily.Monospace) },
                    onClick = {
                        menuExpanded = false
                        onDelete()
                    }
                )
            }
        }
    }
}
