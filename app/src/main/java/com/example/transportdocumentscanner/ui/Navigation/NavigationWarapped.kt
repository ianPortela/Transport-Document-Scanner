package com.example.transportdocumentscanner.ui.Navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.transportdocumentscanner.ui.Presentation.State.DocumentState
import com.example.transportdocumentscanner.ui.Presentation.ViewModels.DocumentViewModel
import com.example.transportdocumentscanner.ui.Presentation.Views.HomeScreen
import com.example.transportdocumentscanner.ui.Presentation.Views.ManualLoadingScreen
import com.example.transportdocumentscanner.ui.Presentation.Views.ScannerScreen
import kotlin.math.log

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun NavigationWarapped() {
    val navController = rememberNavController()
    val documentViewModel: DocumentViewModel = viewModel()

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
                documentViewModel,
                navigateToHome = { navController.navigate(Home) }
            )
        }
        composable<Scanner> {backStackEntry ->
            val scannerScreen = backStackEntry.toRoute<Scanner>()

            ScannerScreen(
                typeDoc = scannerScreen.typeDoc,
                onScanComplete = {document ->
                    documentViewModel.onIdDocumentChange(document.idDocument)
                    documentViewModel.onDateChange(document.date)
                    documentViewModel.onOriginChange(document.origin)
                    documentViewModel.onDestinyChange(document.destiny)
                    documentViewModel.onDistanceChange(document.distance.toString())
                    documentViewModel.onProductChange(document.product)
                    documentViewModel.onWeightChange(document.weight.toString())
                    documentViewModel.onRateChange(document.rate.toString())
                    documentViewModel.onAmountChange(((document.weight/1000).toInt() * document.rate).toString())


                    navController.navigate(LoadingScreen(typeDoc = scannerScreen.typeDoc))
                }
            )
        }
    }
}

