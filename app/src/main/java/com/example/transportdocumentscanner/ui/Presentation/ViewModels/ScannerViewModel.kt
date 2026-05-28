package com.example.transportdocumentscanner.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transportdocumentscanner.ui.Domain.Models.Document
import com.example.transportdocumentscanner.ui.Domain.Models.DocumentField
import com.example.transportdocumentscanner.ui.Presentation.State.ScannerState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ScannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ScannerState>(ScannerState.Idle)
    val uiState: StateFlow<ScannerState> = _uiState.asStateFlow()

    private val _currentFieldIndex = MutableStateFlow(0)
    val currentFieldIndex: StateFlow<Int> = _currentFieldIndex.asStateFlow()

    private val fields = DocumentField.values()
    val currentField: DocumentField
        get() = fields[_currentFieldIndex.value]

    private var scannedData = Document()

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)


    fun processCapturedImage(bitmap: Bitmap, typeDoc: String) {
        viewModelScope.launch {
            _uiState.update { ScannerState.Processing }
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val result = recognizer.process(image).await()
                val rawText = result.text

                val validData = extractFieldData(currentField, rawText, typeDoc)

                if (validData != null) {
                    saveFieldData(currentField, validData)
                    advanceToNextField()
                } else {
                    val cleanError = rawText.replace("\n", " ").trim()
                    _uiState.update {
                        ScannerState.Error("Dato inválido para ${currentField.title}. Se leyó: '$cleanError'")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { ScannerState.Error("Error en OCR: ${e.message}") }
            }
        }
    }


    private fun extractFieldData(field: DocumentField, text: String, typeDoc: String): String? {
        val cleanText = text.replace("\n", " ").trim()
        if (cleanText.isEmpty()) return null

        return when (field) {
            DocumentField.ID_DOCUMENT -> {
                if(typeDoc.equals("Carta de Porte")) {
                    val regex = Regex("""(?<!\d)\d{11}(?!\d)""")
                    val match = regex.find(cleanText)

                    match?.value
                }else {
                    val regex = Regex("""(?<!\d)\d{4}-\d{8}(?!\d)""")
                    val match = regex.find(cleanText)

                    match?.value
                }
            }
            DocumentField.DATE -> {
                val regex = Regex("""(\d{2})\s*[-/]\s*(\d{2})\s*[-/]\s*(\d{4})""")
                val match = regex.find(cleanText)

                match?.let { "${it.groupValues[1]}/${it.groupValues[2]}/${it.groupValues[3]}" }
            }
            DocumentField.DISTANCE, DocumentField.WEIGHT, DocumentField.RATE -> {
                val numText = cleanText.replace("O", "0", ignoreCase = true)

                val regex = Regex("""^-?\d+$""")
                val match = regex.find(numText)
                match?.value
            }
            else -> {
                cleanText
            }
        }
    }

    private fun saveFieldData(field: DocumentField, text: String) {
        scannedData = when (field) {
            DocumentField.ID_DOCUMENT -> scannedData.copy(idDocument = text)
            DocumentField.DATE -> scannedData.copy(date = text)
            DocumentField.ORIGIN -> scannedData.copy(origin = text)
            DocumentField.DESTINY -> scannedData.copy(destiny = text)
            DocumentField.DISTANCE -> scannedData.copy(distance = text.toIntOrNull() ?: 0)
            DocumentField.WEIGHT -> scannedData.copy(weight = text.toDoubleOrNull() ?: 0.0)
            DocumentField.PRODUCT -> scannedData.copy(product = text)
            DocumentField.RATE -> scannedData.copy(rate = text.toIntOrNull() ?: 0)
        }
    }

    private fun advanceToNextField() {
        if (_currentFieldIndex.value < fields.size - 1) {
            _currentFieldIndex.value += 1
            _uiState.update { ScannerState.Idle }
        } else {
            // Fin del flujo
            _uiState.update { ScannerState.Success(scannedData) }
        }
    }

    fun resetError() {
        _uiState.update { ScannerState.Idle }
    }
}