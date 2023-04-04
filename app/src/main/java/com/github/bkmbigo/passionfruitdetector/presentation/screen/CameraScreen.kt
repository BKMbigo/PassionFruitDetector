package com.github.bkmbigo.passionfruitdetector.presentation.screen

import android.graphics.Bitmap
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.bkmbigo.passionfruitdetector.domain.models.AnalysisResult
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.AppSettings
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.DefaultCamera
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.FeedbackMode
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.InferenceDevice
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.PassionFruitModel
import com.github.bkmbigo.passionfruitdetector.domain.repositories.HistoryRepository
import com.github.bkmbigo.passionfruitdetector.domain.repositories.ImageAnalyzer
import com.github.bkmbigo.passionfruitdetector.domain.repositories.SettingsRepository
import com.github.bkmbigo.passionfruitdetector.domain.utils.ImageMetadata
import com.github.bkmbigo.passionfruitdetector.presentation.components.camera.CameraPreview
import com.github.bkmbigo.passionfruitdetector.presentation.components.dialogs.SaveHistoryDialog
import com.github.bkmbigo.passionfruitdetector.presentation.components.ml.DetectorView
import kotlinx.coroutines.launch
import kotlin.time.Duration

context(ImageAnalyzer, HistoryRepository, SettingsRepository)
        @Composable
fun CameraScreen() {
    val coroutineScope = rememberCoroutineScope()

    // State Variables
    val appSettings by observeSettings().collectAsState(
        initial = AppSettings(
            feedbackMode = FeedbackMode.DISPLAY_ONLY,
            showConfidence = true,
            defaultCamera = DefaultCamera.BACK,
            maximumDisplayResults = 1,
            displayThreshold = 0.7f,
            passionFruitModel = PassionFruitModel.EFFICIENT_DET_0,
            inferenceDevice = InferenceDevice.CPU
        )
    )

    var showSaveDialog by remember { mutableStateOf(false) }

    var cameraSelector by remember(appSettings) {
        mutableStateOf(
            if (appSettings.defaultCamera == DefaultCamera.BACK) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else CameraSelector.DEFAULT_FRONT_CAMERA
        )
    }
    var camera by remember { mutableStateOf<Camera?>(null) }
    val flashLightIsPresent by remember {
        derivedStateOf {
            camera?.cameraInfo?.hasFlashUnit() == true
        }
    }
    var flashLightIsOn by remember { mutableStateOf(false) }

    val detectionResult = remember { mutableStateOf<AnalysisResult>(AnalysisResult.NoResult) }
//    val inferenceTime by remember {
//        derivedStateOf {
//            when (detectionResult.value) {
//                is AnalysisResult.EmptyResult -> (detectionResult.value as AnalysisResult.EmptyResult).inferenceTime
//                AnalysisResult.NoResult -> Duration.ZERO
//                is AnalysisResult.WithResult -> (detectionResult.value as AnalysisResult.WithResult).inferenceTime
//            }
//        }
//    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            cameraSelector = cameraSelector,
            analyzeImage = { imageProxy ->
                coroutineScope.launch {
                    detectionResult.value = this@ImageAnalyzer.analyzeImage(
                        Bitmap.createBitmap(
                            imageProxy.width,
                            imageProxy.height,
                            Bitmap.Config.ARGB_8888
                        ).apply {
                            copyPixelsFromBuffer(imageProxy.planes[0].buffer)
                        },
                        imageRotation = imageProxy.imageInfo.rotationDegrees,
                        imageMetadata = ImageMetadata(
                            imageProxy,
                            cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                        ),
                    )
                    imageProxy.close()
                }
            },
            onCameraReady = { cam ->
                camera = cam
                flashLightIsOn = false
            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ElevatedButton(
                onClick = {
                    cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                        CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = null
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Change Camera"
                )
            }

            ElevatedButton(
                onClick = {
                    if (flashLightIsPresent) {
                        camera?.cameraControl?.enableTorch(!flashLightIsOn)
                        flashLightIsOn = !flashLightIsOn
                    }
                },
                enabled = flashLightIsPresent
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = null
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (flashLightIsOn)
                        "Turn Off Flashlight" else "Turn On Flashlight"
                )
            }
        }

//        InferenceTimeGraph(
//            inferenceTime = inferenceTime,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(100.dp)
//                .padding(bottom = 4.dp)
//                .align(Alignment.BottomCenter)
//        )

        DetectorView(
            resultState = detectionResult,
            constraints = constraints,
            isImageFlipped = cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA,
            modifier = Modifier.fillMaxSize(),
            isGalleryView = false
        )

        SaveHistoryDialog(
            analysisState = detectionResult,
            saveAction = { detectionHistories ->
                coroutineScope.launch {
                    detectionHistories.map { detectionHistory -> insertHistoryItem(detectionHistory) }
                }
            }
        )
    }
}