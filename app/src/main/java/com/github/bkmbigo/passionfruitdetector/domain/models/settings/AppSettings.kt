package com.github.bkmbigo.passionfruitdetector.domain.models.settings

data class AppSettings(
    val feedbackMode: FeedbackMode,
    val showConfidence: Boolean,
    val defaultCamera: DefaultCamera,
    val maximumDisplayResults: Int,
    val displayThreshold: Float,
    val passionFruitModel: PassionFruitModel,
    val inferenceDevice: InferenceDevice
)