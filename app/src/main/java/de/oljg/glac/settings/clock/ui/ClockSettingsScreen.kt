package de.oljg.glac.settings.clock.ui


import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.oljg.glac.core.settings.data.ClockSettings
import kotlinx.coroutines.launch

@Composable
fun ClockSettingsScreen(
    viewModel: ClockSettingsViewModel
) {
    val scrollState = rememberScrollState()

    val clockSettings = viewModel.clockSettings.collectAsState(
        initial = ClockSettings()
    ).value

    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.scrollable(state = scrollState, orientation = Orientation.Vertical),
            verticalArrangement = Arrangement.Top,
        ) {
            Row( //TODO: extract SettingsSwitch() composable
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show Seconds")
                Switch(
                    checked = clockSettings.showSeconds,
                    onCheckedChange = { newValue ->
                        coroutineScope.launch {
                            viewModel.setShowSeconds(newValue)
                        }
                    }
                )
            }
        }
    }
}