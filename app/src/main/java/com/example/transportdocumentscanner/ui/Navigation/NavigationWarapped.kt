package com.example.transportdocumentscanner.ui.Navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.transportdocumentscanner.ui.Presentation.Views.HomeScreen
import com.example.transportdocumentscanner.ui.Presentation.Views.ManualLoadingScreen
import com.example.transportdocumentscanner.ui.Presentation.Views.ScannerScreen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun NavigationWarapped() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                navigateToManualLoading = {typeDoc -> navController.navigate(LoadingScreen(typeDoc = typeDoc)) },
                navigateToScanner = {typeDoc -> navController.navigate(Scanner(typeDoc = typeDoc))}
            )
        }
        composable<LoadingScreen> {backStackEntry ->
            val loadingScreen = backStackEntry.toRoute<LoadingScreen>()
            ManualLoadingScreen(
                loadingScreen.typeDoc,
                navigateToHome = { navController.navigate(Home) }
            )
        }
        composable<Scanner> {backStackEntry ->
            val scannerScreen = backStackEntry.toRoute<Scanner>()
            ScannerScreen(
                typeDoc = scannerScreen.typeDoc,
                onScanComplete = {document ->
                    
                }
            )
        }
    }
}

