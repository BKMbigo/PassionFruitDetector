package com.github.bkmbigo.passionfruitdetector.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.PassionFruitModel

object SettingsKeys{
    val FeedbackModeKey = intPreferencesKey("feedback_mode")
    val ShowConfidenceKey = booleanPreferencesKey("show_confidence")
    val DefaultCameraKey = intPreferencesKey("default_camera")
    val MaximumDisplayResultsKey = intPreferencesKey("maximum_display_result")
    val DisplayThresholdKey = floatPreferencesKey("display_threshold")
    val PassionFruitModelKey = intPreferencesKey("passion_fruit_model")
    val InferenceDeviceKey = intPreferencesKey("inference_device")
}