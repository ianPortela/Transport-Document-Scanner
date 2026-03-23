package com.example.transportdocumentscanner.ui.Views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ManualLoadingScreen(modifier:Modifier = Modifier.fillMaxSize(), ) {
    Column(
        modifier = Modifier
        .fillMaxSize()
        .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro Manual de Datos",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(40.dp))
        InputFields()
        Spacer(modifier = Modifier.height(55.dp))
        Button(
            onClick = {}
        ) {
            Text(
                text = "Registrar",
                fontSize = 35.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .height(85.dp)
                    .width(380.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun InputFields() {
    Input("Fecha")
    Spacer(modifier = Modifier.height(15.dp))
    Input("Origen de carga")
    Spacer(modifier = Modifier.height(15.dp))
    Input("Destino de carga")
    Spacer(modifier = Modifier.height(15.dp))
    Input("Distancia")
    Spacer(modifier = Modifier.height(15.dp))
    Input("Producto")
    Spacer(modifier = Modifier.height(15.dp))
    Input("Peso")
    Spacer(modifier = Modifier.height(15.dp))
    Input("ID del documento (Nro de remito / CTG)")
    Spacer(modifier = Modifier.height(15.dp))
    Input("Tarifa")
    Spacer(modifier = Modifier.height(15.dp))
    Input("Importe")
}

@Composable
fun Input(label:String) {
    var texto by remember { mutableStateOf("") }

    Spacer(modifier = Modifier.height(10.dp))
    TextField(
        value = texto,
        onValueChange = { texto = it },
        label = { Text(label) },
        modifier = Modifier.width(400.dp)
    )
}