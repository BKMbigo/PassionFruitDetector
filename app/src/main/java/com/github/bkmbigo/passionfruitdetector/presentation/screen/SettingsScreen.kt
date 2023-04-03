package com.github.bkmbigo.passionfruitdetector.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.AppSettings
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.DefaultCamera
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.FeedbackMode
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.InferenceDevice
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.PassionFruitModel
import com.github.bkmbigo.passionfruitdetector.domain.repositories.SettingsRepository
import com.github.bkmbigo.passionfruitdetector.presentation.components.dialogs.SettingsDialog
import com.github.bkmbigo.passionfruitdetector.presentation.components.settings.SettingDataItem
import com.github.bkmbigo.passionfruitdetector.presentation.components.settings.SettingDialogOption
import com.github.bkmbigo.passionfruitdetector.presentation.components.settings.SettingsItem
import com.github.bkmbigo.passionfruitdetector.presentation.theme.PassionFruitDetectorTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

context(SettingsRepository)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {

    //State Variables
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
    var activeDialog by remember { mutableStateOf<SettingDialogOption?>(null) }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.padding(vertical = 4.dp))

            Text(
                text = "General Settings",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )

            SettingsItem(
                settingDataItem = SettingDataItem.SettingDataItemWithDescription.FeedbackModeSettingItem(
                    appSettings.feedbackMode
                ),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    activeDialog = SettingDialogOption.FEEDBACK_MODE
                }
            )

            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp))

            SettingsItem(
                settingDataItem = SettingDataItem.SettingDataItemWithDescription.DefaultCameraSettingItem(
                    appSettings.defaultCamera
                ),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    activeDialog = SettingDialogOption.DEFAULT_CAMERA
                }
            )

            Text(
                text = "Display Settings",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )

            SettingsItem(
                settingDataItem = SettingDataItem.ShowConfidenceSettingItem(appSettings.showConfidence),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    activeDialog = SettingDialogOption.SHOW_CONFIDENCE
                }
            )

            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp))

            SettingsItem(
                settingDataItem = SettingDataItem.DisplayThresholdSettingItem(appSettings.displayThreshold),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    activeDialog = SettingDialogOption.DISPLAY_THRESHOLD
                }
            )

            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp))

            SettingsItem(
                settingDataItem = SettingDataItem.MaximumDisplayResultSettingItem(appSettings.maximumDisplayResults),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    activeDialog = SettingDialogOption.MAXIMUM_DISPLAY_RESULTS
                }
            )

            Text(
                text = "Inference Settings",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )

            SettingsItem(
                settingDataItem = SettingDataItem.SettingDataItemWithDescription.ModelSettingItem(
                    appSettings.passionFruitModel
                ),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    activeDialog = SettingDialogOption.INFERENCE_MODEL
                }
            )

            SettingsItem(
                settingDataItem = SettingDataItem.SettingDataItemWithDescription.InferenceDeviceSettingItem(
                    appSettings.inferenceDevice
                ),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    activeDialog = SettingDialogOption.INFERENCE_DEVICE
                }
            )
        }

        when (activeDialog) {
            SettingDialogOption.FEEDBACK_MODE -> {
                SettingsDialog(
                    settingDataItem = SettingDataItem.SettingDataItemWithDescription.FeedbackModeSettingItem(
                        appSettings.feedbackMode
                    ),
                    onDismissRequest = {
                        activeDialog = null
                    },
                    onValueChanged = { feedbackSetting ->
                        setFeedbackMode(feedbackSetting.value)
                    }
                )
            }

            SettingDialogOption.SHOW_CONFIDENCE -> {
                SettingsDialog(
                    settingDataItem = SettingDataItem.ShowConfidenceSettingItem(appSettings.showConfidence),
                    onDismissRequest = {
                        activeDialog = null
                    },
                    onValueChanged = { showConfidenceSetting ->
                        setShowConfidence(showConfidenceSetting.value)
                    }
                )
            }

            SettingDialogOption.DEFAULT_CAMERA -> {
                SettingsDialog(
                    settingDataItem = SettingDataItem.SettingDataItemWithDescription.DefaultCameraSettingItem(
                        appSettings.defaultCamera
                    ),
                    onDismissRequest = {
                        activeDialog = null
                    },
                    onValueChanged = { defaultCameraSetting ->
                        setDefaultCamera(defaultCameraSetting.value)
                    }
                )
            }

            SettingDialogOption.MAXIMUM_DISPLAY_RESULTS -> {
                SettingsDialog(
                    settingDataItem = SettingDataItem.MaximumDisplayResultSettingItem(appSettings.maximumDisplayResults),
                    onDismissRequest = {
                        activeDialog = null
                    },
                    onValueChanged = { maximumDisplayResultSetting ->
                        setMaximumDisplayResults(maximumDisplayResultSetting.value)
                    }
                )
            }

            SettingDialogOption.DISPLAY_THRESHOLD -> {
                SettingsDialog(
                    settingDataItem = SettingDataItem.DisplayThresholdSettingItem(appSettings.displayThreshold),
                    onDismissRequest = {
                        activeDialog = null
                    },
                    onValueChanged = { displayThresholdSetting ->
                        setDisplayThreshold(displayThresholdSetting.value)
                    }
                )
            }

            SettingDialogOption.INFERENCE_MODEL -> {
                SettingsDialog(
                    settingDataItem = SettingDataItem.SettingDataItemWithDescription.ModelSettingItem(
                        appSettings.passionFruitModel
                    ),
                    onDismissRequest = {
                        activeDialog = null
                    },
                    onValueChanged = { inferenceDeviceSetting ->
                        setPassionFruitModel(inferenceDeviceSetting.value)
                    }
                )
            }

            SettingDialogOption.INFERENCE_DEVICE -> {
                SettingsDialog(
                    settingDataItem = SettingDataItem.SettingDataItemWithDescription.InferenceDeviceSettingItem(
                        appSettings.inferenceDevice
                    ),
                    onDismissRequest = {
                        activeDialog = null
                    },
                    onValueChanged = { inferenceDeviceSetting ->
                        setInferenceDevice(inferenceDeviceSetting.value)
                    }
                )
            }

            null -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewSettingsScreen() {
    PassionFruitDetectorTheme {
        Scaffold { pd ->
            with(
                object : SettingsRepository {
                    val settings = MutableStateFlow(
                        AppSettings(
                            feedbackMode = FeedbackMode.DISPLAY_ONLY,
                            showConfidence = true,
                            defaultCamera = DefaultCamera.BACK,
                            maximumDisplayResults = 1,
                            displayThreshold = 0.7f,
                            passionFruitModel = PassionFruitModel.EFFICIENT_DET_0,
                            inferenceDevice = InferenceDevice.CPU
                        )
                    )

                    override fun observeSettings(): Flow<AppSettings> = settings.map { it }

                    override suspend fun getFeedbackMode(): FeedbackMode {
                        TODO("Not yet implemented")
                    }

                    override suspend fun setFeedbackMode(feedbackMode: FeedbackMode) {
                        settings.value = settings.value.copy(feedbackMode = feedbackMode)
                    }

                    override suspend fun getShowConfidence(): Boolean {
                        TODO("Not yet implemented")
                    }

                    override suspend fun setShowConfidence(showConfidence: Boolean) {
                        settings.value = settings.value.copy(showConfidence = showConfidence)
                    }

                    override suspend fun getDefaultCamera(): DefaultCamera {
                        TODO("Not yet implemented")
                    }

                    override suspend fun setDefaultCamera(defaultCamera: DefaultCamera) {
                        settings.value = settings.value.copy(defaultCamera = defaultCamera)
                    }

                    override suspend fun getMaximumDisplayResults(): Int {
                        TODO("Not yet implemented")
                    }

                    override suspend fun setMaximumDisplayResults(maximumResults: Int) {
                        settings.value = settings.value.copy(maximumDisplayResults = maximumResults)
                    }

                    override suspend fun getDisplayThreshold(): Float {
                        TODO("Not yet implemented")
                    }

                    override suspend fun setDisplayThreshold(displayThreshold: Float) {
                        settings.value = settings.value.copy(displayThreshold = displayThreshold)
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
                        settings.value = settings.value.copy(inferenceDevice = inferenceDevice)
                    }

                }
            ) {
                Box(modifier = Modifier.padding(pd)) {
                    SettingsScreen()
                }
            }
        }
    }
}