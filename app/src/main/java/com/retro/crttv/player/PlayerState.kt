package com.retro.crttv.player

import android.net.Uri
import com.retro.crttv.crt.AspectRatioMode

data class PlayerState(
    val currentUri: Uri? = null,
    val youtubeVideoId: String? = null,
    val isOnlineStream: Boolean = false,
    val isPlaying: Boolean = false,
    val durationMs: Long = 0L,
    val currentPositionMs: Long = 0L,
    val aspectRatio: AspectRatioMode = AspectRatioMode.FOUR_BY_THREE,
    val isMuted: Boolean = false,
    val volume: Float = 0.5f,
    val channel: Int = 3,
    val osdMessage: String? = "AV 1",
    val osdTimestamp: Long = System.currentTimeMillis(),
    val isPoweredOn: Boolean = true,
    val isEnded: Boolean = false,
    val seekEventId: Long = 0L,
    val userSeekTargetMs: Long = 0L
) {
    val progress: Float
        get() = if (durationMs > 0L) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    val formattedCurrentTime: String
        get() = formatTime(currentPositionMs)

    val formattedTotalTime: String
        get() = formatTime(durationMs)

    private fun formatTime(ms: Long): String {
        if (ms <= 0L) return "00:00"
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}
