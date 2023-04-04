package com.github.bkmbigo.passionfruitdetector.presentation.components.ml

import androidx.camera.view.PreviewView

data class PreviewImageBounds(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
) {
    fun toViewX(imageX: Float) = imageX * width + x
    fun toViewY(imageY: Float) = imageY * height + y

    companion object {
        fun getPreviewImageBounds(
            sourceImageWidth: Int,
            sourceImageHeight: Int,
            viewWidth: Int,
            viewHeight: Int,
            scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER
        ): PreviewImageBounds {
            val scale = if (scaleType == PreviewView.ScaleType.FILL_START ||
                scaleType == PreviewView.ScaleType.FILL_END ||
                scaleType == PreviewView.ScaleType.FILL_CENTER
            ) {
                maxOf(viewWidth.toFloat() / sourceImageWidth, viewHeight.toFloat() / sourceImageHeight)
            } else {
                minOf(viewWidth.toFloat() / sourceImageWidth, viewHeight.toFloat() / sourceImageHeight)
            }
            val previewImageWidth = sourceImageWidth * scale
            val previewImageHeight = sourceImageHeight * scale
            return when (scaleType) {
                PreviewView.ScaleType.FILL_START, PreviewView.ScaleType.FIT_START -> {
                    PreviewImageBounds(0f, 0f, previewImageWidth, previewImageHeight)
                }
                PreviewView.ScaleType.FILL_END, PreviewView.ScaleType.FIT_END -> {
                    PreviewImageBounds(
                        viewWidth - previewImageWidth, viewHeight - previewImageHeight,
                        previewImageWidth, previewImageHeight
                    )
                }
                else -> {
                    PreviewImageBounds(
                        viewWidth / 2 - previewImageWidth / 2, viewHeight / 2 - previewImageHeight / 2,
                        previewImageWidth, previewImageHeight
                    )
                }
            }
        }
    }
}
