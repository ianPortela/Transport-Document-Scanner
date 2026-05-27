package com.example.transportdocumentscanner.ui.Presentation.State

import com.example.transportdocumentscanner.ui.Domain.Models.Document

sealed class ScannerState {
    object Idle : ScannerState()
    object Processing : ScannerState()
    data class Error(val message: String) : ScannerState()
    data class Success(val data: Document) : ScannerState()
}