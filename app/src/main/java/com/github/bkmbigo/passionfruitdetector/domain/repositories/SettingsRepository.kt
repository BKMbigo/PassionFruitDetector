package com.github.bkmbigo.passionfruitdetector.domain.repositories

import com.github.bkmbigo.passionfruitdetector.domain.models.settings.AppSettings
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.DefaultCamera
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.FeedbackMode
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.InferenceDevice
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.PassionFruitModel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>

    suspend fun getFeedbackMode(): FeedbackMode
    suspend fun setFeedbackMode(feedbackMode: FeedbackMode)

    suspend fun getShowConfidence(): Boolean
    suspend fun setShowConfidence(showConfidence: Boolean)

    suspend fun getDefaultCamera(): DefaultCamera
    suspend fun setDefaultCamera(defaultCamera: DefaultCamera)

    suspend fun getMaximumDisplayResults(): Int
    suspend fun setMaximumDisplayResults(maximumResults: Int)

    suspend fun getDisplayThreshold(): Float
    suspend fun setDisplayThreshold(displayThreshold: Float)

    suspend fun getPassionFruitModel(): PassionFruitModel
    suspend fun setPassionFruitModel(passionFruitModel: PassionFruitModel)

    suspend fun getInferenceDevice(): InferenceDevice
    suspend fun setInferenceDevice(inferenceDevice: InferenceDevice)
}