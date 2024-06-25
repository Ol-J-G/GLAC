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
fun SecondsOnesColorSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )
    val defaultCharColor = defaultColor()
    val currentClockPartsColors = clockTheme.clockPartsColors

    ColorSelector(
        title = stringResource(id = R.string.seconds_shortened) + " " + stringResource(R.string.ones),
        color = clockTheme.clockPartsColors.seconds.ones
            ?: clockTheme.charColor
            ?: defaultCharColor,
        defaultColor = clockTheme.charColor ?: defaultCharColor,
        onResetColor = {
            viewModel.updateClockTheme(
                clockSettings, clockThemeName,
                clockTheme.copy(
                    clockPartsColors = currentClockPartsColors.copy(
                        seconds = currentClockPartsColors.seconds.copy(ones = null)
                    )
                )
            )
        }
    ) { selectedColor ->
        viewModel.updateClockTheme(
            clockSettings, clockThemeName,
            clockTheme.copy(
                clockPartsColors = currentClockPartsColors.copy(
                    seconds = currentClockPartsColors.seconds.copy(ones = selectedColor)
                )
            )
        )
    }
}
