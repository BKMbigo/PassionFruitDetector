package com.github.bkmbigo.passionfruitdetector.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.bkmbigo.passionfruitdetector.R
import com.github.bkmbigo.passionfruitdetector.domain.repositories.HistoryRepository
import com.github.bkmbigo.passionfruitdetector.presentation.components.history.HistoryItem
import kotlinx.coroutines.launch

context(HistoryRepository)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // State Variables
    val detectionHistoryItems by getHistoryItemLive().observeAsState(initial = emptyList())

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(all = 8.dp)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.title_history),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedIconButton(
                onClick = {
                    coroutineScope.launch {
                        deleteAllHistoryItems()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null
                )
            }
        }

        Spacer(Modifier.padding(vertical = 6.dp))


        LazyColumn(
            modifier = Modifier.weight(1F, true),
        ) {
            items(detectionHistoryItems) { detectionItem ->
                HistoryItem(
                    detectionHistory = detectionItem,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 6.dp),
                    onClick = {}
                )
            }
        }
    }
}