package com.example.transportdocumentscanner.ui.Presentation.Views

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.LocalDate
import androidx.compose.runtime.rememberCoroutineScope
import com.example.transportdocumentscanner.ui.Presentation.ViewModels.DocumentViewModel
import com.example.transportdocumentscanner.ui.Presentation.State.DocumentState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFRow

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ManualLoadingScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    navigateToHome: () -> Unit,
    viewModel: DocumentViewModel = DocumentViewModel()) {

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(  //Scaffold es necesario para que el Snackbar aparezca bien posicionado
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
                    onClick = navigateToHome,
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
                context = context,
                snackbarHostState = snackbarHostState,
                scope = scope,
                onClear = { viewModel.onClear() }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun InputFields(
    doc: DocumentState,
    viewModel: DocumentViewModel,
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onClear: () -> Unit
)
{

    TextField(
        value = doc.date,
        onValueChange = viewModel::onDateChange,
        label = { Text("Fecha") },
        modifier = Modifier.width(360.dp),
        isError = doc.errors.containsKey("idDocument")
    )
    doc.errors["date"]?.let {
        Text(text = it, color = Color.Red)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.origin,
        onValueChange = viewModel::onOriginChange,
        label = { Text("Origen de Carga") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["origin"]?.let {
        Text(text = it, color = Color.Red)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.destiny,
        onValueChange = viewModel::onDestinyChange,
        label = { Text("Destino de Carga") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["destiny"]?.let {
        Text(text = it, color = Color.Red)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.distance,
        onValueChange = viewModel::onDistanceChange,
        label = { Text("Distancia") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["distance"]?.let {
        Text(text = it, color = Color.Red)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.product,
        onValueChange = viewModel::onProductChange,
        label = { Text("Producto") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["product"]?.let {
        Text(text = it, color = Color.Red)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.weight,
        onValueChange = viewModel::onWeightChange,
        label = { Text("Peso") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["weight"]?.let {
        Text(text = it, color = Color.Red)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.idDocument,
        onValueChange = viewModel::onIdDocumentChange,
        label = { Text("CTG / Nro. de Remito") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["idDocument"]?.let {
        Text(text = it, color = Color.Red)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.rate,
        onValueChange = viewModel::onRateChange,
        label = { Text("Tarifa") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["rate"]?.let {
        Text(text = it, color = Color.Red)
    }
    Spacer(modifier = Modifier.height(18.dp))

    TextField(
        value = doc.amount,
        onValueChange = viewModel::onAmountChange,
        label = { Text("Importe") },
        modifier = Modifier.width(360.dp)
    )
    doc.errors["amount"]?.let {
        Text(text = it, color = Color.Red)
    }
    Spacer(modifier = Modifier.height(40.dp))

    Button(
        onClick = {
            scope.launch {
                val result = viewModel.validate()

                if (result.isValid) {

                    val uri = withContext(Dispatchers.IO) {
                        viewModel.writeExcel(context, doc)
                    }

                    onClear()

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

/*@RequiresApi(Build.VERSION_CODES.Q)
fun writeExcel(context: Context, doc: DocumentState): Uri? {
    val fileName = "Registro_Viajes.xlsx"
    val resolver = context.contentResolver

    //Buscar si el archivo ya existe en Downloads
    val existingUri: Uri? = findExistingFile(resolver, fileName)

    //Abrir workbook existente o crear uno nuevo
    val workbook: XSSFWorkbook = if (existingUri != null) {
        resolver.openInputStream(existingUri).use { inputStream ->
            XSSFWorkbook(inputStream)
        }
    } else {
        XSSFWorkbook()
    }

    //Buscar la hoja del mes actual o crearla
    val monthName = LocalDate.now().month.toString()
    val sheet: XSSFSheet = workbook.getSheet(monthName) ?: workbook.createSheet(monthName)

    //Agregar fila al final (no siempre en la 0)
    var newRowIndex = if (sheet.physicalNumberOfRows == 0) 0 else sheet.lastRowNum + 1

    //Creamos los estilos para la hoja
    val headerStyle: XSSFCellStyle = workbook.createCellStyle()
    //Bordes
    headerStyle.borderTop = BorderStyle.THIN
    headerStyle.borderRight = BorderStyle.THIN
    headerStyle.borderLeft = BorderStyle.THIN
    headerStyle.borderBottom = BorderStyle.THIN
    //Centramos textp
    headerStyle.alignment = HorizontalAlignment.CENTER
    headerStyle.verticalAlignment = VerticalAlignment.CENTER
    //Asignamos un color
    //Indicamos el color de la celda
    headerStyle.fillForegroundColor = IndexedColors.LIGHT_BLUE.index
    //Es necesario aplicar el pattern, ya que, de lo contrario no aplicara el color
    headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

    val bodyStyle: XSSFCellStyle = workbook.createCellStyle()
    //bordes
    bodyStyle.borderTop = BorderStyle.THIN
    bodyStyle.borderRight = BorderStyle.THIN
    bodyStyle.borderLeft = BorderStyle.THIN
    bodyStyle.borderBottom = BorderStyle.THIN

    if(newRowIndex == 0) {
        val row = sheet.createRow(newRowIndex)

        writeRow(0, row, "FECHA", headerStyle)
        writeRow(1, row, "ORIGEN", headerStyle)
        writeRow(2, row, "DESTINO", headerStyle)
        writeRow(3, row, "DISTANCIA", headerStyle)
        writeRow(4, row, "PRODUCTO", headerStyle)
        writeRow(5, row, "PESO", headerStyle)
        writeRow(6, row, "Nro. REMITO / CTG", headerStyle)
        writeRow(7, row, "TARIFA", headerStyle)
        writeRow(8, row, "MONTO", headerStyle)

        newRowIndex += 1
    }

    val row = sheet.createRow(newRowIndex)

    writeRow(0, row, doc.date, bodyStyle)
    writeRow(1, row, doc.origin, bodyStyle)
    writeRow(2, row, doc.destiny, bodyStyle)
    writeRow(3, row, doc.distance, bodyStyle)
    writeRow(4, row, doc.product, bodyStyle)
    writeRow(5, row, doc.weight, bodyStyle)
    writeRow(6, row, doc.idDocument, bodyStyle)
    writeRow(7, row, doc.rate, bodyStyle)
    writeRow(8, row, doc.amount, bodyStyle)

    //Escribir sobre el archivo existente o crear uno nuevo
    val targetUri: Uri? = if (existingUri != null) {
        resolver.openOutputStream(existingUri, "wt").use { outputStream ->
            workbook.write(outputStream)
        }
        existingUri
    } else {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val newUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        newUri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                for(i in 0..8){
                    sheet.setColumnWidth(i, 6000)
                }
                workbook.write(outputStream)
            }
        }
        newUri
    }

    workbook.close()
    return targetUri  // devuelve la uri donde se guardó
}

// Busca el archivo en MediaStore Downloads
@RequiresApi(Build.VERSION_CODES.Q)
private fun findExistingFile(resolver: ContentResolver, fileName: String): Uri? {
    val projection = arrayOf(MediaStore.Downloads._ID)
    val selection = "${MediaStore.Downloads.DISPLAY_NAME} = ?"
    val selectionArgs = arrayOf(fileName)

    resolver.query(
        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID))
            return ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)
        }
    }
    return null
}

private fun writeRow(columnIndex: Int, row: XSSFRow, value: String, style: XSSFCellStyle) {
    row.createCell(columnIndex).apply{
        setCellValue(value)
        cellStyle = style
    }
}*/