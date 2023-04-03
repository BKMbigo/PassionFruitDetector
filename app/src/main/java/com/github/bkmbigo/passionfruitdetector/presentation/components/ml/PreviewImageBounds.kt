package com.github.bkmbigo.passionfruitdetector.presentation.components.ml

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
//        Scale type is always FILL_CENTER in this project
        ): PreviewImageBounds {
            val scale =
                maxOf(
                    viewWidth.toFloat() / sourceImageWidth,
                    viewHeight.toFloat() / sourceImageHeight
                )

            val previewImageWidth = sourceImageWidth * scale
            val previewImageHeight = sourceImageHeight * scale

            return PreviewImageBounds(
                x = viewWidth / 2 - previewImageWidth / 2,
                y = viewHeight / 2 - previewImageHeight / 2,
                width = previewImageWidth,
                height = previewImageHeight
            )
        }
    }
}
