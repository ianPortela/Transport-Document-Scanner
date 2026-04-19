package com.example.transportdocumentscanner.ui.Views

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ManualLoadingScreen(modifier:Modifier = Modifier.fillMaxSize(), ) {

    var document by remember { mutableStateOf(Document()) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }  // 👈
    val scope = rememberCoroutineScope()

    Scaffold(  // 👈 Scaffold es necesario para que el Snackbar aparezca bien posicionado
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registro Manual de Datos",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(40.dp))
            InputFields(
                document = document,
                context = context,
                snackbarHostState = snackbarHostState,  // 👈
                scope = scope,                          // 👈
                onDocumentChange = { document = it },
                onClear = { document = Document() }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun InputFields(
    document: Document,
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onDocumentChange: (Document) -> Unit,
    onClear: () -> Unit
)
{
    Input("Fecha", document.date) {
        onDocumentChange(document.copy(date = it))
    }
    Spacer(modifier = Modifier.height(8.dp))

    Input("Origen de carga", document.origin) {
        onDocumentChange(document.copy(origin = it))
    }
    Spacer(modifier = Modifier.height(8.dp))

    Input("Destino de carga", document.destiny) {
        onDocumentChange(document.copy(destiny = it))
    }
    Spacer(modifier = Modifier.height(8.dp))

    Input("Distancia", document.distance) {
        onDocumentChange(document.copy(distance = it))
    }
    Spacer(modifier = Modifier.height(8.dp))

    Input("Producto", document.product) {
        onDocumentChange(document.copy(product = it))
    }
    Spacer(modifier = Modifier.height(8.dp))

    Input("Peso", document.weight) {
        onDocumentChange(document.copy(weight = it))
    }
    Spacer(modifier = Modifier.height(8.dp))

    Input("ID del documento", document.idDocument) {
        onDocumentChange(document.copy(idDocument = it))
    }
    Spacer(modifier = Modifier.height(8.dp))

    Input("Tarifa", document.rate) {
        onDocumentChange(document.copy(rate = it))
    }
    Spacer(modifier = Modifier.height(8.dp))

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
                    duration = SnackbarDuration.Long
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

    Spacer(modifier = Modifier.height(10.dp)) //revisar esta linea y ver si es necesaria, en inputField ya hay un spacer
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.width(400.dp)
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
fun writeExcel(context: Context, doc: Document): Uri? {
    val fileName = "Registro_Viajes.xlsx"
    val resolver = context.contentResolver

    // 1. Buscar si el archivo ya existe en Downloads
    val existingUri: Uri? = findExistingFile(resolver, fileName)

    // 2. Abrir workbook existente o crear uno nuevo
    val workbook: XSSFWorkbook = if (existingUri != null) {
        resolver.openInputStream(existingUri).use { inputStream ->
            XSSFWorkbook(inputStream)
        }
    } else {
        XSSFWorkbook()
    }

    // 3. Buscar la hoja del mes actual o crearla
    val monthName = LocalDate.now().month.toString()
    val sheet: XSSFSheet = workbook.getSheet(monthName) ?: workbook.createSheet(monthName)

    // 4. Agregar fila al final (no siempre en la 0)
    var newRowIndex = if (sheet.physicalNumberOfRows == 0) 0 else sheet.lastRowNum + 1

    if(newRowIndex == 0) {
        val row = sheet.createRow(newRowIndex)
        row.createCell(0).setCellValue("FECHA")
        row.createCell(1).setCellValue("ORIGEN")
        row.createCell(2).setCellValue("DESTINO")
        row.createCell(3).setCellValue("DISTANCIA")
        row.createCell(4).setCellValue("PRODUCTO")
        row.createCell(5).setCellValue("PESO")
        row.createCell(6).setCellValue("Nro. REMITO / CTG")
        row.createCell(7).setCellValue("TARIFA")
        row.createCell(8).setCellValue("MONTO")

        newRowIndex += 1
    }

    val row = sheet.createRow(newRowIndex)
    row.createCell(0).setCellValue(doc.date)
    row.createCell(1).setCellValue(doc.origin)
    row.createCell(2).setCellValue(doc.destiny)
    row.createCell(3).setCellValue(doc.distance)
    row.createCell(4).setCellValue(doc.product)
    row.createCell(5).setCellValue(doc.weight)
    row.createCell(6).setCellValue(doc.idDocument)
    row.createCell(7).setCellValue(doc.rate)
    row.createCell(8).setCellValue(doc.amount)

    // 5. Escribir sobre el archivo existente o crear uno nuevo
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
