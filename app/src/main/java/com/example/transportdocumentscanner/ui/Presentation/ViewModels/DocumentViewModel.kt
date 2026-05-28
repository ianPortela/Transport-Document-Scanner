package com.example.transportdocumentscanner.ui.Presentation.ViewModels

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.transportdocumentscanner.ui.Data.Export.ExportExcel
import com.example.transportdocumentscanner.ui.Domain.Validations.ValidationResult
import com.example.transportdocumentscanner.ui.Presentation.State.DocumentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DocumentViewModel : ViewModel() {
    private val exportExcel = ExportExcel()
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

    fun validate (typeDocument: String) : ValidationResult {
        val data = _doc.value
        val errors = mutableMapOf<String, String>()

        if (!isValidId(data.idDocument, typeDocument)) {
            errors["idDocument"] = "El id es inválido"
        }

        if (data.date.isBlank()) {
            errors["date"] = "La fecha es obligatoria"
        }

        if (data.origin.isBlank()) {
            errors["origin"] = "El origen es obligatorio"
        }

        if (data.destiny.isBlank()) {
            errors["destiny"] = "El destino es obligatorio"
        }

        if (data.distance.isBlank()) {
            errors["distance"] = "La distancia es obligatoria"
        }else {
            if (data.distance.toIntOrNull() == null || data.distance.toInt() <= 0) {
                errors["distance"] = "Distancia inválida"
            }
        }

        if(data.product.isBlank()) {
            errors["product"] = "El producto es obligatorio"
        }

        if (data.weight.isBlank()) {
            errors["weight"] = "El Peso es obligatorio"
        }else {
            if (data.weight.toDoubleOrNull() == null || data.weight.toDouble() <= 0 || data.weight.toDouble() >= 50000) {
                errors["weight"] = "Peso invalido"
            }
        }

        if (!data.rate.isBlank()) {
            if(data.rate.toIntOrNull() == null || data.rate.toInt() <= 0) {
                errors["rate"] = "Tarifa inválida"
            }
        }

        if (!data.amount.isBlank()) {
            if(data.amount.toDoubleOrNull() == null || data.amount.toDouble() <= 0){
                errors["amount"] = "El Importe es invalido"
            }
        }

        _doc.value = _doc.value.copy(errors = errors)

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }

    private fun isValidId(idDocument: String, typeDocument: String) : Boolean{
        val regex = when(typeDocument) {
            "Remito" -> Regex("^\\d{4}-\\d{8}$")
            "Carta de Porte" -> Regex("^\\d{11}$")
            else -> return false
        }
        return regex.matches(idDocument)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun writeExcel(context: android.content.Context, doc: DocumentState) : Uri? {
        return exportExcel.write(context, doc)
    }
}