package com.example.transportdocumentscanner.ui.Models

data class Document(
    var idDocument : String = "",
    var date : String = "",
    var origin : String = "",
    var destiny : String = "",
    var distance : String = "",
    var product : String = "",
    var weight : String = "",
    var rate : String = "",
    var amount : String = ""
)