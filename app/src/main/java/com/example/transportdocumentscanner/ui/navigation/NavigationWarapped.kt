package com.example.transportdocumentscanner.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.transportdocumentscanner.ui.Views.HomeScreen
import com.example.transportdocumentscanner.ui.Views.ManualLoadingScreen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun NavigationWarapped() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                navigateToManualLoading = {
                    navController.navigate(LoadingScreen)
                }
            )
        }
        composable<LoadingScreen> {
            ManualLoadingScreen()
        }
    }
}

