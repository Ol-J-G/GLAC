package de.oljg.glac.settings.clock.ui.components.color.clockparts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.core.settings.data.ClockTheme
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.color.ColorSelector
import kotlinx.coroutines.launch

@Composable
fun HoursOnesColorSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )
    val defaultCharColor = defaultColor()
    val currentClockPartsColors = clockTheme.clockPartsColors

    ColorSelector(
        title = stringResource(id = R.string.hours) + " " + stringResource(R.string.ones),
        color = clockTheme.clockPartsColors.hours.ones
            ?: clockTheme.charColor
            ?: defaultCharColor,
        defaultColor = clockTheme.charColor ?: defaultCharColor,
        onResetColor = {
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        themes = clockSettings.themes.put(
                            clockThemeName, clockTheme.copy(
                                clockPartsColors = currentClockPartsColors.copy(
                                    hours = currentClockPartsColors.hours.copy(
                                        ones = null
                                    )
                                )
                            )
                        )
                    )
                )
            }
        }
    ) { selectedColor ->
        coroutineScope.launch {
            viewModel.updateClockSettings(
                clockSettings.copy(
                    themes = clockSettings.themes.put(
                        clockThemeName, clockTheme.copy(
                            clockPartsColors = currentClockPartsColors.copy(
                                hours = currentClockPartsColors.hours.copy(
                                    ones = selectedColor
                                )
                            )
                        )
                    )
                )
            )
        }
    }
}

