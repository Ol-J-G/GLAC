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
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.core.clock.data.ClockTheme
import de.oljg.glac.core.ui.components.SettingsSection
import de.oljg.glac.core.util.defaultBackgroundColor
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.color.ColorSelector
import de.oljg.glac.settings.clock.ui.components.color.ColorsPerCharSelector
import de.oljg.glac.settings.clock.ui.components.color.ColorsPerClockPartSelector
import de.oljg.glac.settings.clock.ui.components.color.SegmentColorsSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import kotlinx.coroutines.launch

@Composable
fun ClockColorSettings(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )
    val defaultCharColor = defaultColor()
    val defaultBackgroundColor = defaultBackgroundColor()

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
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        ColorSelector(
            title = stringResource(id = R.string.characters),
            color = clockTheme.charColor ?: defaultCharColor,
            defaultColor = defaultCharColor,
            onResetColor = {
                viewModel.updateClockTheme(
                    clockSettings, clockThemeName, clockTheme.copy(charColor = null)
                )
            }
        ) { selectedColor ->
            viewModel.updateClockTheme(
                clockSettings, clockThemeName, clockTheme.copy(charColor = selectedColor)
            )
        }
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        ColorSelector(
            title = stringResource(R.string.background),
            color = clockTheme.backgroundColor ?: defaultBackgroundColor,
            defaultColor = defaultBackgroundColor,
            onResetColor = {
                viewModel.updateClockTheme(
                    clockSettings, clockThemeName, clockTheme.copy(backgroundColor = null)
                )
            },
        ) { selectedColor ->
            viewModel.updateClockTheme(
                clockSettings, clockThemeName, clockTheme.copy(backgroundColor = selectedColor)
            )
        }
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        ColorSelector(
            title = stringResource(R.string.dividers),
            color = clockTheme.dividerColor ?: clockTheme.charColor ?: defaultCharColor,
            defaultColor = clockTheme.charColor ?: defaultCharColor,
            onResetColor = {
                viewModel.updateClockTheme(
                    clockSettings, clockThemeName, clockTheme.copy(dividerColor = null)
                )
            },
        ) { selectedColor ->
            viewModel.updateClockTheme(
                clockSettings, clockThemeName, clockTheme.copy(dividerColor = selectedColor)
            )
        }
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))
        ColorsPerCharSelector()

        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))
        ColorsPerClockPartSelector()

        AnimatedVisibility(visible = clockTheme.clockCharType == ClockCharType.SEVEN_SEGMENT) {
            Column {
                Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))
                SegmentColorsSelector()
            }
        }
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
    }
}
