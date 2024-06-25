package de.oljg.glac.feature_clock.ui.settings.components.divider

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.ui.ClockSettingsViewModel
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_COLON_FIRST_CIRCLE_POSITION
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_COLON_SECOND_CIRCLE_POSITION
import de.oljg.glac.feature_clock.ui.settings.components.common.SettingsSlider
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.prettyPrintCirclePosition
import kotlinx.coroutines.launch

@Composable
fun ColonDividerOptionsSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
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
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        themes = clockSettings.themes.put(
                            clockThemeName, clockTheme.copy(colonFirstCirclePosition = newPosition))
                    )
                )
            }
        },
        onResetValue = {
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        themes = clockSettings.themes.put(
                            clockThemeName, clockTheme.copy(
                                colonFirstCirclePosition = DEFAULT_COLON_FIRST_CIRCLE_POSITION))
                    )
                )
            }
        }
    )
    Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
    SettingsSlider(
        label = stringResource(R.string.second_circle_position),
        value = clockTheme.colonSecondCirclePosition,
        defaultValue = DEFAULT_COLON_SECOND_CIRCLE_POSITION,
        sliderValuePrettyPrintFun = Float::prettyPrintCirclePosition,
        onValueChangeFinished = { newPosition ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        themes = clockSettings.themes.put(
                            clockThemeName, clockTheme.copy(colonSecondCirclePosition = newPosition))
                    )
                )
            }
        },
        onResetValue = {
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        themes = clockSettings.themes.put(
                            clockThemeName, clockTheme.copy(
                                colonSecondCirclePosition = DEFAULT_COLON_SECOND_CIRCLE_POSITION))
                    )
                )
            }
        }
    )
}
