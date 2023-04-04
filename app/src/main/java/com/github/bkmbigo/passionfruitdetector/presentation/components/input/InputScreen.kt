package com.github.bkmbigo.passionfruitdetector.presentation.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun InputScreen(
    modifier: Modifier = Modifier,
    CameraScreen: @Composable () -> Unit,
    GalleryScreen: @Composable () -> Unit
) {
    var inputState by remember { mutableStateOf<InputState?>(null) }

    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    when (inputState) {
        InputState.CAMERA_INPUT -> {
            if (!cameraPermissionState.status.isGranted) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "The feature requires camera permission",
                        textAlign = TextAlign.Center,
                        fontSize = 17.sp
                    )
                    ElevatedButton(
                        onClick = {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    ) {
                        Text("Grant Permission")
                    }
                }
            } else {
                CameraScreen()
            }

        }

        InputState.GALLERY_INPUT -> {
            GalleryScreen()
        }

        null -> {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ElevatedCard(
                    onClick = {
                        inputState = InputState.CAMERA_INPUT
                    },
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color(0xDD41174B)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(all = 16.dp).padding(horizontal = 6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            contentDescription = null
                        )

                        Spacer(Modifier.padding(vertical = 6.dp))

                        Text(
                            text = "Camera",
                            maxLines = 1,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                ElevatedCard(
                    onClick = {
                        inputState = InputState.GALLERY_INPUT
                    },
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color(0xDD41174B)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(all = 16.dp).padding(horizontal = 6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.ImageSearch,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            contentDescription = null
                        )

                        Spacer(Modifier.padding(vertical = 6.dp))

                        Text(
                            text = "Gallery",
                            maxLines = 1,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

}