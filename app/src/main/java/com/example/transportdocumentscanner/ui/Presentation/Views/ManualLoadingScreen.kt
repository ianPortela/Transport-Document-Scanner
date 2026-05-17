package com.example.transportdocumentscanner.ui.Presentation.Views

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import com.example.transportdocumentscanner.ui.Presentation.ViewModels.DocumentViewModel
import com.example.transportdocumentscanner.ui.Presentation.State.DocumentState
import com.example.transportdocumentscanner.ui.theme.Purple40
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ManualLoadingScreen(
    typeDocument: String,
    modifier: Modifier = Modifier.fillMaxSize(),
    navigateToHome: () -> Unit,
    viewModel: DocumentViewModel = DocumentViewModel()) {

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 40.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = navigateToHome
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver al inicio"
                    )
                }
                Text(
                    text = "Registro Manual de Datos",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(40.dp))

            val doc by viewModel.doc.collectAsState()

            InputFields(
                doc = doc,
                viewModel = viewModel,
                typeDocument = typeDocument,
                context = context,
                snackbarHostState = snackbarHostState,
                scope = scope,
                onClear = { viewModel.onClear() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun InputFields(
    doc: DocumentState,
    viewModel: DocumentViewModel,
    typeDocument: String,
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onClear: () -> Unit
)
{
    DateField(doc, viewModel)
    doc.errors["date"]?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.origin,
        onValueChange = viewModel::onOriginChange,
        label = { Text("Origen de Carga") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["origin"]?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.destiny,
        onValueChange = viewModel::onDestinyChange,
        label = { Text("Destino de Carga") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["destiny"]?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.distance,
        onValueChange = viewModel::onDistanceChange,
        label = { Text("Distancia") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["distance"]?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.product,
        onValueChange = viewModel::onProductChange,
        label = { Text("Producto") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["product"]?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.weight,
        onValueChange = viewModel::onWeightChange,
        label = { Text("Peso") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["weight"]?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.idDocument,
        onValueChange = viewModel::onIdDocumentChange,
        label = { Text("CTG / Nro. de Remito") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["idDocument"]?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.rate,
        onValueChange = viewModel::onRateChange,
        label = { Text("Tarifa") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["rate"]?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.amount,
        onValueChange = viewModel::onAmountChange,
        label = { Text("Importe") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["amount"]?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(40.dp))

    val focusManager = LocalFocusManager.current

    Button(
        onClick = {
            scope.launch {
                val result = viewModel.validate(typeDocument)

                if (result.isValid) {

                    val uri = withContext(Dispatchers.IO) {
                        viewModel.writeExcel(context, doc)
                    }

                    onClear()

                    focusManager.clearFocus()

                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = "Registro guardado",
                        actionLabel = "Abrir",
                        duration = SnackbarDuration.Short
                    )

                    if (snackbarResult == SnackbarResult.ActionPerformed && uri != null) {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }
    ) {
        Text(
            text = "Registrar",
            fontSize = 35.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .height(60.dp)
                .width(280.dp)
                .wrapContentHeight(Alignment.CenterVertically)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(doc: DocumentState, viewModel: DocumentViewModel) {
    Row() {
        val stateDP = rememberDatePickerState()
        var showDialog by remember {
            mutableStateOf(false)
        }
        TextField(
            value = doc.date,
            onValueChange = viewModel::onDateChange,
            label = { Text("Fecha") },
            modifier = Modifier.width(260.dp),
            isError = doc.errors.containsKey("date")
        )
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(
            onClick = { showDialog = true },
            Modifier.width(90.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Purple40)

        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Seleccionar fecha",
                tint = Color.White
            )
        }
        if(showDialog) {
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            stateDP.selectedDateMillis?.let { millis ->

                                val localDate = Instant
                                    .ofEpochMilli(millis)
                                    .atZone(ZoneId.of("UTC"))
                                    .toLocalDate()

                                val day = String.format("%02d", localDate.dayOfMonth)
                                val month = String.format("%02d", localDate.monthValue)
                                val year = localDate.year

                                viewModel.onDateChange(
                                    "${day}/${month}/${year}"
                                )
                            }

                            showDialog = false
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = stateDP)
            }
        }
    }
}