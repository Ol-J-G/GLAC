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
import de.oljg.glac.core.clock.data.ClockSettings
import de.oljg.glac.core.clock.data.ClockTheme
import de.oljg.glac.core.util.CommonClockUtils.CLOCK_CHARS
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_BORDER_WIDTH
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_ROUNDED_CORNER_SIZE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.EDGE_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.MULTI_COLOR_SELECTOR_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_SECTION_HEIGHT
import kotlinx.coroutines.launch

@Composable
fun ColorsPerCharSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )
    val defaultCharColor = defaultColor()

    Surface(
        modifier = Modifier
            .border(
                width = DEFAULT_BORDER_WIDTH,
                color = if(clockTheme.setColorsPerChar)
                    MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
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
                Text(
                    modifier = Modifier.padding(start = EDGE_PADDING / 2),
                    text = stringResource(R.string.color_per_character)
                )
                Switch(
                    checked = clockTheme.setColorsPerChar,
                    onCheckedChange = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    themes = clockSettings.themes.put(
                                        clockThemeName, clockTheme.copy(
                                            setColorsPerChar = !clockTheme.setColorsPerChar))
                                )
                            )
                        }
                    }
                )
            }

            AnimatedVisibility(visible = clockTheme.setColorsPerChar) {
                Column {
                    Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE / 2))
                    CLOCK_CHARS.forEach { clockChar ->
                        ColorSelector(
                            title = clockChar.toString(),
                            color = clockTheme.charColors.getOrDefault(
                                key = clockChar,
                                defaultValue = clockTheme.charColor ?: defaultCharColor
                            ),
                            defaultColor = clockTheme.charColor ?: defaultCharColor,
                            onResetColor = {
                                coroutineScope.launch {
                                    viewModel.updateClockSettings(
                                        clockSettings.copy(
                                            themes = clockSettings.themes.put(
                                                clockThemeName, clockTheme.copy(
                                                    charColors = clockTheme.charColors.remove(
                                                        clockChar
                                                    )
                                                )
                                            )
                                        )
                                    )
                                }
                            }
                        ) { selectedColor ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(
                                    clockSettings.copy(
                                        themes = clockSettings.themes.put(
                                            clockThemeName, clockTheme.copy(
                                                charColors = clockTheme.charColors.put(
                                                    clockChar,
                                                    selectedColor
                                                )
                                            )
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
