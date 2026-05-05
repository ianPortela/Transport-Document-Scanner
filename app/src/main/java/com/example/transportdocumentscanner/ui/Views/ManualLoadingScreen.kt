package com.example.transportdocumentscanner.ui.Views

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transportdocumentscanner.ui.Models.Document
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.LocalDate
import androidx.compose.runtime.rememberCoroutineScope
import com.example.transportdocumentscanner.ui.Views.State.DocumentState
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
fun ManualLoadingScreen(modifier: Modifier = Modifier.fillMaxSize(), navigateToHome: () -> Unit, ) {

    var document by remember { mutableStateOf(DocumentState()) }
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
            InputFields(
                document = document,
                context = context,
                snackbarHostState = snackbarHostState,
                scope = scope,
                onDocumentChange = { document = it },
                onClear = { document = DocumentState() }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun InputFields(
    document: DocumentState,
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onDocumentChange: (DocumentState) -> Unit,
    onClear: () -> Unit
)
{
    Input("Fecha", document.date) {
        onDocumentChange(document.copy(date = it))
    }
    Spacer(modifier = Modifier.height(18.dp))

    Input("Origen de carga", document.origin) {
        onDocumentChange(document.copy(origin = it))
    }
    Spacer(modifier = Modifier.height(18.dp))

    Input("Destino de carga", document.destiny) {
        onDocumentChange(document.copy(destiny = it))
    }
    Spacer(modifier = Modifier.height(18.dp))

    Input("Distancia", document.distance) {
        onDocumentChange(document.copy(distance = it))
    }
    Spacer(modifier = Modifier.height(18.dp))

    Input("Producto", document.product) {
        onDocumentChange(document.copy(product = it))
    }
    Spacer(modifier = Modifier.height(18.dp))

    Input("Peso", document.weight) {
        onDocumentChange(document.copy(weight = it))
    }
    Spacer(modifier = Modifier.height(18.dp))

    Input("ID del documento", document.idDocument) {
        onDocumentChange(document.copy(idDocument = it))
    }
    Spacer(modifier = Modifier.height(18.dp))

    Input("Tarifa", document.rate) {
        onDocumentChange(document.copy(rate = it))
    }
    Spacer(modifier = Modifier.height(18.dp))

    Input("Importe", document.amount) {
        onDocumentChange(document.copy(amount = it))
    }
    Spacer(modifier = Modifier.height(40.dp))
    Button(
        onClick = {
            scope.launch {
                val uri = withContext(Dispatchers.IO) {
                    writeExcel(context, document)
                }

                onClear()

                // Mostrar Snackbar con acción
                val result = snackbarHostState.showSnackbar(
                    message = "Registro guardado",
                    actionLabel = "Abrir",
                    duration = SnackbarDuration.Short
                )

                // Si el usuario tocó "Abrir"
                if (result == SnackbarResult.ActionPerformed && uri != null) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(intent)
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

@Composable
fun Input(label:String, value:String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.width(360.dp)
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
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
                    sheet.setColumnWidth(i, 5800)
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
}