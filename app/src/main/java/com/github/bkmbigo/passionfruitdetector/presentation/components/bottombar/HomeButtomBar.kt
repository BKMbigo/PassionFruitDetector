package com.github.bkmbigo.passionfruitdetector.presentation.components.bottombar

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.bkmbigo.passionfruitdetector.presentation.navigation.BottomBarDestination

@Composable
fun HomeBottomBar(
    selectedScreen: BottomBarDestination,
    modifier: Modifier = Modifier,
    onNavigationItemSelected: (BottomBarDestination) -> Unit,
) {

    NavigationBar(
        modifier = modifier
    ) {
        BottomBarDestination.values().forEach { destination ->
            NavigationBarItem(
                selected = selectedScreen == destination,
                onClick = {
                    if (selectedScreen != destination) {
                        onNavigationItemSelected(destination)
                    }
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = destination.label)
                    )
                }
            )
        }
    }
}