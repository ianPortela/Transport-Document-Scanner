package com.example.transportdocumentscanner.ui.Presentation.ViewModels

import androidx.lifecycle.ViewModel
import com.example.transportdocumentscanner.ui.Presentation.State.DocumentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DocumentViewModel : ViewModel() {
    private val _doc = MutableStateFlow(DocumentState())
    val doc: StateFlow<DocumentState> = _doc

    fun onIdDocumentChange (value: String) {
        _doc.value = _doc.value.copy(idDocument = value)
    }

    fun onDateChange (value: String) {
        _doc.value = _doc.value.copy(date = value)
    }

    fun onOriginChange (value: String) {
        _doc.value = _doc.value.copy(origin = value)
    }

    fun onDestinyChange (value: String) {
        _doc.value = _doc.value.copy(destiny = value)
    }

    fun onDistanceChange (value: String) {
        _doc.value = _doc.value.copy(distance = value)
    }

    fun onProductChange (value: String) {
        _doc.value = _doc.value.copy(product = value)
    }

    fun onWeightChange (value: String) {
        _doc.value = _doc.value.copy(weight = value)
    }

    fun onRateChange (value: String) {
        _doc.value = _doc.value.copy(rate = value)
    }

    fun onAmountChange (value: String) {
        _doc.value = _doc.value.copy(amount = value)
    }

    fun onClear () {
        _doc.value = DocumentState()
    }
}