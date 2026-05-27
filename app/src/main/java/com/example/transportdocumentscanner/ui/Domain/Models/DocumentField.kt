package com.example.transportdocumentscanner.ui.Domain.Models

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class DocumentField(val title: String, val width: Dp, val height: Dp) {
    FECHA("Fecha (DD/MM/YYYY)", 180.dp, 60.dp),
    ORIGEN("Origen", 250.dp, 60.dp),
    DESTINO("Destino", 250.dp, 60.dp),
    DISTANCIA("Distancia (km)", 150.dp, 60.dp),
    PESO("Peso (kg)", 150.dp, 60.dp),
    PRODUCTO("Producto", 250.dp, 60.dp)
}