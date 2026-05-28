package com.example.transportdocumentscanner.ui.Domain.Models

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class DocumentField(val title: String, val width: Dp, val height: Dp) {

    ID_DOCUMENT("Nro. Documento", 200.dp, 50.dp),
    DATE("Fecha (DD/MM/YYYY)", 180.dp, 50.dp),
    ORIGIN("Origen", 260.dp, 50.dp),
    DESTINY("Destino", 260.dp, 50.dp),
    DISTANCE("Distancia (km)", 150.dp, 50.dp),
    WEIGHT("Peso (kg)", 150.dp, 50.dp),
    PRODUCT("Producto", 250.dp, 50.dp),
    RATE("Tarifa", 150.dp, 50.dp)
}