package de.oljg.glac.feature_clock.ui.settings.components.color.clockparts

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.utils.defaultColor
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.settings.components.color.ColorSelector

@Composable
fun MinutesSecondsDividerColorSelector(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
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
            onEvent(
                ClockSettingsEvent.UpdateThemes(
                    clockThemeName,
                    clockTheme.copy(
                        clockPartsColors = currentClockPartsColors.copy(
                            dividers = currentClockPartsColors.dividers.copy(minutesSeconds = null)
                        )
                    )
                )
            )
        }
    ) { selectedColor ->
        onEvent(
            ClockSettingsEvent.UpdateThemes(
                clockThemeName,
                clockTheme.copy(
                    clockPartsColors = currentClockPartsColors.copy(
                        dividers = currentClockPartsColors.dividers.copy(
                            minutesSeconds = selectedColor
                        )
                    )
                )
            )
        )
    }
}
