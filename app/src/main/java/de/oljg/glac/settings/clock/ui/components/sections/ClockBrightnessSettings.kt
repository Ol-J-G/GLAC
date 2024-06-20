package de.oljg.glac.settings.clock.ui.components.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.SettingsSection
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.common.SettingsSlider
import de.oljg.glac.settings.clock.ui.components.common.SettingsSwitch
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_CLOCK_BRIGHTNESS
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.prettyPrintPercentage
import kotlinx.coroutines.launch

@Composable
fun ClockBrightnessSettings(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()

    SettingsSection(
        sectionTitle = stringResource(R.string.brightness),
        expanded = clockSettings.clockSettingsSectionBrigntnessIsExpanded,
        onExpandedChange = { expanded ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        clockSettingsSectionBrigntnessIsExpanded = expanded
                    )
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        SettingsSwitch(
            label = stringResource(R.string.override_system_brightness),
            checked = clockSettings.overrideSystemBrightness,
            onCheckedChange = { newValue ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            overrideSystemBrightness = newValue
                        )
                    )
                }
            }
        )

        AnimatedVisibility(visible = clockSettings.overrideSystemBrightness) {
            Column {
                Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
                SettingsSlider(
                    label = stringResource(R.string.clock_fullscreen_brightness),
                    value = clockSettings.clockBrightness,
                    defaultValue = DEFAULT_CLOCK_BRIGHTNESS,
                    sliderValuePrettyPrintFun = Float::prettyPrintPercentage,
                    onValueChangeFinished = { newBrightness ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    clockBrightness = newBrightness
                                )
                            )
                        }
                    },
                    onResetValue = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    clockBrightness = DEFAULT_CLOCK_BRIGHTNESS
                                )
                            )
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
    }
}
