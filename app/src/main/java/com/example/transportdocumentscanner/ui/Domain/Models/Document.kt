package com.example.transportdocumentscanner.ui.Domain.Models


data class Document(
    var idDocument : String = "",
    var date : String = "",
    var origin : String = "",
    var destiny : String = "",
    var distance : Int = 0,
    var product : String = "",
    var weight : Double = 0.0,
    var rate : Int = 0,
    var amount : Double = 0.0
)