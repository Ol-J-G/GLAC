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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.core.util.CommonClockUtils.CLOCK_CHARS
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_ROUNDED_CORNER_SIZE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_SECTION_HEIGHT
import kotlinx.coroutines.launch

@Composable
fun ColorsPerCharSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value
    val defaultCharColor = MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = Modifier
            .border(
                width = SettingsDefaults.DEFAULT_BORDER_WIDTH,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(DEFAULT_ROUNDED_CORNER_SIZE)
            )
            .padding(DEFAULT_VERTICAL_SPACE / 2),
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SETTINGS_SECTION_HEIGHT),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.set_color_per_character))
                Switch(
                    checked = clockSettings.setColorsPerChar,
                    onCheckedChange = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(setColorsPerChar = !clockSettings.setColorsPerChar)
                            )
                        }
                    }
                )
            }

            AnimatedVisibility(visible = clockSettings.setColorsPerChar) {
                Column {
                    Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                    CLOCK_CHARS.forEach { clockChar ->
                        ColorSelector(
                            title = clockChar.toString(),
                            color =
                            clockSettings.charColors.getOrDefault(
                                key = clockChar,
                                defaultValue = clockSettings.charColor ?: defaultCharColor
                            ),
                            defaultColor = clockSettings.charColor ?: defaultCharColor,
                            onResetColor = {
                                coroutineScope.launch {
                                    viewModel.updateClockSettings(
                                        clockSettings.copy(
                                            charColors = clockSettings.charColors.remove(clockChar)
                                        )
                                    )
                                }
                            }
                        ) { selectedColor ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(
                                    clockSettings.copy(
                                        charColors = clockSettings.charColors.put(
                                            clockChar,
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
