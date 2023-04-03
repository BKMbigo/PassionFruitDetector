package com.github.bkmbigo.passionfruitdetector.presentation.components.settings

import androidx.annotation.StringRes
import com.github.bkmbigo.passionfruitdetector.R
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.DefaultCamera
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.FeedbackMode
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.InferenceDevice
import com.github.bkmbigo.passionfruitdetector.domain.models.settings.PassionFruitModel

sealed class SettingDataItem<T>(
    @StringRes open val label: Int,
    open val value: T,
    open val options: List<SettingOption<T>>
) {
    /**
     * @throws IllegalStateException
     */
    fun getOptionWithValue(): SettingOption<T> =
        options.associateBy { it.value }[value] ?: throw IllegalStateException("Value not found in options list")

    class ShowConfidenceSettingItem(override val value: Boolean): SettingDataItem<Boolean>(
        label = R.string.label_show_confidence,
        value = value,
        options = listOf(
            SettingOption.StringResource(
                resId = R.string.label_yes,
                value = true
            ),
            SettingOption.StringResource(
                resId = R.string.label_no,
                value = false
            )
        )
    )

    class MaximumDisplayResultSettingItem(override val value: Int): SettingDataItem<Int>(
        label = R.string.label_maximum_display_results,
        value = value,
        options = listOf(
            SettingOption.Value(1),
            SettingOption.Value(2),
            SettingOption.Value(3),
            SettingOption.Value(4),
            SettingOption.Value(5),
            SettingOption.Value(6),
            SettingOption.Value(7),
            SettingOption.Value(8),
        )
    )

    class DisplayThresholdSettingItem(override val value: Float): SettingDataItem<Float>(
        label = R.string.label_display_threshold,
        value = value,
        options = listOf(
            SettingOption.Value(0.3f),
            SettingOption.Value(0.4f),
            SettingOption.Value(0.5f),
            SettingOption.Value(0.6f),
            SettingOption.Value(0.7f),
            SettingOption.Value(0.8f),
            SettingOption.Value(0.9f)
        )
    )

    sealed class SettingDataItemWithDescription<T> private constructor(
        @StringRes override val label: Int,
        override val value: T,
        override val options: List<SettingOption<T>>,
        @StringRes val description: Int
    ): SettingDataItem<T>(label, value, options) {
        /**
         * Feedback Setting: Used to set the mode of [FeedbackMode]. Can either be [FeedbackMode.DISPLAY_ONLY], [FeedbackMode.DISPLAY_AND_SPEECH], [FeedbackMode.DISPLAY_AND_VIBRATIONS] or [FeedbackMode.DISPLAY_SPEECH_AND_VIBRATIONS]
         */
        class FeedbackModeSettingItem(override val value: FeedbackMode): SettingDataItemWithDescription<FeedbackMode>(
            label = R.string.label_feedback_mode,
            value = value,
            options = listOf(
                SettingOption.StringResource(
                    resId = R.string.label_display_only,
                    value = FeedbackMode.DISPLAY_ONLY
                ),
//                SettingOption.StringResource(
//                    resId = R.string.label_display_and_speech,
//                    value = FeedbackMode.DISPLAY_AND_SPEECH
//                ),
//                SettingOption.StringResource(
//                    resId = R.string.label_display_and_vibration,
//                    value = FeedbackMode.DISPLAY_AND_VIBRATIONS
//                ),
//                SettingOption.StringResource(
//                    resId = R.string.label_display_speech_and_vibration,
//                    value = FeedbackMode.DISPLAY_SPEECH_AND_VIBRATIONS
//                ),
            ),
            description = R.string.desc_feedback_mode
        )

        class DefaultCameraSettingItem(override val value: DefaultCamera): SettingDataItemWithDescription<DefaultCamera>(
            label = R.string.label_default_camera,
            value = value,
            options = listOf(
                SettingOption.StringResource(
                    resId = R.string.label_back_camera,
                    value = DefaultCamera.BACK
                ),
                SettingOption.StringResource(
                    resId = R.string.label_front_camera,
                    value = DefaultCamera.FRONT
                )
            ),
            description = R.string.desc_default_camera
        )

        class InferenceDeviceSettingItem(override val value: InferenceDevice): SettingDataItemWithDescription<InferenceDevice>(
            label = R.string.label_inference_device,
            value = value,
            options = listOf(
                SettingOption.StringResource(
                    R.string.label_cpu,
                    InferenceDevice.CPU
                ),
                SettingOption.StringResource(
                    R.string.label_gpu,
                    InferenceDevice.GPU
                ),
                SettingOption.StringResource(
                    R.string.label_nnapi,
                    InferenceDevice.NNAPI
                ),
            ),
            description = R.string.desc_inference_device
        )

        class ModelSettingItem(override val value: PassionFruitModel): SettingDataItemWithDescription<PassionFruitModel>(
            label = R.string.label_model,
            value = value,
            options = PassionFruitModel.values().map {
                 SettingOption.ValueWithName(it.title, it)
            },
            description = R.string.desc_inference_model
        )
    }
}

sealed class SettingOption<T>(open val value: T) {
    data class StringResource<T>(
        @StringRes val resId: Int,
        override val value: T
    ): SettingOption<T>(value)

    data class Value<T>(
        override val value: T
    ): SettingOption<T>(value)

    data class ValueWithName<T>(
        val name: String,
        override val value: T,
    ): SettingOption<T>(value)
}
