package com.github.bkmbigo.passionfruitdetector.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import com.github.bkmbigo.passionfruitdetector.domain.models.AnalysisResult
import com.github.bkmbigo.passionfruitdetector.domain.repositories.ImageAnalyzer
import com.github.bkmbigo.passionfruitdetector.domain.utils.ImageMetadata
import kotlinx.datetime.Clock
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions

const val TAG = "PassionEfficientDet_lite0"

class PassionEfficientDet0(
    private val objectDetector: ObjectDetector
): ImageAnalyzer {

    init {
        Log.i(TAG, "init: PassionEfficientDet initialized")
    }

    override fun analyzeImage(bitmap: Bitmap, imageRotation: Int, imageMetadata: ImageMetadata): AnalysisResult {
        //Log.i(TAG, "analyzeImage: Image Received")
        val imageProcessor = ImageProcessor.Builder().apply { add(Rot90Op(-imageRotation / 90)) }.build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        //Log.i(TAG, "analyzeImage: Image Pre-processed")
        val startTime = Clock.System.now()
        val results = objectDetector.detect(tensorImage)
        val inferenceTime = Clock.System.now() - startTime
        Log.i(TAG, "analyzeImage: Detection Finished")

        return if(results.isEmpty()){
            Log.i(TAG, "analyzeImage: Empty Result in $inferenceTime milliseconds.")
            AnalysisResult.EmptyResult(
                inferenceTime,
                imageMetadata = imageMetadata
            )
        } else {
            Log.i(TAG, "analyzeImage: ${results.size} results found")
            results.forEachIndexed { i, res ->
                Log.i(TAG, "analyzeImage: Result $i label: ${res.categories[0].label} has confidence of ${res.categories[0].score}")
            }
            AnalysisResult.WithResult(
                detections = results,
                inferenceTime = inferenceTime,
                imageMetadata = imageMetadata
            )
        }
    }

    companion object {
        /**
         *
         * @throws java.io.IOException
         */
        fun buildPassionFruitDetector(
            context: Context,
            options: ObjectDetectorOptions
        ): PassionEfficientDet0 {
            val modelPath = "money_model.tflite"
            val objectDetector = ObjectDetector.createFromFileAndOptions(context, modelPath, options)
            Log.i(TAG, "buildPassionFruitDetector: Object Detector created")
            return PassionEfficientDet0(objectDetector)
        }
    }
}