package com.github.bkmbigo.passionfruitdetector.presentation.screen

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.github.bkmbigo.passionfruitdetector.domain.models.AnalysisResult
import com.github.bkmbigo.passionfruitdetector.domain.repositories.HistoryRepository
import com.github.bkmbigo.passionfruitdetector.domain.repositories.ImageAnalyzer
import com.github.bkmbigo.passionfruitdetector.domain.repositories.SettingsRepository
import com.github.bkmbigo.passionfruitdetector.domain.utils.ImageMetadata
import com.github.bkmbigo.passionfruitdetector.presentation.components.dialogs.SaveHistoryDialog
import com.github.bkmbigo.passionfruitdetector.presentation.components.ml.DetectorView
import kotlinx.coroutines.launch

context(ImageAnalyzer, HistoryRepository, SettingsRepository)
        @OptIn(ExperimentalGlideComposeApi::class)
        @Composable
fun GalleryScreen(
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Activity

    var state by remember { mutableStateOf(GalleryScreenState.AWAITING_USER_SELECTION) }
    var loading by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val detections = remember { mutableStateOf<AnalysisResult>(AnalysisResult.NoResult) }


    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            loading = true
            uri?.let {
                imageUri = uri
            }
            loading = false
            detections.value = AnalysisResult.NoResult
            state = GalleryScreenState.READY_FOR_ANALYSIS
        }
    )

    BoxWithConstraints(
        modifier = modifier
    ) {
        imageUri?.let {
            state.name // recompose on state change
            GlideImage(
                model = it,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        DetectorView(
            resultState = detections,
            constraints = constraints,
            isImageFlipped = false,
            modifier = Modifier.fillMaxSize(),
            isGalleryView = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (state == GalleryScreenState.AWAITING_USER_SELECTION) {
                Button(
                    onClick = {
                        imagePicker.launch("image/*")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.BrowseGallery,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = "Add picture from gallery",
                    )
                }
            }


            if (state == GalleryScreenState.READY_FOR_ANALYSIS) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            detections.value = imageUri?.let { uri ->
                                getBitmapFromUri(uri, context.contentResolver)?.let { bitmap ->
                                    analyzeImage(
                                        bitmap,
                                        0,
                                        ImageMetadata(bitmap.height, bitmap.width, false)
                                    )
                                }
                            } ?: AnalysisResult.NoResult
                            state = GalleryScreenState.AWAITING_USER_SELECTION
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ImageSearch,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = "Detect Image",
                    )
                }
            }
        }

        SaveHistoryDialog(
            analysisState = detections,
            saveAction = { detectionHistories ->
                coroutineScope.launch {
                    detectionHistories.map { detectionHistory -> insertHistoryItem(detectionHistory) }
                }
            }
        )
    }
}

private enum class GalleryScreenState {
    AWAITING_USER_SELECTION,
    READY_FOR_ANALYSIS,
}

private fun getBitmapFromUri(imageUri: Uri, contentResolver: ContentResolver): Bitmap? {
    try {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        } else {
            val source = ImageDecoder.createSource(contentResolver, imageUri)
            val bmp = ImageDecoder.decodeBitmap(source)

            bmp.copy(Bitmap.Config.ARGB_8888, false)
        }
    } catch (e: Exception) {
        Log.e("ImageUtils", "getBitmapFromUri: ", e)
    }
    return null
}