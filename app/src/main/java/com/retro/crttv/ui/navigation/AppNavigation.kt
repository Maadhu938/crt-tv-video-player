package com.retro.crttv.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.retro.crttv.ui.home.HomeScreen
import com.retro.crttv.ui.library.VideoLibraryScreen
import com.retro.crttv.ui.presets.PresetPreviewScreen
import com.retro.crttv.ui.settings.SettingsScreen
import com.retro.crttv.ui.splash.SplashScreen
import com.retro.crttv.viewmodel.LibraryViewModel
import com.retro.crttv.viewmodel.PlayerViewModel
import com.retro.crttv.viewmodel.SettingsViewModel

@Composable
fun AppNavigation(
    playerViewModel: PlayerViewModel,
    libraryViewModel: LibraryViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val activePreset by settingsViewModel.activePreset.collectAsState()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
        modifier = modifier
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Home.route) {
            HomeScreen(
                viewModel = playerViewModel,
                onNavigateToLibrary = { navController.navigate(NavRoutes.Library.route) },
                onNavigateToSettings = { navController.navigate(NavRoutes.Settings.route) },
                onNavigateToPresets = { navController.navigate(NavRoutes.Presets.route) },
                onNavigateToHelp = { navController.navigate(NavRoutes.Help.route) },
                onNavigateToAbout = { navController.navigate(NavRoutes.About.route) }
            )
        }

        composable(NavRoutes.Library.route) {
            VideoLibraryScreen(
                viewModel = libraryViewModel,
                onVideoSelected = { uri ->
                    playerViewModel.playVideo(uri)
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = true }
                    }
                },
                onNavigateToSettings = { navController.navigate(NavRoutes.Settings.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateToPresets = { navController.navigate(NavRoutes.Presets.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Presets.route) {
            PresetPreviewScreen(
                currentPreset = activePreset,
                onApplyPreset = { preset ->
                    settingsViewModel.applyPreset(preset)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Help.route) {
            com.retro.crttv.ui.help.HelpScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.About.route) {
            com.retro.crttv.ui.about.AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
