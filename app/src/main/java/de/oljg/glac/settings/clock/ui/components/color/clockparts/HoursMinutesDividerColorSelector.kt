package de.oljg.glac.settings.clock.ui.components.color.clockparts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.color.ColorSelector
import kotlinx.coroutines.launch

@Composable
fun HoursMinutesDividerColorSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value
    val defaultCharColor = defaultColor()
    val currentClockPartsColors = clockSettings.clockPartsColors

    ColorSelector(
        title = stringResource(R.string.div_h_m),
        color = clockSettings.clockPartsColors.dividers.hoursMinutes
            ?: clockSettings.dividerColor
            ?: clockSettings.charColor
            ?: defaultCharColor,
        defaultColor = clockSettings.dividerColor ?: clockSettings.charColor ?: defaultCharColor,
        onResetColor = {
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        clockPartsColors = currentClockPartsColors.copy(
                            dividers = currentClockPartsColors.dividers.copy(hoursMinutes = null)
                        )
                    )
                )
            }
        }
    ) { selectedColor ->
        coroutineScope.launch {
            viewModel.updateClockSettings(
                clockSettings.copy(
                    clockPartsColors = currentClockPartsColors.copy(
                        dividers = currentClockPartsColors.dividers.copy(hoursMinutes = selectedColor)
                    )
                )
            )
        }
    }
}

