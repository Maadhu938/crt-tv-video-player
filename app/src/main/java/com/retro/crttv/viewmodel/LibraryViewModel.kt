package com.retro.crttv.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retro.crttv.data.model.VideoItem
import com.retro.crttv.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class LibrarySortOrder {
    DATE_ADDED,
    NAME,
    DURATION
}

class LibraryViewModel(
    val videoRepository: VideoRepository
) : ViewModel() {

    fun scanDeviceVideos() {
        viewModelScope.launch {
            videoRepository.scanDeviceVideos()
        }
    }

    private val _sortOrder = MutableStateFlow(LibrarySortOrder.DATE_ADDED)
    val sortOrder: StateFlow<LibrarySortOrder> = _sortOrder.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Videos, 1: Folders, 2: Favorites
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    val videos: StateFlow<List<VideoItem>> = combine(videoRepository.videos, _sortOrder) { list, sort ->
        when (sort) {
            LibrarySortOrder.DATE_ADDED -> list.sortedByDescending { it.dateAdded }
            LibrarySortOrder.NAME -> list.sortedBy { it.name.lowercase() }
            LibrarySortOrder.DURATION -> list.sortedByDescending { it.durationMs }
        }
    }.let {
        MutableStateFlow(emptyList<VideoItem>()).apply {
            // We can expose the combined flow directly
        }
    }

    val allVideos = videoRepository.videos
    val favorites = videoRepository.favorites

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun setSortOrder(order: LibrarySortOrder) {
        _sortOrder.value = order
    }

    fun onVideosPicked(uris: List<Uri>) {
        uris.forEach { uri ->
            videoRepository.addVideoUri(uri)
        }
    }

    fun toggleFavorite(uri: Uri) {
        videoRepository.toggleFavorite(uri)
    }

    fun removeVideo(uri: Uri) {
        videoRepository.removeVideo(uri)
    }
}
