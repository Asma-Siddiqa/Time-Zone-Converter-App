package com.timezone.converter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.timezone.converter.navigation.NavRoutes
import com.timezone.converter.ui.screens.AddTimeZoneScreen
import com.timezone.converter.ui.screens.ConverterScreen
import com.timezone.converter.ui.screens.HomeScreen
import com.timezone.converter.ui.theme.TimeZoneConverterTheme
import com.timezone.converter.viewmodel.TimeZoneViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimeZoneConverterTheme {
                TimeZoneApp()
            }
        }
    }
}

@Composable
fun TimeZoneApp() {
    val navController = rememberNavController()
    val viewModel: TimeZoneViewModel = viewModel()

    NavHost(navController = navController, startDestination = NavRoutes.HOME) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAdd = { navController.navigate(NavRoutes.ADD_TIME_ZONE) },
                onNavigateToConverter = { navController.navigate(NavRoutes.CONVERTER) }
            )
        }
        composable(NavRoutes.ADD_TIME_ZONE) {
            AddTimeZoneScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.CONVERTER) {
            ConverterScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
