package com.example.transportdocumentscanner.ui.Navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class LoadingScreen(val typeDoc: String)

