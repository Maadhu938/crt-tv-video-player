package com.retro.crttv.ui.home

import android.content.Context
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.retro.crttv.crt.CrtEffectContainer
import com.retro.crttv.crt.CrtNoSignalStatic
import com.retro.crttv.crt.CrtPowerTransition
import com.retro.crttv.ui.components.CrtControlsOverlay
import com.retro.crttv.ui.components.CrtOsdOverlay
import com.retro.crttv.ui.components.CrtTvChassis
import com.retro.crttv.ui.components.RetroDrawerContent
import com.retro.crttv.ui.components.TvControlPanel
import com.retro.crttv.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun HomeScreen(
    viewModel: PlayerViewModel,
    onNavigateToLibrary: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPresets: () -> Unit,
    onNavigateToHelp: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val playerState by viewModel.playerState.collectAsState()
    val crtSettings by viewModel.crtSettings.collectAsState()
    val activePreset by viewModel.activePreset.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var controlsVisible by remember { mutableStateOf(false) }
    var showSourceDialog by remember { mutableStateOf(false) }
    var showYouTubeDialog by remember { mutableStateOf(false) }

    val storagePermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_VIDEO
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // SAF Video Picker Launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.playVideo(uri)
        }
    }

    // Storage Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.scanDeviceVideos()
        }
        videoPickerLauncher.launch(arrayOf("video/*"))
    }

    // Auto-scan on load if permission is already granted
    LaunchedEffect(Unit) {
        val hasPerm = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            storagePermission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (hasPerm) {
            viewModel.scanDeviceVideos()
        }
    }

    // Auto-hide controls timer
    LaunchedEffect(controlsVisible) {
        if (controlsVisible) {
            delay(4500)
            controlsVisible = false
        }
    }

    // Source Selection Dialog (Local Device vs. Online Stream)
    if (showSourceDialog) {
        com.retro.crttv.ui.components.RetroInputSourceDialog(
            onDismiss = { showSourceDialog = false },
            onSelectLocal = {
                val hasPerm = androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    storagePermission
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                if (hasPerm) {
                    videoPickerLauncher.launch(arrayOf("video/*"))
                } else {
                    permissionLauncher.launch(storagePermission)
                }
            },
            onSelectStream = {
                showYouTubeDialog = true
            }
        )
    }

    // Online Stream & YouTube Tuning Dialog
    if (showYouTubeDialog) {
        com.retro.crttv.ui.components.RetroYouTubeStreamDialog(
            onDismiss = { showYouTubeDialog = false },
            onTuneUrl = { url ->
                viewModel.playOnlineStream(url)
            }
        )
    }

    // ModalNavigationDrawer with swipe gesture enabled!
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true, // Swipe from the left edge of the screen opens the menu drawer
        drawerContent = {
            RetroDrawerContent(
                currentRoute = "home",
                onNavigate = { route ->
                    when (route) {
                        "home" -> Unit
                        "library", "library_favorites" -> onNavigateToLibrary()
                        "settings" -> onNavigateToSettings()
                        "presets" -> onNavigateToPresets()
                        "help" -> onNavigateToHelp()
                        "about" -> onNavigateToAbout()
                    }
                },
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        CrtTvChassis(
            aspectRatioMode = crtSettings.aspectRatio,
            skinStyle = activePreset.skinStyle,
            modifier = modifier,
            screenContent = {
                CrtPowerTransition(isPoweredOn = playerState.isPoweredOn) {
                    CrtEffectContainer(settings = crtSettings) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            controlsVisible = !controlsVisible
                                        },
                                        onDoubleTap = { offset ->
                                            if (playerState.currentUri != null || playerState.youtubeVideoId != null) {
                                                if (offset.x < size.width / 2) {
                                                    viewModel.rewind()
                                                } else {
                                                    viewModel.fastForward()
                                                }
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (playerState.youtubeVideoId != null) {
                                // Screen: YouTube Video Playing inside the CRT Screen Cabinet!
                                com.retro.crttv.ui.components.CrtYouTubeView(
                                    videoId = playerState.youtubeVideoId!!,
                                    isPlaying = playerState.isPlaying,
                                    isPoweredOn = playerState.isPoweredOn,
                                    volume = playerState.volume,
                                    seekEventId = playerState.seekEventId,
                                    userSeekTargetMs = playerState.userSeekTargetMs,
                                    onPlaybackUpdated = { isPlaying, curMs, durMs ->
                                        viewModel.updateYouTubePlayback(isPlaying, curMs, durMs)
                                    },
                                    onErrorOccurred = { errorMsg ->
                                        viewModel.playerManager.showOsd(errorMsg)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (playerState.currentUri != null) {
                                // Screen 3 & 4: Video Playing directly on TextureView for 100% GPU shader blending!
                                AndroidView(
                                    factory = { ctx ->
                                        android.view.TextureView(ctx).apply {
                                            viewModel.playerManager.player.setVideoTextureView(this)
                                            layoutParams = ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.MATCH_PARENT
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // Screen 2: NO SIGNAL STATIC & MULTI-CHANNEL TEST PATTERNS (SMPTE / RF Noise / Blue Screen)
                                CrtNoSignalStatic(
                                    channel = playerState.channel,
                                    channelLabel = String.format("CH %02d", playerState.channel)
                                )
                            }

                            // Vintage Phosphor / Camcorder OSD Overlay
                            CrtOsdOverlay(
                                playerState = playerState,
                                showTimestamp = crtSettings.showTimestamp
                            )
                        }
                    }
                }
            },
            bottomControls = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Playback Controls Row (Screen 4) - appears when tapped
                    if (playerState.currentUri != null || playerState.youtubeVideoId != null) {
                        CrtControlsOverlay(
                            isVisible = controlsVisible,
                            playerState = playerState,
                            onPlay = { viewModel.playerManager.play() },
                            onPause = { viewModel.playerManager.pause() },
                            onRewind = { viewModel.rewind() },
                            onFastForward = { viewModel.fastForward() },
                            onStop = { viewModel.stop() },
                            onSeek = { viewModel.seekTo(it) }
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    // Physical TV controls: Real 3D molded buttons (POWER with jewel LED, CHANNEL rocker, VOLUME rocker, MENU, INPUT, SETTINGS)
                    TvControlPanel(
                        isPoweredOn = playerState.isPoweredOn,
                        onPowerClick = { viewModel.togglePower() },
                        onChannelDown = { viewModel.onChannelDown() },
                        onChannelUp = { viewModel.onChannelUp() },
                        onVolumeDown = { viewModel.onVolumeDown() },
                        onVolumeUp = { viewModel.onVolumeUp() },
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onInputClick = { showSourceDialog = true },
                        onSettingsClick = onNavigateToSettings,
                        isRetroSkin = (activePreset == com.retro.crttv.crt.CrtPreset.RETRO)
                    )
                }
            }
        )
    }
}
