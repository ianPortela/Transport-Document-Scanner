package com.example.transportdocumentscanner.ui.Presentation.Views

import android.Manifest
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.transportdocumentscanner.ui.Data.Ocr.cropImageToBoundingBox
import com.example.transportdocumentscanner.ui.Presentation.State.ScannerState
import com.example.transportdocumentscanner.ui.viewmodels.ScannerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    typeDoc: String,
    viewModel: ScannerViewModel = viewModel(),
    onScanComplete: (com.example.transportdocumentscanner.ui.Domain.Models.Document) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current

    val screenWidthDp = configuration.screenWidthDp.toFloat()
    val screenHeightDp = configuration.screenHeightDp.toFloat()

    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val uiState by viewModel.uiState.collectAsState()
    val currentFieldIndex by viewModel.currentFieldIndex.collectAsState()
    val currentField = viewModel.currentField

    val cameraController = remember { LifecycleCameraController(context) }

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is ScannerState.Success) {
            onScanComplete((uiState as ScannerState.Success).data)
        }
        if (uiState is ScannerState.Error) {
            Toast.makeText(context, (uiState as ScannerState.Error).message, Toast.LENGTH_SHORT).show()
            viewModel.resetError()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (permissionState.status.isGranted && uiState !is ScannerState.Processing) {
                FloatingActionButton(
                    onClick = {
                        takePictureAndProcess(
                            cameraController = cameraController,
                            context = context,
                            overlayWidthDp = currentField.width.value,
                            overlayHeightDp = currentField.height.value,
                            screenWidthDp = screenWidthDp,
                            screenHeightDp = screenHeightDp,
                            onImageCropped = { bitmap -> viewModel.processCapturedImage(bitmap, typeDoc) }
                        )
                    },
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Capturar ${currentField.title}",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (permissionState.status.isGranted) {
                cameraController.bindToLifecycle(lifecycleOwner)

                // 1. Cámara de fondo
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            controller = cameraController
                        }
                    }
                )

                // 2. Overlay oscurecido con el recorte dinámico
                ScannerOverlay(
                    frameWidth = currentField.width,
                    frameHeight = currentField.height
                )

                // 3. Indicador superior de qué campo capturar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Paso ${currentFieldIndex + 1}/8",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Enfoca: ${currentField.title}",
                        color = Color.Green,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                // 4. Loading state
                if (uiState is ScannerState.Processing) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
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
            .graphicsLayer(alpha = 0.99f) // Necesario para que BlendMode.Clear funcione correctamente
    ) {
        val cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Fondo oscuro
        drawRect(color = Color.Black.copy(alpha = 0.75f))

        val left = (canvasWidth - frameWidthPx) / 2f
        val top = (canvasHeight - frameHeightPx) / 2f
        val offset = Offset(left, top)
        val frameSize = Size(frameWidthPx, frameHeightPx)

        // Perfora el fondo oscuro (hace transparente el recuadro)
        drawRoundRect(
            color = Color.Transparent,
            topLeft = offset,
            size = frameSize,
            cornerRadius = cornerRadius,
            blendMode = BlendMode.Clear
        )

        // Borde blanco del recuadro
        drawRoundRect(
            color = Color.White,
            topLeft = offset,
            size = frameSize,
            cornerRadius = cornerRadius,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

// Función auxiliar para capturar
private fun takePictureAndProcess(
    cameraController: LifecycleCameraController,
    context: android.content.Context,
    overlayWidthDp: Float,
    overlayHeightDp: Float,
    screenWidthDp: Float,
    screenHeightDp: Float,
    onImageCropped: (android.graphics.Bitmap) -> Unit
) {
    val mainExecutor = ContextCompat.getMainExecutor(context)

    cameraController.takePicture(
        mainExecutor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                // Ejecutar el recorte
                val croppedBitmap = cropImageToBoundingBox(
                    imageProxy = image,
                    overlayWidthDp = overlayWidthDp,
                    overlayHeightDp = overlayHeightDp,
                    screenWidthDp = screenWidthDp,
                    screenHeightDp = screenHeightDp
                )

                croppedBitmap?.let { onImageCropped(it) }
            }
        }
    )
}