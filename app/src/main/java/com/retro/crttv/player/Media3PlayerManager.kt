package com.retro.crttv.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class Media3PlayerManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    val player: ExoPlayer by lazy {
        ExoPlayer.Builder(context)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_OFF
                addListener(playerListener)
            }
    }

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var progressJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) {
                startProgressTracker()
            } else {
                stopProgressTracker()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    _playerState.update {
                        it.copy(
                            durationMs = player.duration.coerceAtLeast(0L),
                            currentPositionMs = player.currentPosition.coerceAtLeast(0L),
                            isEnded = false
                        )
                    }
                }
                Player.STATE_ENDED -> {
                    _playerState.update { it.copy(isEnded = true, isPlaying = false) }
                    stopProgressTracker()
                }
                else -> Unit
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            showOsd("ERROR")
        }
    }

    fun playUri(uri: Uri, startPositionMs: Long = 0L) {
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        if (startPositionMs > 0L) {
            player.seekTo(startPositionMs)
        }
        player.prepare()
        player.playWhenReady = true
        _playerState.update {
            it.copy(
                currentUri = uri,
                currentPositionMs = startPositionMs,
                isPlaying = true,
                isEnded = false
            )
        }
        showOsd("PLAY ▶")
    }

    fun play() {
        if (_playerState.value.isEnded) {
            player.seekTo(0)
        }
        player.play()
        showOsd("PLAY ▶")
    }

    fun pause() {
        player.pause()
        showOsd("PAUSE ❚❚")
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun stop() {
        player.stop()
        _playerState.update { it.copy(isPlaying = false, currentPositionMs = 0L) }
        showOsd("STOP ■")
    }

    fun seekTo(positionMs: Long) {
        val clamped = positionMs.coerceIn(0L, player.duration.coerceAtLeast(0L))
        player.seekTo(clamped)
        _playerState.update { it.copy(currentPositionMs = clamped) }
    }

    fun rewind() {
        val newPos = (player.currentPosition - 10000L).coerceAtLeast(0L)
        player.seekTo(newPos)
        _playerState.update { it.copy(currentPositionMs = newPos) }
        showOsd("REW <<")
    }

    fun fastForward() {
        val newPos = (player.currentPosition + 10000L).coerceAtMost(player.duration.coerceAtLeast(0L))
        player.seekTo(newPos)
        _playerState.update { it.copy(currentPositionMs = newPos) }
        showOsd("FFWD >>")
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        player.volume = clamped
        _playerState.update { it.copy(volume = clamped) }
        val volInt = (clamped * 100).toInt()
        showOsd("VOL $volInt")
    }

    fun adjustVolume(delta: Float) {
        val current = _playerState.value.volume
        setVolume(current + delta)
    }

    fun setChannel(channel: Int) {
        val clamped = channel.coerceIn(1, 99)
        _playerState.update { it.copy(channel = clamped) }
        showOsd(String.format("CH %02d", clamped))
    }

    fun cycleChannel(delta: Int) {
        var next = _playerState.value.channel + delta
        if (next < 1) next = 99
        if (next > 99) next = 1
        setChannel(next)
    }

    fun togglePower(): Boolean {
        val current = _playerState.value.isPoweredOn
        val newPower = !current
        if (!newPower && player.isPlaying) {
            player.pause()
        }
        _playerState.update { it.copy(isPoweredOn = newPower) }
        return newPower
    }

    fun showOsd(message: String) {
        _playerState.update {
            it.copy(
                osdMessage = message,
                osdTimestamp = System.currentTimeMillis()
            )
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = coroutineScope.launch(Dispatchers.Main) {
            while (isActive) {
                if (player.isPlaying) {
                    _playerState.update {
                        it.copy(
                            currentPositionMs = player.currentPosition.coerceAtLeast(0L),
                            durationMs = player.duration.coerceAtLeast(0L)
                        )
                    }
                }
                delay(300)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracker()
        player.removeListener(playerListener)
        player.release()
    }
}
