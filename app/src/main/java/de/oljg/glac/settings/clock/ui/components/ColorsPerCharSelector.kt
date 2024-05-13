package de.oljg.glac.settings.clock.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.core.util.CommonClockUtils.CLOCK_CHARS
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import kotlinx.coroutines.launch

@Composable
fun ColorsPerCharSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value
    val defaultCharColor = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SettingsDefaults.COLOR_SELECTOR_HEIGHT),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(stringResource(R.string.set_color_per_character))
        Checkbox(
            checked = clockSettings.setColorsPerChar,
            onCheckedChange = {
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(setColorsPerChar = !clockSettings.setColorsPerChar))
                }
            }
        )
    }

    AnimatedVisibility(visible = clockSettings.setColorsPerChar) {
        Column {
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
                                charColors = clockSettings.charColors.put(clockChar, selectedColor)
                            )
                        )
                    }
                }
            }
        }
    }
}
