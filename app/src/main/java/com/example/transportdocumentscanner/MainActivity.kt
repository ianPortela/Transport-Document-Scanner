package com.example.transportdocumentscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.example.transportdocumentscanner.ui.theme.TransportDocumentScannerTheme
import com.example.transportdocumentscanner.ui.Navigation.NavigationWarapped

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TransportDocumentScannerTheme {
                androidx.compose.material3.Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationWarapped()
                }
            }
        }
    }
}
