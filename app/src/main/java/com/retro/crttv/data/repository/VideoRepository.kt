package com.retro.crttv.data.repository

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.retro.crttv.data.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class VideoRepository(private val context: Context) {

    private val _videos = MutableStateFlow<List<VideoItem>>(emptyList())
    val videos: StateFlow<List<VideoItem>> = _videos.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    /**
     * Scans the device's MediaStore directly for local videos.
     * Populates the video library automatically when storage permission is granted.
     */
    suspend fun scanDeviceVideos(): List<VideoItem> = withContext(Dispatchers.IO) {
        val videoList = mutableListOf<VideoItem>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_ADDED
        )
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        try {
            val queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            context.contentResolver.query(
                queryUri,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idCol = cursor.getColumnIndex(MediaStore.Video.Media._ID)
                val nameCol = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                val sizeCol = cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
                val durationCol = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                val dateCol = cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val id = if (idCol >= 0) cursor.getLong(idCol) else continue
                    val name = if (nameCol >= 0) cursor.getString(nameCol) ?: "Video $id" else "Video $id"
                    val size = if (sizeCol >= 0) cursor.getLong(sizeCol) else 0L
                    val duration = if (durationCol >= 0) cursor.getLong(durationCol) else 0L
                    val dateAdded = if (dateCol >= 0) cursor.getLong(dateCol) * 1000L else System.currentTimeMillis()

                    val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    videoList.add(
                        VideoItem(
                            uri = contentUri,
                            name = name,
                            sizeBytes = size,
                            durationMs = duration,
                            dateAdded = dateAdded,
                            isFavorite = _favorites.value.contains(contentUri.toString())
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (videoList.isNotEmpty()) {
            val existingUris = _videos.value.map { it.uri.toString() }.toSet()
            val newItems = videoList.filterNot { existingUris.contains(it.uri.toString()) }
            _videos.value = _videos.value + newItems
        }
        _videos.value
    }

    fun addVideoUri(uri: Uri): VideoItem {
        // Take persistable permission if content URI
        try {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, flags)
        } catch (e: Exception) {
            // Might be a non-persistable URI or file URI, which is okay
        }

        val name = queryFileName(uri) ?: "Unknown Video ${System.currentTimeMillis() % 1000}"
        val size = queryFileSize(uri)

        val newItem = VideoItem(
            uri = uri,
            name = name,
            sizeBytes = size,
            dateAdded = System.currentTimeMillis()
        )

        val current = _videos.value.toMutableList()
        // Replace if already exists or add to front
        val index = current.indexOfFirst { it.uri == uri }
        if (index >= 0) {
            current[index] = current[index].copy(name = name, sizeBytes = size)
        } else {
            current.add(0, newItem)
        }
        _videos.value = current
        return newItem
    }

    fun toggleFavorite(uri: Uri) {
        val uriStr = uri.toString()
        val currentFavs = _favorites.value.toMutableSet()
        if (currentFavs.contains(uriStr)) {
            currentFavs.remove(uriStr)
        } else {
            currentFavs.add(uriStr)
        }
        _favorites.value = currentFavs

        _videos.value = _videos.value.map {
            if (it.uri == uri) it.copy(isFavorite = currentFavs.contains(uriStr)) else it
        }
    }

    fun removeVideo(uri: Uri) {
        _videos.value = _videos.value.filterNot { it.uri == uri }
    }

    fun updatePlaybackPosition(uri: Uri, positionMs: Long, durationMs: Long) {
        _videos.value = _videos.value.map {
            if (it.uri == uri) {
                it.copy(
                    lastPlayedPositionMs = positionMs,
                    durationMs = if (durationMs > 0) durationMs else it.durationMs
                )
            } else {
                it
            }
        }
    }

    private fun queryFileName(uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        return it.getString(nameIndex)
                    }
                }
            }
        }
        return uri.lastPathSegment
    }

    private fun queryFileSize(uri: Uri): Long {
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        return it.getLong(sizeIndex)
                    }
                }
            }
        }
        return 0L
    }
}
