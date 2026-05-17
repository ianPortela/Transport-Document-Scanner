package com.example.transportdocumentscanner.ui.Data.Export

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.example.transportdocumentscanner.ui.Presentation.State.DocumentState
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExportExcel {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun write(context: Context, doc: DocumentState): Uri? {
        val fileName = "Registro_Viajes.xlsx"
        val resolver = context.contentResolver
        val months = mapOf<String, String>(
            "JANUARY" to "Enero",
            "FEBRUARY" to "Febrero",
            "MARCH" to "Marzo",
            "APRIL" to "Abril",
            "MAY" to "Mayo",
            "JUNE" to "Junio",
            "JULY" to "Julio",
            "AUGUST" to "Agosto",
            "SEPTEMBER" to "Septiembre",
            "OCTOBER" to "Octuble",
            "NOVEMBER" to "Nobiembre",
            "DECEMBER" to "Diciembre",
        )



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

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val selectedDate = LocalDate.parse(doc.date, formatter)
        val monthName = months[selectedDate.month.toString()]

        //Buscar la hoja del mes actual o crearla
        val sheet: XSSFSheet = workbook.getSheet(monthName) ?: workbook.createSheet(monthName)

        //definimos el ancho de la columna
        for(i in 0..8){
            sheet.setColumnWidth(i, 6000)
        }

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
}