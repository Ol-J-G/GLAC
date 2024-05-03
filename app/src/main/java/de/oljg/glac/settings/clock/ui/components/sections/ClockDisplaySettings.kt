package de.oljg.glac.settings.clock.ui.components.sections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.common.SettingsSection
import de.oljg.glac.settings.clock.ui.components.common.SettingsSwitch
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockDisplaySettings(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value

    SettingsSection(
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
        SettingsSwitch(
            label = stringResource(R.string.seconds),
            checked = clockSettings.showSeconds,
            onCheckedChange = { newValue ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(clockSettings.copy(showSeconds = newValue))
                }
            }
        )
        SettingsSwitch(
            label = stringResource(R.string.daytime_marker),
            checked = clockSettings.showDaytimeMarker,
            onCheckedChange = { newValue ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(clockSettings.copy(showDaytimeMarker = newValue))
                }
            }
        )
    }
}
