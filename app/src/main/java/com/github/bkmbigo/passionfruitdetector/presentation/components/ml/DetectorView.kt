package com.github.bkmbigo.passionfruitdetector.presentation.components.ml

import android.graphics.RectF
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.bkmbigo.passionfruitdetector.domain.models.AnalysisResult
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.AppSettings
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.DefaultCamera
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.FeedbackMode
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.InferenceDevice
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.PassionFruitModel
import com.github.bkmbigo.passionfruitdetector.domain.repositories.SettingsRepository
import com.github.bkmbigo.passionfruitdetector.domain.utils.ImageMetadata
import org.tensorflow.lite.task.vision.detector.Detection

context(SettingsRepository)
        @Composable
fun DetectorView(
    resultState: State<AnalysisResult>,
    constraints: Constraints,
    isImageFlipped: Boolean,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Blue,
    lineWidth: Dp = 1.dp,
) {

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

    val bounds by remember {
        derivedStateOf {
            val imageMetadata = if (resultState.value is AnalysisResult.WithResult) (resultState.value as AnalysisResult.WithResult).imageMetadata else null

            PreviewImageBounds.getPreviewImageBounds(
                sourceImageWidth = imageMetadata?.imageWidth ?: 1,
                sourceImageHeight = imageMetadata?.imageHeight ?: 1,
                viewWidth = constraints.maxWidth,
                viewHeight = constraints.maxHeight
            )
        }
    }

    val result =
        if (resultState.value is AnalysisResult.WithResult) (resultState.value as AnalysisResult.WithResult).detections.filter { it.categories[0].score > appSettings.displayThreshold } else emptyList()

    if(resultState.value is AnalysisResult.WithResult){
        DetectionPanel(
            detections = result,
            bounds = bounds,
            imageMetadata = (resultState.value as AnalysisResult.WithResult).imageMetadata,
            isImageFlipped = isImageFlipped,
            modifier = modifier,
            lineColor = lineColor
        )
    }

}

@Composable
private fun DetectionPanel(
    detections: List<Detection>,
    bounds: PreviewImageBounds,
    imageMetadata: ImageMetadata,
    isImageFlipped: Boolean,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Blue,
    lineWidth: Dp = 1.dp,
) {


    Canvas(
        modifier = modifier
    ) {
        if (detections.isNotEmpty()) {
            detections.forEach { detection ->
                val rectangle = RectF(
                    bounds.toViewX(if(isImageFlipped) 1 -  detection.boundingBox.left / imageMetadata.imageHeight else detection.boundingBox.left / imageMetadata.imageHeight),
                    bounds.toViewY(detection.boundingBox.top / imageMetadata.imageWidth),
                    bounds.toViewX(if(isImageFlipped) 1 -  detection.boundingBox.right / imageMetadata.imageHeight else detection.boundingBox.right / imageMetadata.imageHeight),
                    bounds.toViewY(detection.boundingBox.bottom / imageMetadata.imageWidth),
                )

                drawRect(
                    color = lineColor,
                    topLeft = Offset(rectangle.left, rectangle.top),
                    size = Size(
                        rectangle.width(),
                        rectangle.height()
                    ),
                    style = Stroke(),
                    alpha = detection.categories[0].score
                )

                Log.i("Detector View", "Detection: Original Rectangle(left: ${detection.boundingBox.left}, top: ${detection.boundingBox.top}, right: ${detection.boundingBox.right}, bottom: ${detection.boundingBox.bottom} )")
                Log.i("Detector View", "Detection: Drawing Rectangle(left: ${rectangle.left}, top: ${rectangle.top}, right: ${rectangle.right}, bottom: ${rectangle.bottom} )")
            }
        }
    }
}