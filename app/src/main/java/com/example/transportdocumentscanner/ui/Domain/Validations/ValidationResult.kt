package com.example.transportdocumentscanner.ui.Domain.Validations

data class ValidationResult(
    val isValid: Boolean,
    val errors: Map<String, String> = emptyMap()
)
