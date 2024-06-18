package de.oljg.glac.settings.clock.ui.components.character

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_OUTLINE_SIZE
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.MAX_STROKE_WIDTH
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.MIN_STROKE_WIDTH
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.clock.digital.ui.utils.isOutline
import de.oljg.glac.core.clock.data.ClockTheme
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.common.SettingsSlider
import de.oljg.glac.settings.clock.ui.components.common.SettingsSwitch
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.prettyPrintPixel
import kotlinx.coroutines.launch

@Composable
fun SevenSegmentSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )

    Column {
        SevenSegmentWeightSelector(
            label = stringResource(id = R.string.weight),
            selectedSevenSegmentWeight = clockTheme.sevenSegmentWeight,
            onNewSevenSegmentWeightSelected = { newSevenSegmentWeight ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            themes = clockSettings.themes.put(
                                clockThemeName, clockTheme.copy(
                                    sevenSegmentWeight = SevenSegmentWeight.valueOf(
                                        newSevenSegmentWeight
                                    )
                                )
                            )
                        )
                    )
                }
            }
        )
        SevenSegmentStyleSelector(
            label = stringResource(id = R.string.style),
            selectedSevenSegmentStyle = clockTheme.sevenSegmentStyle,
            onNewSevenSegmentStyleSelected = { newSevenSegmentStyle ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            themes = clockSettings.themes.put(
                                clockThemeName, clockTheme.copy(
                                    sevenSegmentStyle = SevenSegmentStyle.valueOf(
                                        newSevenSegmentStyle
                                    )
                                )
                            )
                        )
                    )
                }
            }
        )
        AnimatedVisibility(visible = clockTheme.sevenSegmentStyle.isOutline()) {
            Column {
                Divider(
                    modifier = Modifier.padding(
                        top = DEFAULT_VERTICAL_SPACE / 2,
                        bottom = DEFAULT_VERTICAL_SPACE
                    )
                )
                SettingsSlider(
                    label = stringResource(R.string.outline_size),
                    value = clockTheme.sevenSegmentOutlineSize,
                    defaultValue = DEFAULT_OUTLINE_SIZE,
                    sliderValuePrettyPrintFun = Float::prettyPrintPixel,
                    onValueChangeFinished = { newValue ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    themes = clockSettings.themes.put(
                                        clockThemeName, clockTheme.copy(
                                            sevenSegmentOutlineSize = newValue
                                        )
                                    )
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
                                            sevenSegmentOutlineSize = DEFAULT_OUTLINE_SIZE
                                        )
                                    )
                                )
                            )
                        }
                    },
                    valueRange = MIN_STROKE_WIDTH..MAX_STROKE_WIDTH
                )
                Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
            }
        }
        Divider(
            modifier = Modifier.padding(
                top = DEFAULT_VERTICAL_SPACE / 2,
                bottom = DEFAULT_VERTICAL_SPACE
            )
        )
        SettingsSwitch(
            label = stringResource(R.string.off_segments),
            checked = clockTheme.drawOffSegments,
            onCheckedChange = { newValue ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            themes = clockSettings.themes.put(
                                clockThemeName, clockTheme.copy(drawOffSegments = newValue)
                            )
                        )
                    )
                }
            }
        )
    }
}

