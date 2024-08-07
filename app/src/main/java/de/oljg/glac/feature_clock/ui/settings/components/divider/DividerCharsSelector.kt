package de.oljg.glac.feature_clock.ui.settings.components.divider

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults

@Composable
fun DividerCharsSelector(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )

    DividerCharSelector(
        title = stringResource(R.string.between) + " " +
                stringResource(R.string.hours) + " / " +
                stringResource(R.string.minutes),
        char = clockTheme.hoursMinutesDividerChar,
        onCharChanged = { newChar ->
            onEvent(
                ClockSettingsEvent.UpdateThemes(
                    clockThemeName,
                    clockTheme.copy(hoursMinutesDividerChar = newChar)
                )
            )
        }
    )
    HorizontalDivider(
        modifier = Modifier.padding(
            top = ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE / 2,
            bottom = ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE
        )
    )
    DividerCharSelector(
        title = stringResource(R.string.between) + " " +
                stringResource(R.string.minutes) + " / " +
                stringResource(R.string.seconds),
        char = clockTheme.minutesSecondsDividerChar,
        onCharChanged = { newChar ->
            onEvent(
                ClockSettingsEvent.UpdateThemes(
                    clockThemeName,
                    clockTheme.copy(minutesSecondsDividerChar = newChar)
                )
            )
        }
    )
    HorizontalDivider(modifier = Modifier.padding(
        vertical = ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE)
    )
    DividerCharSelector(
        title = stringResource(R.string.between) + " " +
                stringResource(R.string.minutes_shortened) + ". | " +
                stringResource(R.string.seconds_shortened) + ". / " +
                stringResource(R.string.daytime_marker),
        char = clockTheme.daytimeMarkerDividerChar,
        onCharChanged = { newChar ->
            onEvent(
                ClockSettingsEvent.UpdateThemes(
                    clockThemeName,
                    clockTheme.copy(daytimeMarkerDividerChar = newChar)
                )
            )
        }
    )
}
