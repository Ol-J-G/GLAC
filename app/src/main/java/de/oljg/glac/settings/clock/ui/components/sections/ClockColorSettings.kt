package de.oljg.glac.settings.clock.ui.components.sections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.core.clock.data.ClockSettings
import de.oljg.glac.core.clock.data.ClockTheme
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.color.ColorSelector
import de.oljg.glac.settings.clock.ui.components.color.ColorsPerCharSelector
import de.oljg.glac.settings.clock.ui.components.color.ColorsPerClockPartSelector
import de.oljg.glac.settings.clock.ui.components.color.SegmentColorsSelector
import de.oljg.glac.settings.clock.ui.components.common.SettingsSection
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockColorSettings(viewModel: ClockSettingsViewModel = hiltViewModel()) {
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

    SettingsSection(
        sectionTitle = stringResource(R.string.colors),
        expanded = clockSettings.clockSettingsSectionColorsIsExpanded,
        onExpandedChange = { expanded ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        clockSettingsSectionColorsIsExpanded = expanded
                    )
                )
            }
        }
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(DEFAULT_VERTICAL_SPACE / 2)
        )
        ColorSelector(
            title = stringResource(id = R.string.characters),
            color = clockTheme.charColor ?: defaultCharColor,
            defaultColor = defaultCharColor,
            onResetColor = {
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            themes = clockSettings.themes.put(
                                clockThemeName, clockTheme.copy(charColor = null))
                        )
                    )
                }
            }
        ) { selectedColor ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        themes = clockSettings.themes.put(
                            clockThemeName, clockTheme.copy(charColor = selectedColor))
                    )
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(DEFAULT_VERTICAL_SPACE / 2)
        )
        ColorSelector(
            title = stringResource(R.string.dividers),
            color = clockTheme.dividerColor ?: clockTheme.charColor ?: defaultCharColor,
            defaultColor = clockTheme.charColor ?: defaultCharColor,
            onResetColor = {
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            themes = clockSettings.themes.put(
                                clockThemeName, clockTheme.copy(dividerColor = null))
                        )
                    )
                }
            },
        ) { selectedColor ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        themes = clockSettings.themes.put(
                            clockThemeName, clockTheme.copy(dividerColor = selectedColor))
                    )
                )
            }
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(DEFAULT_VERTICAL_SPACE))
        ColorsPerCharSelector()

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(DEFAULT_VERTICAL_SPACE))
        ColorsPerClockPartSelector()

        AnimatedVisibility(visible = clockTheme.clockCharType == ClockCharType.SEVEN_SEGMENT) {
            Column {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DEFAULT_VERTICAL_SPACE)
                )
                SegmentColorsSelector()
            }
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(DEFAULT_VERTICAL_SPACE / 2))
    }
}
