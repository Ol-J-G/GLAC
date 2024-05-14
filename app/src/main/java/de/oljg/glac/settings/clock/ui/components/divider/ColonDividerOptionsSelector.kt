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
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_COLON_FIRST_CIRCLE_POSITION
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_COLON_SECOND_CIRCLE_POSITION
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.common.SettingsSlider
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.prettyPrintCirclePosition
import kotlinx.coroutines.launch

@Composable
fun ColonDividerOptionsSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value

    SettingsSlider(
        label = stringResource(R.string.first_circle_position),
        value = clockSettings.colonFirstCirclePosition,
        defaultValue = DEFAULT_COLON_FIRST_CIRCLE_POSITION,
        sliderValuePrettyPrintFun = Float::prettyPrintCirclePosition,
        onValueChangeFinished = { newPosition ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(colonFirstCirclePosition = newPosition)
                )
            }
        },
        onResetValue = {
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        colonFirstCirclePosition = DEFAULT_COLON_FIRST_CIRCLE_POSITION
                    )
                )
            }
        }
    )
    Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
    SettingsSlider(
        label = stringResource(R.string.second_circle_position),
        value = clockSettings.colonSecondCirclePosition,
        defaultValue = DEFAULT_COLON_SECOND_CIRCLE_POSITION,
        sliderValuePrettyPrintFun = Float::prettyPrintCirclePosition,
        onValueChangeFinished = { newPosition ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(colonSecondCirclePosition = newPosition)
                )
            }
        },
        onResetValue = {
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        colonSecondCirclePosition = DEFAULT_COLON_SECOND_CIRCLE_POSITION
                    )
                )
            }
        }
    )
}
