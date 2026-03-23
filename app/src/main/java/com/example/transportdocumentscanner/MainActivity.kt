package com.example.transportdocumentscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.transportdocumentscanner.ui.Views.HomeScreen
import com.example.transportdocumentscanner.ui.theme.TransportDocumentScannerTheme
import com.example.transportdocumentscanner.ui.navigation.NavigationWarapped

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TransportDocumentScannerTheme {
                androidx.compose.material3.Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    /*HomeScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.White)
                    )*/
                    NavigationWarapped()
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun HomePreview() {
    //HomeScreen()
    NavigationWarapped()
}