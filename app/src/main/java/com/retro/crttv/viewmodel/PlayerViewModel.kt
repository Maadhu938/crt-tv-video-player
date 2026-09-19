package com.retro.crttv.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.retro.crttv.crt.AspectRatioMode
import com.retro.crttv.crt.CrtPreset
import com.retro.crttv.crt.CrtSettings
import com.retro.crttv.data.preferences.UserPreferencesRepository
import com.retro.crttv.data.repository.VideoRepository
import com.retro.crttv.player.Media3PlayerManager
import com.retro.crttv.player.PlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel(
    application: Application,
    val videoRepository: VideoRepository,
    val preferencesRepository: UserPreferencesRepository
) : AndroidViewModel(application) {

    val playerManager = Media3PlayerManager(application, viewModelScope)

    val playerState: StateFlow<PlayerState> = playerManager.playerState

    val crtSettings: StateFlow<CrtSettings> = preferencesRepository.crtSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CrtSettings())

    val activePreset: StateFlow<CrtPreset> = preferencesRepository.activePresetFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CrtPreset.CLASSIC)

    init {
        // Sync saved channel and volume from DataStore
        viewModelScope.launch {
            preferencesRepository.channelFlow.collect { ch ->
                playerManager.setChannel(ch)
            }
        }
        viewModelScope.launch {
            preferencesRepository.volumeFlow.collect { vol ->
                playerManager.setVolume(vol / 100f)
            }
        }
    }

    fun scanDeviceVideos() {
        viewModelScope.launch {
            videoRepository.scanDeviceVideos()
        }
    }

    fun playVideo(uri: Uri, startPosition: Long = 0L) {
        val item = videoRepository.addVideoUri(uri)
        playerManager.playUri(uri, if (startPosition > 0L) startPosition else item.lastPlayedPositionMs)
    }

    fun togglePlayPause() = playerManager.togglePlayPause()

    fun stop() = playerManager.stop()

    fun rewind() = playerManager.rewind()

    fun fastForward() = playerManager.fastForward()

    fun seekTo(positionMs: Long) {
        playerManager.seekTo(positionMs)
        val uri = playerState.value.currentUri
        if (uri != null) {
            videoRepository.updatePlaybackPosition(uri, positionMs, playerState.value.durationMs)
        }
    }

    fun onChannelUp() {
        val videos = videoRepository.videos.value
        if (videos.isNotEmpty()) {
            val currentUri = playerState.value.currentUri
            val currentIndex = videos.indexOfFirst { it.uri == currentUri }
            val nextIndex = if (currentIndex < 0 || currentIndex >= videos.size - 1) 0 else currentIndex + 1
            val nextVideo = videos[nextIndex]
            playerManager.setChannel(nextIndex + 1)
            playerManager.playUri(nextVideo.uri, nextVideo.lastPlayedPositionMs)
            val cleanTitle = nextVideo.name.take(14)
            playerManager.showOsd(String.format("CH %02d: %s", nextIndex + 1, cleanTitle))
        } else {
            val currentCh = playerState.value.channel
            val nextCh = if (currentCh >= 4) 1 else currentCh + 1
            playerManager.setChannel(nextCh)
            val label = when (nextCh) {
                1 -> "COLOR BARS"
                2 -> "NO SIGNAL"
                3 -> "AV 1: NO INPUT"
                else -> "TEST PATTERN"
            }
            playerManager.showOsd(String.format("CH %02d: %s", nextCh, label))
        }
        viewModelScope.launch {
            preferencesRepository.updateChannel(playerState.value.channel)
        }
    }

    fun onChannelDown() {
        val videos = videoRepository.videos.value
        if (videos.isNotEmpty()) {
            val currentUri = playerState.value.currentUri
            val currentIndex = videos.indexOfFirst { it.uri == currentUri }
            val prevIndex = if (currentIndex <= 0) videos.size - 1 else currentIndex - 1
            val prevVideo = videos[prevIndex]
            playerManager.setChannel(prevIndex + 1)
            playerManager.playUri(prevVideo.uri, prevVideo.lastPlayedPositionMs)
            val cleanTitle = prevVideo.name.take(14)
            playerManager.showOsd(String.format("CH %02d: %s", prevIndex + 1, cleanTitle))
        } else {
            val currentCh = playerState.value.channel
            val prevCh = if (currentCh <= 1) 4 else currentCh - 1
            playerManager.setChannel(prevCh)
            val label = when (prevCh) {
                1 -> "COLOR BARS"
                2 -> "NO SIGNAL"
                3 -> "AV 1: NO INPUT"
                else -> "TEST PATTERN"
            }
            playerManager.showOsd(String.format("CH %02d: %s", prevCh, label))
        }
        viewModelScope.launch {
            preferencesRepository.updateChannel(playerState.value.channel)
        }
    }

    fun onVolumeUp() {
        playerManager.adjustVolume(0.05f)
        val volInt = ((playerState.value.volume) * 100).toInt()
        playerManager.showOsd("VOL $volInt")
        viewModelScope.launch {
            preferencesRepository.updateVolume(volInt)
        }
    }

    fun onVolumeDown() {
        playerManager.adjustVolume(-0.05f)
        val volInt = ((playerState.value.volume) * 100).toInt()
        playerManager.showOsd("VOL $volInt")
        viewModelScope.launch {
            preferencesRepository.updateVolume(volInt)
        }
    }

    /**
     * Plays an online video stream or YouTube link.
     */
    fun playOnlineStream(url: String) {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            val streamUri = if (trimmed.contains("youtube.com") || trimmed.contains("youtu.be")) {
                resolveYouTubeStreamUri(trimmed)
            } else {
                Uri.parse(trimmed)
            }

            withContext(Dispatchers.Main) {
                if (streamUri != null) {
                    playVideo(streamUri)
                    playerManager.showOsd("CH ONLINE: STREAM")
                } else {
                    playerManager.showOsd("INVALID STREAM URL")
                }
            }
        }
    }

    private fun resolveYouTubeStreamUri(youtubeUrl: String): Uri? {
        val videoId = extractYouTubeVideoId(youtubeUrl) ?: return null
        // Public high-reliability Invidious & Piped streaming endpoints for direct video stream resolution
        val endpoints = listOf(
            "https://inv.tux.pizza/api/v1/videos/$videoId",
            "https://invidious.nerdvpn.de/api/v1/videos/$videoId",
            "https://pipedapi.kavin.rocks/streams/$videoId"
        )

        for (endpoint in endpoints) {
            try {
                val conn = (java.net.URL(endpoint).openConnection() as java.net.HttpURLConnection).apply {
                    connectTimeout = 4000
                    readTimeout = 4000
                    setRequestProperty("User-Agent", "Mozilla/5.0")
                }
                if (conn.responseCode == 200) {
                    val body = conn.inputStream.bufferedReader().readText()
                    // Extract first playable stream URL (mp4 or hls)
                    val urlRegex = Regex(""""url"\s*:\s*"([^"]+\.mp4[^"]*)"""")
                    val match = urlRegex.find(body)
                    if (match != null) {
                        val streamUrl = match.groupValues[1].replace("\\/", "/")
                        return Uri.parse(streamUrl)
                    }
                }
            } catch (e: Exception) {
                // Try next endpoint
            }
        }
        // Fallback: direct embed URI
        return Uri.parse("https://www.youtube.com/watch?v=$videoId")
    }

    private fun extractYouTubeVideoId(url: String): String? {
        val pattern = Regex("""(?:v=|\/|embed\/|shorts\/|youtu\.be\/)([a-zA-Z0-9_-]{11})""")
        return pattern.find(url)?.groupValues?.get(1)
    }

    fun togglePower() {
        playerManager.togglePower()
    }

    fun setAspectRatio(mode: AspectRatioMode) {
        viewModelScope.launch {
            val current = crtSettings.value
            preferencesRepository.saveCrtSettings(current.copy(aspectRatio = mode))
        }
    }

    override fun onCleared() {
        val currentUri = playerState.value.currentUri
        if (currentUri != null) {
            videoRepository.updatePlaybackPosition(
                currentUri,
                playerState.value.currentPositionMs,
                playerState.value.durationMs
            )
        }
        playerManager.release()
        super.onCleared()
    }
}
