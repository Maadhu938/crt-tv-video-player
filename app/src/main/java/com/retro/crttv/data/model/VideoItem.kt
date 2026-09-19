package com.retro.crttv.data.model

import android.net.Uri

data class VideoItem(
    val uri: Uri,
    val name: String,
    val durationMs: Long = 0L,
    val sizeBytes: Long = 0L,
    val dateAdded: Long = System.currentTimeMillis(),
    val lastPlayedPositionMs: Long = 0L,
    val isFavorite: Boolean = false
) {
    val formattedDuration: String
        get() {
            if (durationMs <= 0L) return "--:--"
            val totalSeconds = durationMs / 1000
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
