package com.retro.crttv

import android.app.Application
import com.retro.crttv.data.preferences.UserPreferencesRepository
import com.retro.crttv.data.repository.VideoRepository

class CrtTvApp : Application() {

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    lateinit var videoRepository: VideoRepository
        private set

    override fun onCreate() {
        super.onCreate()
        preferencesRepository = UserPreferencesRepository(this)
        videoRepository = VideoRepository(this)
    }
}
