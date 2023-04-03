package com.github.bkmbigo.passionfruitdetector.domain.models

import androidx.compose.runtime.Immutable
import com.github.bkmbigo.passionfruitdetector.domain.utils.ImageMetadata
import org.tensorflow.lite.task.vision.detector.Detection
import kotlin.time.Duration

@Immutable
sealed class AnalysisResult() {
    object NoResult: AnalysisResult()
    data class EmptyResult(
        val inferenceTime: Duration,
        val imageMetadata: ImageMetadata,
    ): AnalysisResult()
    data class WithResult(
        val detections: List<Detection>,
        val inferenceTime: Duration,
        val imageMetadata: ImageMetadata
    ) : AnalysisResult()
}