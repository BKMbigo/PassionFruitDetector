package com.github.bkmbigo.passionfruitdetector.presentation.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.bkmbigo.passionfruitdetector.presentation.components.settings.SettingDataItem
import com.github.bkmbigo.passionfruitdetector.presentation.components.settings.SettingOption
import kotlinx.coroutines.launch

@Composable
fun <T> SettingsDialog(
    settingDataItem: SettingDataItem<T>,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onValueChanged: suspend (SettingOption<T>) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {
            if (!loading) {
                onDismissRequest()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        // Paddings for each of the dialog's parts.
        val dialogPadding = PaddingValues(all = 24.dp)
        val iconPadding = PaddingValues(bottom = 16.dp)
        val titlePadding = PaddingValues(bottom = 16.dp)
        val textPadding = PaddingValues(bottom = 24.dp)

        val minWidth = 280.dp
        val maxWidth = 560.dp


        Surface(
            modifier = modifier,
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .sizeIn(minWidth = minWidth, maxWidth = maxWidth)
                    .padding(dialogPadding)
            ) {
                CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.titleContentColor) {
                    val textStyle = MaterialTheme.typography.headlineSmall
                    ProvideTextStyle(textStyle) {
                        Box(
                            // Align the title to the center when an icon is present.
                            Modifier
                                .padding(titlePadding)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = stringResource(id = settingDataItem.label)
                            )
                        }
                    }
                }

                if (settingDataItem is SettingDataItem.SettingDataItemWithDescription) {
                    CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.textContentColor) {
                        val textStyle =
                            MaterialTheme.typography.bodyMedium
                        ProvideTextStyle(textStyle) {
                            Box(
                                Modifier
                                    .weight(weight = 1f, fill = false)
                                    .padding(textPadding)
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = stringResource(id = settingDataItem.description),
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.padding(vertical = 12.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        settingDataItem.options.forEachIndexed { index, settingOption ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (settingDataItem.value != settingOption.value && !loading) {
                                            coroutineScope.launch {
                                                loading = true
                                                onValueChanged(settingOption)
                                                onDismissRequest()
                                            }
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                RadioButton(
                                    selected = settingDataItem.value == settingOption.value,
                                    onClick = {
                                        if (settingDataItem.value != settingOption.value && !loading) {
                                            coroutineScope.launch {
                                                loading = true
                                                onValueChanged(settingOption)
                                                onDismissRequest()
                                            }
                                        }
                                    }
                                )

                                Text(
                                    text = when (settingOption) {
                                        is SettingOption.StringResource -> stringResource(id = settingOption.resId)
                                        is SettingOption.Value -> settingOption.value.toString()
                                        is SettingOption.ValueWithName -> settingOption.name
                                    },
                                    fontFamily = FontFamily.Serif,
                                    maxLines = 2,
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (index != settingDataItem.options.size - 1) {
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }

                    if(loading)
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }

    }
}