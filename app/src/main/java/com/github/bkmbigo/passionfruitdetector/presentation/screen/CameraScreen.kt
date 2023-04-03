package com.github.bkmbigo.passionfruitdetector.presentation.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.RectF
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.bkmbigo.passionfruitdetector.domain.models.AnalysisResult
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.AppSettings
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.DefaultCamera
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.FeedbackMode
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.InferenceDevice
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.PassionFruitModel
import com.github.bkmbigo.passionfruitdetector.domain.repositories.ImageAnalyzer
import com.github.bkmbigo.passionfruitdetector.domain.repositories.SettingsRepository
import com.github.bkmbigo.passionfruitdetector.domain.utils.ImageMetadata
import com.github.bkmbigo.passionfruitdetector.presentation.components.camera.CameraPreview
import com.github.bkmbigo.passionfruitdetector.presentation.components.ml.DetectorView
import com.github.bkmbigo.passionfruitdetector.presentation.theme.PassionFruitDetectorTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.detector.Detection
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

context(ImageAnalyzer, SettingsRepository)
        @Composable
fun CameraScreen() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // State Variables
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    val flashLightIsPresent by remember {
        derivedStateOf {
            camera?.cameraInfo?.hasFlashUnit() == true
        }
    }
    var flashLightIsOn by remember { mutableStateOf(false) }

    val detectionResult = remember { mutableStateOf<AnalysisResult>(AnalysisResult.NoResult) }
    val inferenceTime by remember {
        derivedStateOf {
            when (detectionResult.value) {
                is AnalysisResult.EmptyResult -> (detectionResult as AnalysisResult.EmptyResult).inferenceTime
                AnalysisResult.NoResult -> Duration.ZERO
                is AnalysisResult.WithResult -> (detectionResult as AnalysisResult.WithResult).inferenceTime
            }
        }
    }

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
        )

    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewCameraScreen() {
    PassionFruitDetectorTheme {
        Scaffold {
            with(
                object : ImageAnalyzer {
                    override fun analyzeImage(
                        bitmap: Bitmap,
                        imageRotation: Int,
                        imageMetadata: ImageMetadata
                    ): AnalysisResult = AnalysisResult.WithResult(
                        detections = listOf(
                            Detection.create(
                                RectF(120.10f, 30.1f, 132.10f, 60.0f),
                                listOf(
                                    Category("fruit_healthy", 0.7f)
                                )
                            )
                        ),
                        inferenceTime = 1300.toDuration(DurationUnit.MILLISECONDS),
                        imageMetadata = ImageMetadata(192, 192, 0, false)
                    )
                },
            ) {
                with(
                    object : SettingsRepository {
                        override fun observeSettings(): Flow<AppSettings> {
                            TODO("Not yet implemented")
                        }

                        override suspend fun getFeedbackMode(): FeedbackMode {
                            TODO("Not yet implemented")
                        }

                        override suspend fun setFeedbackMode(feedbackMode: FeedbackMode) {
                            TODO("Not yet implemented")
                        }

                        override suspend fun getShowConfidence(): Boolean {
                            TODO("Not yet implemented")
                        }

                        override suspend fun setShowConfidence(showConfidence: Boolean) {
                            TODO("Not yet implemented")
                        }

                        override suspend fun getDefaultCamera(): DefaultCamera {
                            TODO("Not yet implemented")
                        }

                        override suspend fun setDefaultCamera(defaultCamera: DefaultCamera) {
                            TODO("Not yet implemented")
                        }

                        override suspend fun getMaximumDisplayResults(): Int {
                            TODO("Not yet implemented")
                        }

                        override suspend fun setMaximumDisplayResults(maximumResults: Int) {
                            TODO("Not yet implemented")
                        }

                        override suspend fun getDisplayThreshold(): Float {
                            TODO("Not yet implemented")
                        }

                        override suspend fun setDisplayThreshold(displayThreshold: Float) {
                            TODO("Not yet implemented")
                        }

                        override suspend fun getPassionFruitModel(): PassionFruitModel {
                            TODO("Not yet implemented")
                        }

                        override suspend fun setPassionFruitModel(passionFruitModel: PassionFruitModel) {
                            TODO("Not yet implemented")
                        }

                        override suspend fun getInferenceDevice(): InferenceDevice {
                            TODO("Not yet implemented")
                        }

                        override suspend fun setInferenceDevice(inferenceDevice: InferenceDevice) {
                            TODO("Not yet implemented")
                        }

                    }
                ) {
                    CameraScreen()
                }
            }
        }
    }
}