package com.example.transportdocumentscanner.ui.Presentation.Views

import android.Manifest
import android.view.ViewGroup
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(typeDoc: String) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {
        LifecycleCameraController(context)
    }

    // Calculamos el tamaño del recuadro dependiendo del campo a escanear.
    // Esto evita hardcodear y te permite escalar la app fácilmente.
    val (frameWidth, frameHeight) = remember(typeDoc) {
        when (typeDoc.lowercase()) {
            "fecha" -> Pair(150.dp, 50.dp)
            "ctg" -> Pair(250.dp, 60.dp)
            "patente" -> Pair(200.dp, 70.dp)
            "peso" -> Pair(180.dp, 60.dp)
            else -> Pair(250.dp, 100.dp) // Tamaño por defecto
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.Center, // Centramos el botón abajo
        floatingActionButton = {
            if (permissionState.status.isGranted) {
                FloatingActionButton(
                    onClick = { takePicture(cameraController) },
                    shape = CircleShape,
                    // Margen inferior para que no quede pegado al borde del teléfono
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Capturar documento",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        // Usamos Box para superponer el Overlay encima de la cámara
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (permissionState.status.isGranted) {
                cameraController.bindToLifecycle(lifecycleOwner)

                // 1. Capa de la Cámara
                ScannerContent(
                    modifier = Modifier.fillMaxSize(), // No usamos paddingValues para que la cámara ocupe todo el fondo
                    cameraController = cameraController
                )

                // 2. Capa del Overlay (sombreado + recuadro de recorte)
                ScannerOverlay(
                    frameWidth = frameWidth,
                    frameHeight = frameHeight
                )
            }
        }
    }
}

@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    frameWidth: Dp,
    frameHeight: Dp
) {
    val density = LocalDensity.current
    val frameWidthPx = with(density) { frameWidth.toPx() }
    val frameHeightPx = with(density) { frameHeight.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            // graphicsLayer(alpha = 0.99f) es vital para que BlendMode.Clear funcione
            // sobre esta capa de Compose sin borrar la cámara que está por debajo.
            .graphicsLayer(alpha = 0.99f)
    ) {
        val cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
        val canvasWidth = size.width
        val canvasHeight = size.height

        // 1. Dibujar el fondo semitransparente oscuro
        drawRect(color = Color.Black.copy(alpha = 0.6f))

        // 2. Calcular las coordenadas del centro
        val left = (canvasWidth - frameWidthPx) / 2f
        val top = (canvasHeight - frameHeightPx) / 2f
        val offset = Offset(left, top)
        val frameSize = Size(frameWidthPx, frameHeightPx)

        // 3. "Perforar" el recuadro transparente en el centro
        drawRoundRect(
            color = Color.Transparent,
            topLeft = offset,
            size = frameSize,
            cornerRadius = cornerRadius,
            blendMode = BlendMode.Clear
        )

        // 4. Dibujar el borde blanco del recuadro
        drawRoundRect(
            color = Color.White,
            topLeft = offset,
            size = frameSize,
            cornerRadius = cornerRadius,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Composable
fun ScannerContent(modifier: Modifier = Modifier, cameraController: LifecycleCameraController) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Usamos FIT_CENTER o FILL_CENTER. FILL_CENTER es estándar para escáneres.
                scaleType = PreviewView.ScaleType.FILL_CENTER
                controller = cameraController
            }
        }
    )
}

fun takePicture(cameraController: LifecycleCameraController) {
    // Aquí irá la lógica de captura para tu OCR.
}