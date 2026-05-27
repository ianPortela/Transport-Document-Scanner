package com.example.transportdocumentscanner.ui.Data.Ocr

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy

fun cropImageToBoundingBox(
    imageProxy: ImageProxy,
    overlayWidthDp: Float,
    overlayHeightDp: Float,
    screenWidthDp: Float,
    screenHeightDp: Float
): Bitmap? {
    try {
        val bitmap = imageProxy.toBitmap()

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()
        val matrix = Matrix().apply { postRotate(rotationDegrees) }
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val widthRatio = overlayWidthDp / screenWidthDp
        val heightRatio = overlayHeightDp / screenHeightDp

        val cropWidth = (rotatedBitmap.width * widthRatio).toInt()
        val cropHeight = (rotatedBitmap.height * heightRatio).toInt()

        val x = (rotatedBitmap.width - cropWidth) / 2
        val y = (rotatedBitmap.height - cropHeight) / 2

        return Bitmap.createBitmap(rotatedBitmap, x, y, cropWidth, cropHeight)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        imageProxy.close()
    }
}