package com.github.bkmbigo.passionfruitdetector.domain.utils

import androidx.camera.core.ImageProxy

data class ImageMetadata(
    val imageHeight: Int,
    val imageWidth: Int,
    val isImageFlipped: Boolean
) {

    constructor(image: ImageProxy, isImageFlipped: Boolean): this(
        imageHeight = if(areDimensionsSwitched(image.imageInfo.rotationDegrees)) image.height else image.width,
        imageWidth = if(areDimensionsSwitched(image.imageInfo.rotationDegrees)) image.width else image.height,
        isImageFlipped = isImageFlipped
    )

    constructor(width: Int, height: Int, rotationDegrees: Int, isImageFlipped: Boolean): this(
        imageHeight = if(areDimensionsSwitched(rotationDegrees)) height else width,
        imageWidth = if(areDimensionsSwitched(rotationDegrees)) width else height,
        isImageFlipped = isImageFlipped
    )

    companion object {
        private fun areDimensionsSwitched(rotationDegrees: Int): Boolean {
            return rotationDegrees == 90 || rotationDegrees == 270
        }
    }
}