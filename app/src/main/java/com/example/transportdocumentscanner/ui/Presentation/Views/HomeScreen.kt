package com.example.transportdocumentscanner.ui.Presentation.Views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(modifier: Modifier = Modifier.fillMaxSize(), navigateToManualLoading: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        TitleApp()
        Spacer(modifier = Modifier.height(65.dp))
        DropdownMenu()
        Spacer(modifier = Modifier.height(100.dp))
        RegistrationMethods(navigateToManualLoading)
    }
}

@Composable
fun RegistrationMethods(navigateToManualLoading: () -> Unit) {
    Text(
        text = "Metodos de registro",
        fontSize = 18.sp
    )
    ButtonRegistrationMethod("Subir PDF", action = {})
    ButtonRegistrationMethod("Escanear documento", action = {})
    ButtonRegistrationMethod("Subir foto", action = {})
    ButtonManualRegistration("Registrar manualmente",navigateTo = {navigateToManualLoading()})
}

@Composable
fun ButtonRegistrationMethod(method: String, action: () -> Unit) {
    Spacer(modifier = Modifier.height(30.dp))
    Button(
        //action va a ser la forma de registro dependiendo que metodo se elija
        onClick = { action() },
        modifier = Modifier.height(85.dp).width(380.dp),
    ) {
        Text(
            text = method,
            fontSize = 20.sp
        )
    }
}

@Composable
fun ButtonManualRegistration(method: String, navigateTo: () -> Unit) {
    Spacer(modifier = Modifier.height(30.dp))
    Button(
        onClick = { navigateTo() },
        modifier = Modifier.height(85.dp).width(380.dp),
    ) {
        Text(
            text = method,
            fontSize = 20.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu() {

    val options = listOf("Carta de Porte", "Remito")
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        TextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Seleccionar el tipo de documento") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .width(400.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TitleApp() {
    Text(
        text = "Transport Document Scanner",
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold
    )
}

