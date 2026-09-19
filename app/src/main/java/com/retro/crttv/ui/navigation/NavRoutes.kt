package com.retro.crttv.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Splash : NavRoutes("splash")
    data object Home : NavRoutes("home")
    data object Library : NavRoutes("library")
    data object Settings : NavRoutes("settings")
    data object Presets : NavRoutes("presets")
    data object Help : NavRoutes("help")
    data object About : NavRoutes("about")
}
