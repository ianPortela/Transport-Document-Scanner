package com.example.transportdocumentscanner.ui.Models

class Document(
    val idDocument : Int,
    var date : String,
    var pickupLocation : String,
    var unloadingLocation : String,
    var distance : Int,
    var product : String,
    var weight : Int,
    var tariff : Double,
    val documentType : DocumentType
) {
    var amount : Double

    init {
        this.amount = weight * tariff
    }

}