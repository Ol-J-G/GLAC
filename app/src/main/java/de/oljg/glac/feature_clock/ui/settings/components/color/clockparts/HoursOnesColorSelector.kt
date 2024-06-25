package de.oljg.glac.feature_clock.ui.settings.components.color.clockparts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.ui.ClockSettingsViewModel
import de.oljg.glac.feature_clock.ui.settings.components.color.ColorSelector

@Composable
fun HoursOnesColorSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )
    val defaultCharColor = defaultColor()
    val currentClockPartsColors = clockTheme.clockPartsColors

    ColorSelector(
        title = stringResource(id = R.string.hours_shortened) + " " + stringResource(R.string.ones),
        color = clockTheme.clockPartsColors.hours.ones
            ?: clockTheme.charColor
            ?: defaultCharColor,
        defaultColor = clockTheme.charColor ?: defaultCharColor,
        onResetColor = {
            viewModel.updateClockTheme(
                clockSettings, clockThemeName,
                clockTheme.copy(
                    clockPartsColors = currentClockPartsColors.copy(
                        hours = currentClockPartsColors.hours.copy(ones = null)
                    )
                )
            )
        }
    ) { selectedColor ->
        viewModel.updateClockTheme(
            clockSettings, clockThemeName,
            clockTheme.copy(
                clockPartsColors = currentClockPartsColors.copy(
                    hours = currentClockPartsColors.hours.copy(ones = selectedColor)
                )
            )
        )
    }
}
