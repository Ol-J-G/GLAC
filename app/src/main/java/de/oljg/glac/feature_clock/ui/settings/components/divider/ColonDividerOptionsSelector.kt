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
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_COLON_FIRST_CIRCLE_POSITION
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_COLON_SECOND_CIRCLE_POSITION
import de.oljg.glac.feature_clock.ui.settings.components.SettingsSlider
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.prettyPrintCirclePosition

@Composable
fun ColonDividerOptionsSelector(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )

    SettingsSlider(
        label = stringResource(R.string.first_circle_position),
        value = clockTheme.colonFirstCirclePosition,
        defaultValue = DEFAULT_COLON_FIRST_CIRCLE_POSITION,
        sliderValuePrettyPrintFun = Float::prettyPrintCirclePosition,
        onValueChangeFinished = { newPosition ->
            onEvent(
                ClockSettingsEvent.UpdateThemes(
                    clockThemeName,
                    clockTheme.copy(colonFirstCirclePosition = newPosition)
                )
            )
        },
        onResetValue = {
            onEvent(
                ClockSettingsEvent.UpdateThemes(
                    clockThemeName,
                    clockTheme.copy(colonFirstCirclePosition = DEFAULT_COLON_FIRST_CIRCLE_POSITION)
                )
            )
        }
    )
    HorizontalDivider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
    SettingsSlider(
        label = stringResource(R.string.second_circle_position),
        value = clockTheme.colonSecondCirclePosition,
        defaultValue = DEFAULT_COLON_SECOND_CIRCLE_POSITION,
        sliderValuePrettyPrintFun = Float::prettyPrintCirclePosition,
        onValueChangeFinished = { newPosition ->
            onEvent(
                ClockSettingsEvent.UpdateThemes(
                    clockThemeName,
                    clockTheme.copy(colonSecondCirclePosition = newPosition)
                )
            )
        },
        onResetValue = {
            onEvent(
                ClockSettingsEvent.UpdateThemes(
                    clockThemeName,
                    clockTheme.copy(
                        colonSecondCirclePosition = DEFAULT_COLON_SECOND_CIRCLE_POSITION
                    )
                )
            )
        }
    )
}
