package de.oljg.glac.settings.clock.ui.components.character

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_STROKE_WIDTH
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.MAX_STROKE_WIDTH
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.MIN_STROKE_WIDTH
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.contains
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.common.SettingsSlider
import de.oljg.glac.settings.clock.ui.components.common.SettingsSwitch
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.prettyPrintPixel
import kotlinx.coroutines.launch

@Composable
fun SevenSegmentSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value

    Column {
        SevenSegmentWeightSelector(
            label = "${stringResource(id = R.string.weight)}:",
            selectedSevenSegmentWeight = clockSettings.sevenSegmentWeight,
            onNewSevenSegmentWeightSelected = { newSevenSegmentWeight ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            sevenSegmentWeight = newSevenSegmentWeight
                        )
                    )
                }
            }
        )
        SevenSegmentStyleSelector(
            label = "${stringResource(id = R.string.style)}:    ",
            selectedSevenSegmentStyle = clockSettings.sevenSegmentStyle,
            onNewSevenSegmentStyleSelected = { newSevenSegmentStyle ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            sevenSegmentStyle = newSevenSegmentStyle
                        )
                    )
                }
            }
        )
        AnimatedVisibility(visible = clockSettings.sevenSegmentStyle
            .contains(SevenSegmentStyle.OUTLINE.name)) {
            Column {
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.outline_size),
                    value = clockSettings.sevenSegmentOutlineSize,
                    defaultValue = DEFAULT_STROKE_WIDTH,
                    sliderValuePrettyPrintFun = Float::prettyPrintPixel,
                    onValueChangeFinished = { newValue ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    sevenSegmentOutlineSize = newValue
                                )
                            )
                        }
                    },
                    onResetValue = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(sevenSegmentOutlineSize = DEFAULT_STROKE_WIDTH)
                            )
                        }
                        DEFAULT_STROKE_WIDTH
                    },
                    valueRange = MIN_STROKE_WIDTH..MAX_STROKE_WIDTH
                )
                Spacer(modifier = Modifier.fillMaxWidth().height(DEFAULT_VERTICAL_SPACE / 2))
            }
        }
        Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
        SettingsSwitch(
            label = stringResource(R.string.off_segments),
            checked = clockSettings.drawOffSegments,
            onCheckedChange = { newValue ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(drawOffSegments = newValue)
                    )
                }
            }
        )
    }
}

