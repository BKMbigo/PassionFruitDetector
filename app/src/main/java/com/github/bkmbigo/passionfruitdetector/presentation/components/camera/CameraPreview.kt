package com.github.bkmbigo.passionfruitdetector.presentation.components.camera

import android.util.Log
import android.view.Surface.ROTATION_90
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.bkmbigo.passionfruitdetector.presentation.components.camera.utils.executor
import com.github.bkmbigo.passionfruitdetector.presentation.components.camera.utils.getCameraProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

const val TAG = "Camera Preview"

/**
 * Camera Preview
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    analyzeImage: (ImageProxy) -> Unit = {},
    onCameraReady: (Camera) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Log.i(TAG, "CameraPreview: recomposed")

    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    if (!cameraPermissionState.status.isGranted) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color.Red
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(text = "The application does not have camera permission")
        }
    } else {
        AndroidView(
            modifier = modifier,
            update = { previewView ->
                val previewUseCase = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                Log.i(TAG, "CameraPreview: Camera Preview updated")

                val analysisUseCase = ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    //.setTargetRotation(previewView.display.rotation)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()
                    .apply {
                        setAnalyzer(context.executor) { image ->
                            analyzeImage(image)
                        }
                    }

                coroutineScope.launch {
                    val cameraProvider = context.getCameraProvider()
                    try {
                        cameraProvider.unbindAll()
                        onCameraReady(
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                previewUseCase,
                                analysisUseCase
                            )
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, "Exception Encountered", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            factory = { previewContext ->
                PreviewView(previewContext).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )
    }
}