package com.example.transportdocumentscanner.ui.Models


data class Document(
    var idDocument : String,
    var date : String,
    var origin : String,
    var destiny : String,
    var distance : Int,
    var product : String,
    var weight : Double,
    var rate : Int,
    var amount : Double
)