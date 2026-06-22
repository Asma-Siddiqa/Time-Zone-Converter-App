package com.timezone.converter.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddTimeZone : Screen("add_timezone")
    data object Converter : Screen("converter")
}

object NavRoutes {
    const val HOME = "home"
    const val ADD_TIME_ZONE = "add_timezone"
    const val CONVERTER = "converter"
}
