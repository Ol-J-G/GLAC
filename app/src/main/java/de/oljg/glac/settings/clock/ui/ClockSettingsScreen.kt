package de.oljg.glac.settings.clock.ui


import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.oljg.glac.R
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.settings.clock.ui.components.SettingsSwitch
import kotlinx.coroutines.launch

@Composable
fun ClockSettingsScreen(
    viewModel: ClockSettingsViewModel
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettings.collectAsState(
        initial = ClockSettings()
    ).value

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
            SettingsSwitch(
                label = stringResource(R.string.show_seconds),
                checked = clockSettings.showSeconds,
                onCheckedChange = { newValue ->
                    coroutineScope.launch {
                        viewModel.updateClockSettings(clockSettings.copy(showSeconds = newValue))
                    }
                }
            )
            SettingsSwitch(
                label = stringResource(R.string.show_daytime_marker),
                checked = clockSettings.showDaytimeMarker,
                onCheckedChange = { newValue ->
                    coroutineScope.launch {
                        viewModel.updateClockSettings(clockSettings.copy(showDaytimeMarker = newValue))
                    }
                }
            )
        }
    }
}