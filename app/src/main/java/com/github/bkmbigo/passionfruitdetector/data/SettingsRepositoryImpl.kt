package com.github.bkmbigo.passionfruitdetector.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.github.bkmbigo.passionfruitdetector.data.datastore.SettingsKeys
import com.github.bkmbigo.passionfruitdetector.data.datastore.settingGetValue
import com.github.bkmbigo.passionfruitdetector.data.datastore.settingSetValue
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.AppSettings
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.DefaultCamera
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.FeedbackMode
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.InferenceDevice
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.PassionFruitModel
import com.github.bkmbigo.passionfruitdetector.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

context(DataStore<Preferences>)
class SettingsRepositoryImpl : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> =
        data.map { preferences ->
            AppSettings(
                feedbackMode = preferences[SettingsKeys.FeedbackModeKey]?.settingGetValue<FeedbackMode>()
                    ?: FeedbackMode.DISPLAY_ONLY,
                showConfidence = preferences[SettingsKeys.ShowConfidenceKey] ?: true,
                defaultCamera = preferences[SettingsKeys.DefaultCameraKey]?.settingGetValue<DefaultCamera>()
                    ?: DefaultCamera.BACK,
                maximumDisplayResults = preferences[SettingsKeys.MaximumDisplayResultsKey] ?: 5,
                displayThreshold = preferences[SettingsKeys.DisplayThresholdKey] ?: 0.7f,
                passionFruitModel = preferences[SettingsKeys.PassionFruitModelKey]?.settingGetValue<PassionFruitModel>()
                    ?: PassionFruitModel.EFFICIENT_DET_0,
                inferenceDevice = preferences[SettingsKeys.InferenceDeviceKey]?.settingGetValue<InferenceDevice>()
                    ?: InferenceDevice.CPU
            )
        }


    override suspend fun getFeedbackMode(): FeedbackMode =
        data.map { it[SettingsKeys.FeedbackModeKey] }.single()?.settingGetValue<FeedbackMode>()
            ?: FeedbackMode.DISPLAY_ONLY


    override suspend fun setFeedbackMode(feedbackMode: FeedbackMode) {
        edit { settings ->
            settings[SettingsKeys.FeedbackModeKey] = feedbackMode.settingSetValue()
        }
    }

    override suspend fun getShowConfidence(): Boolean =
        data.map { it[SettingsKeys.ShowConfidenceKey] }.single() ?: true

    override suspend fun setShowConfidence(showConfidence: Boolean) {
        edit { settings ->
            settings[SettingsKeys.ShowConfidenceKey] = showConfidence
        }
    }

    override suspend fun getDefaultCamera(): DefaultCamera =
        data.map { it[SettingsKeys.DefaultCameraKey] }.single()?.settingGetValue<DefaultCamera>()
            ?: DefaultCamera.BACK

    override suspend fun setDefaultCamera(defaultCamera: DefaultCamera) {
        edit { settings ->
            settings[SettingsKeys.DefaultCameraKey] = defaultCamera.settingSetValue()
        }
    }

    override suspend fun getMaximumDisplayResults(): Int =
        data.map { it[SettingsKeys.MaximumDisplayResultsKey] }.single() ?: 1

    override suspend fun setMaximumDisplayResults(maximumResults: Int) {
        edit { settings ->
            settings[SettingsKeys.MaximumDisplayResultsKey] = maximumResults
        }
    }

    override suspend fun getDisplayThreshold(): Float =
        data.map { it[SettingsKeys.DisplayThresholdKey] }.single() ?: 0.7f

    override suspend fun setDisplayThreshold(displayThreshold: Float) {
        edit { settings ->
            settings[SettingsKeys.DisplayThresholdKey] = displayThreshold
        }
    }

    override suspend fun getPassionFruitModel(): PassionFruitModel =
        data.map { it[SettingsKeys.InferenceDeviceKey] }.single()
            ?.settingGetValue<PassionFruitModel>() ?: PassionFruitModel.EFFICIENT_DET_0


    override suspend fun setPassionFruitModel(passionFruitModel: PassionFruitModel) {
        edit { settings ->
            settings[SettingsKeys.PassionFruitModelKey] = passionFruitModel.settingSetValue()
        }
    }

    override suspend fun getInferenceDevice(): InferenceDevice =
        data.map { it[SettingsKeys.InferenceDeviceKey] }.single()
            ?.settingGetValue<InferenceDevice>() ?: InferenceDevice.CPU

    override suspend fun setInferenceDevice(inferenceDevice: InferenceDevice) {
        edit { settings ->
            settings[SettingsKeys.InferenceDeviceKey] = inferenceDevice.settingSetValue()
        }
    }
}