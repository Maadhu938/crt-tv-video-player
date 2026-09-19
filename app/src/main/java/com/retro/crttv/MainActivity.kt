package com.retro.crttv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.retro.crttv.ui.navigation.AppNavigation
import com.retro.crttv.ui.theme.CrtTvTheme
import com.retro.crttv.viewmodel.LibraryViewModel
import com.retro.crttv.viewmodel.PlayerViewModel
import com.retro.crttv.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val app by lazy { application as CrtTvApp }

    private val playerViewModel: PlayerViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlayerViewModel(
                    application = app,
                    videoRepository = app.videoRepository,
                    preferencesRepository = app.preferencesRepository
                ) as T
            }
        }
    }

    private val libraryViewModel: LibraryViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LibraryViewModel(videoRepository = app.videoRepository) as T
            }
        }
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(preferencesRepository = app.preferencesRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            CrtTvTheme {
                AppNavigation(
                    playerViewModel = playerViewModel,
                    libraryViewModel = libraryViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            val dataUri: Uri? = intent.data
            if (dataUri != null) {
                playerViewModel.playVideo(dataUri)
            }
        }
    }
}
