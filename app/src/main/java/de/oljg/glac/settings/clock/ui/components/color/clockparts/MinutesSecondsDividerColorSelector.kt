package de.oljg.glac.settings.clock.ui.components.color.clockparts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.clock.data.ClockTheme
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.color.ColorSelector

@Composable
fun MinutesSecondsDividerColorSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )
    val defaultCharColor = defaultColor()
    val currentClockPartsColors = clockTheme.clockPartsColors

    ColorSelector(
        title = stringResource(R.string.div_m_s),
        color = clockTheme.clockPartsColors.dividers.minutesSeconds
            ?: clockTheme.dividerColor
            ?: clockTheme.charColor
            ?: defaultCharColor,
        defaultColor = clockTheme.dividerColor ?: clockTheme.charColor ?: defaultCharColor,
        onResetColor = {
            viewModel.updateClockTheme(
                clockSettings, clockThemeName,
                clockTheme.copy(
                    clockPartsColors = currentClockPartsColors.copy(
                        dividers = currentClockPartsColors.dividers.copy(
                            minutesSeconds = null
                        )
                    )
                )
            )
        }
    ) { selectedColor ->
        viewModel.updateClockTheme(
            clockSettings, clockThemeName,
            clockTheme.copy(
                clockPartsColors = currentClockPartsColors.copy(
                    dividers = currentClockPartsColors.dividers.copy(
                        minutesSeconds = selectedColor
                    )
                )
            )
        )
    }
}
