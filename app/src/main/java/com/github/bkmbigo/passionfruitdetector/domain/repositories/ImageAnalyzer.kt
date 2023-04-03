package com.github.bkmbigo.passionfruitdetector.domain.repositories

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.github.bkmbigo.passionfruitdetector.domain.models.AnalysisResult
import com.github.bkmbigo.passionfruitdetector.domain.utils.ImageMetadata

interface ImageAnalyzer {
    fun analyzeImage(bitmap: Bitmap, imageRotation: Int, imageMetadata: ImageMetadata): AnalysisResult
}