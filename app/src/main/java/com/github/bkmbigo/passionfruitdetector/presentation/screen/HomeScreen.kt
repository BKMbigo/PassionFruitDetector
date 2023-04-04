package com.github.bkmbigo.passionfruitdetector.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.AppSettings
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.DefaultCamera
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.FeedbackMode
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.InferenceDevice
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.PassionFruitModel
import com.github.bkmbigo.passionfruitdetector.domain.repositories.HistoryRepository
import com.github.bkmbigo.passionfruitdetector.domain.repositories.ImageAnalyzer
import com.github.bkmbigo.passionfruitdetector.domain.repositories.SettingsRepository
import com.github.bkmbigo.passionfruitdetector.ml.PassionEfficientDet0
import com.github.bkmbigo.passionfruitdetector.presentation.components.bottombar.HomeBottomBar
import com.github.bkmbigo.passionfruitdetector.presentation.components.input.InputScreen
import com.github.bkmbigo.passionfruitdetector.presentation.navigation.BottomBarDestination
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.ObjectDetector

context(SettingsRepository, HistoryRepository)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current

    //State Variables
    val appSettings by observeSettings().collectAsState(
        initial = AppSettings(
            feedbackMode = FeedbackMode.DISPLAY_ONLY,
            showConfidence = true,
            defaultCamera = DefaultCamera.BACK,
            maximumDisplayResults = 4,
            displayThreshold = 0.7f,
            passionFruitModel = PassionFruitModel.EFFICIENT_DET_0,
            inferenceDevice = InferenceDevice.CPU
        )
    )
    val objectDetectorOptions by remember { derivedStateOf { prepareOptions(appSettings) } }

    var activeDestination by remember { mutableStateOf(BottomBarDestination.Camera) }
    var passionFruitDetector by remember { mutableStateOf<ImageAnalyzer?>(null) }


    DisposableEffect(appSettings, objectDetectorOptions) {
        passionFruitDetector = when (appSettings.passionFruitModel) {
            PassionFruitModel.EFFICIENT_DET_0 -> PassionEfficientDet0.buildPassionFruitDetector(
                context,
                objectDetectorOptions
            )
        }

        onDispose {
            passionFruitDetector = null
        }
    }

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                selectedScreen = activeDestination,
                onNavigationItemSelected = { destination ->
                    activeDestination = destination
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (activeDestination) {
                BottomBarDestination.Camera -> {
                    InputScreen(
                        modifier = Modifier.fillMaxSize(),
                        CameraScreen = {
                            if (passionFruitDetector != null) {
                                with(passionFruitDetector!!) {
                                    CameraScreen()
                                }
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HourglassEmpty,
                                        contentDescription = null,
                                        tint = Color.Green
                                    )
                                    Text(
                                        text = "Setting Up Model..."
                                    )
                                }
                            }
                        },
                        GalleryScreen = {
                            if (passionFruitDetector != null) {
                                with(passionFruitDetector!!) {
                                    GalleryScreen()
                                }
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HourglassEmpty,
                                        contentDescription = null,
                                        tint = Color.Green
                                    )
                                    Text(
                                        text = "Setting Up Model..."
                                    )
                                }
                            }
                        }
                    )
                }

                BottomBarDestination.Settings -> {
                    SettingsScreen(modifier = Modifier.fillMaxSize())
                }

                BottomBarDestination.HISTORY -> {
                    HistoryScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

private fun prepareOptions(appSettings: AppSettings): ObjectDetector.ObjectDetectorOptions {

    return ObjectDetector.ObjectDetectorOptions.builder().apply {
        setBaseOptions(
            BaseOptions.builder().apply {
                when (appSettings.inferenceDevice) {
                    InferenceDevice.NNAPI -> useNnapi()
                    InferenceDevice.GPU -> {
                        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                            useGpu()
                        } else {
                            //TODO: Add Error Handler
                        }
                    }

                    else -> {}
                }
            }.build()
        )
        setScoreThreshold(appSettings.displayThreshold)
        setMaxResults(appSettings.maximumDisplayResults)
    }.build()
}