package de.oljg.glac.settings.clock.ui.components.divider

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import kotlinx.coroutines.launch

@Composable
fun CharDividerOptionsSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value

    DividerCharSelector(
        title = stringResource(R.string.between) + " " +
                stringResource(R.string.hours) + " / " +
                stringResource(R.string.minutes),
        char = clockSettings.hoursMinutesDividerChar,
        onCharChanged = { newChar ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(hoursMinutesDividerChar = newChar)
                )
            }
        }
    )
    Divider(modifier = Modifier.padding(vertical = SettingsDefaults.DEFAULT_VERTICAL_SPACE))
    DividerCharSelector(
        title = stringResource(R.string.between) + " " +
                stringResource(R.string.minutes) + " / " +
                stringResource(R.string.seconds),
        char = clockSettings.minutesSecondsDividerChar,
        onCharChanged = { newChar ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(minutesSecondsDividerChar = newChar)
                )
            }
        }
    )
    Divider(modifier = Modifier.padding(vertical = SettingsDefaults.DEFAULT_VERTICAL_SPACE))
    DividerCharSelector(
        title = stringResource(R.string.between) + " " +
                stringResource(R.string.min) + ". | " +
                stringResource(R.string.sec) + ". / " +
                stringResource(R.string.daytime_marker),
        char = clockSettings.daytimeMarkerDividerChar,
        onCharChanged = { newChar ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(daytimeMarkerDividerChar = newChar)
                )
            }
        }
    )
}
