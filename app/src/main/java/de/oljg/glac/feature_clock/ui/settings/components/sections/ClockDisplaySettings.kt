package de.oljg.glac.feature_clock.ui.settings.components.sections

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
import de.oljg.glac.core.ui.components.ExpandableSection
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.ui.ClockSettingsViewModel
import de.oljg.glac.feature_clock.ui.settings.components.common.SettingsSwitch
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import kotlinx.coroutines.launch

@Composable
fun ClockDisplaySettings(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )

    ExpandableSection(
        sectionTitle = stringResource(R.string.display),
        expanded = clockSettings.clockSettingsSectionDisplayIsExpanded,
        onExpandedChange = { expanded ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        clockSettingsSectionDisplayIsExpanded = expanded
                    )
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        SettingsSwitch(
            label = stringResource(R.string.seconds),
            checked = clockTheme.showSeconds,
            onCheckedChange = { newValue ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            themes = clockSettings.themes.put(
                                clockThemeName, clockTheme.copy(showSeconds = newValue))
                        )
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        SettingsSwitch(
            label = stringResource(R.string.daytime_marker),
            checked = clockTheme.showDaytimeMarker,
            onCheckedChange = { newValue ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            themes = clockSettings.themes.put(
                                clockThemeName, clockTheme.copy(showDaytimeMarker = newValue))
                        )
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
    }
}

