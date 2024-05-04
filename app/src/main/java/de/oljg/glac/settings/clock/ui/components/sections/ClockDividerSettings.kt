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
import de.oljg.glac.settings.clock.ui.components.DividerStyleSelector
import de.oljg.glac.settings.clock.ui.components.common.SettingsSection
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockDividerSettings(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value

    SettingsSection(
        sectionTitle = stringResource(R.string.divider),
        expanded = clockSettings.clockSettingsSectionDividerIsExpanded,
        onExpandedChange = { expanded ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        clockSettingsSectionDividerIsExpanded = expanded
                    )
                )
            }
        }
    ) {
        DividerStyleSelector(
            label = stringResource(R.string.style),
            selectedDividerStyle = clockSettings.dividerStyle,
            onNewDividerStyleSelected = { newDividerStyle ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(dividerStyle = newDividerStyle))
                }
            }
        )

    }
}

