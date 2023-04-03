package com.github.bkmbigo.passionfruitdetector.presentation.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.bkmbigo.passionfruitdetector.presentation.theme.PassionFruitDetectorTheme

@Composable
fun <T> SettingsItem(
    settingDataItem: SettingDataItem<T>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = settingDataItem.label),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.padding(vertical = 4.dp))

        Text(
            text = when (val value = settingDataItem.getOptionWithValue()) {
                is SettingOption.StringResource -> stringResource(id = value.resId)
                is SettingOption.Value -> value.value.toString()
                is SettingOption.ValueWithName -> value.name
            },
        )
    }
}

@Preview
@Composable
fun PreviewSettingsItem() {
    PassionFruitDetectorTheme {
        Surface {
            SettingsItem(
                settingDataItem = SettingDataItem.ShowConfidenceSettingItem(true),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}