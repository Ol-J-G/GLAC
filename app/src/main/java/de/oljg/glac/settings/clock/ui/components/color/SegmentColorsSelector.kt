package de.oljg.glac.settings.clock.ui.components.color

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.utils.Segment
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_ROUNDED_CORNER_SIZE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.MULTI_COLOR_SELECTOR_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_SECTION_HEIGHT
import de.oljg.glac.settings.clock.ui.utils.prettyPrintEnumName
import kotlinx.coroutines.launch

@Composable
fun SegmentColorsSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value
    val defaultCharColor = defaultColor()

    Surface(
        modifier = Modifier
            .border(
                width = SettingsDefaults.DEFAULT_BORDER_WIDTH,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(DEFAULT_ROUNDED_CORNER_SIZE)
            )
            .padding(DEFAULT_VERTICAL_SPACE / 2),
    ) {
        Column(modifier = Modifier.padding(MULTI_COLOR_SELECTOR_PADDING)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SETTINGS_SECTION_HEIGHT),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.segment_colors))
                Switch(
                    checked = clockSettings.setSegmentColors,
                    onCheckedChange = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    setSegmentColors = !clockSettings.setSegmentColors)
                            )
                        }
                    }
                )
            }

            AnimatedVisibility(visible = clockSettings.setSegmentColors) {
                Column {
                    Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                    Segment.entries.forEach { segment ->
                        ColorSelector(
                            title = segment.name.prettyPrintEnumName(),
                            color =
                            clockSettings.segmentColors.getOrDefault(
                                key = segment,
                                defaultValue = clockSettings.charColor ?: defaultCharColor
                            ),
                            defaultColor = clockSettings.charColor ?: defaultCharColor,
                            onResetColor = {
                                coroutineScope.launch {
                                    viewModel.updateClockSettings(
                                        clockSettings.copy(
                                            segmentColors = clockSettings.segmentColors.remove(segment)
                                        )
                                    )
                                }
                            }
                        ) { selectedColor ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(
                                    clockSettings.copy(
                                        segmentColors = clockSettings.segmentColors.put(
                                            segment,
                                            selectedColor
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
