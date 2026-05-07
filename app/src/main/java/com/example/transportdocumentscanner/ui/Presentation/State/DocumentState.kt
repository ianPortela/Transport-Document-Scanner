package com.example.transportdocumentscanner.ui.Presentation.State

data class DocumentState (
    var idDocument : String = "",
    var date : String = "",
    var origin : String = "",
    var destiny : String = "",
    var distance : String = "",
    var product : String = "",
    var weight : String = "",
    var rate : String = "",
    var amount : String = "",
    val errors: Map<String, String> = emptyMap()
)