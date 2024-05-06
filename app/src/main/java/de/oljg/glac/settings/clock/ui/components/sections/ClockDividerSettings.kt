package de.oljg.glac.settings.clock.ui.components.sections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MAX_DIVIDER_THICKNESS
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MIN_DIVIDER_THICKNESS
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.DividerStyleSelector
import de.oljg.glac.settings.clock.ui.components.common.SettingsSection
import de.oljg.glac.settings.clock.ui.components.common.SettingsSlider
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.prettyPrintOnePlace
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

        AnimatedVisibility(
            visible = DividerStyle.valueOf(clockSettings.dividerStyle) != DividerStyle.NONE
        ) {
            Column {
                Divider(modifier = Modifier.padding(vertical = SettingsDefaults.DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.size),
                    value = clockSettings.dividerThickness,
                    sliderValuePrettyPrint = Float::prettyPrintOnePlace,
                    valueRange = MIN_DIVIDER_THICKNESS..MAX_DIVIDER_THICKNESS,
                    onValueChangeFinished = { newSizeFactor ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(dividerThickness = newSizeFactor)
                            )
                        }
                    }
                )
            }
        }



    }
}

