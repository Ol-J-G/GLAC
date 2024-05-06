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
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MAX_DASH_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MAX_DASH_DOTTED_PART_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MAX_DIVIDER_THICKNESS
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MIN_DASH_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MIN_DASH_DOTTED_PART_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MIN_DIVIDER_THICKNESS
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.isLineBased
import de.oljg.glac.clock.digital.ui.utils.isNeitherNoneNorChar
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.DividerStyleSelector
import de.oljg.glac.settings.clock.ui.components.common.SettingsSection
import de.oljg.glac.settings.clock.ui.components.common.SettingsSlider
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.cutOffDecimalPlaces
import de.oljg.glac.settings.clock.ui.utils.prettyPrintPercentage
import de.oljg.glac.settings.clock.ui.utils.prettyPrintPixel
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
            visible = DividerStyle.valueOf(clockSettings.dividerStyle).isNeitherNoneNorChar()
        ) {
            Column {
                Divider(modifier = Modifier.padding(vertical = SettingsDefaults.DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.thickness),
                    value = clockSettings.dividerThickness.toFloat(),
                    sliderValuePrettyPrint = Float::prettyPrintPixel,
                    valueRange = MIN_DIVIDER_THICKNESS.toFloat()..MAX_DIVIDER_THICKNESS.toFloat(),
                    onValueChangeFinished = { newSizeFactor ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(dividerThickness = newSizeFactor.toInt())
                            )
                        }
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = DividerStyle.valueOf(clockSettings.dividerStyle).isLineBased()
        ) {
            Column {
                Divider(modifier = Modifier.padding(vertical = SettingsDefaults.DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.length),
                    value = clockSettings.dividerLengthPercentage,
                    sliderValuePrettyPrint = Float::prettyPrintPercentage,
                    onValueChangeFinished = { newLength ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(dividerLengthPercentage = newLength)
                            )
                        }
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = DividerStyle.valueOf(clockSettings.dividerStyle) == DividerStyle.DASHED_LINE
        ) {
            Column {
                Divider(modifier = Modifier.padding(vertical = SettingsDefaults.DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.dashes),
                    value = clockSettings.dividerDashCount.toFloat(),
                    sliderValuePrettyPrint = Float::cutOffDecimalPlaces,
                    valueRange = MIN_DASH_COUNT.toFloat()..MAX_DASH_COUNT.toFloat(),
                    onValueChangeFinished = { newCount ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(dividerDashCount = newCount.toInt())
                            )
                        }
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = DividerStyle.valueOf(clockSettings.dividerStyle) == DividerStyle.DASHDOTTED_LINE
        ) {
            Column {
                Divider(modifier = Modifier.padding(vertical = SettingsDefaults.DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.dots),
                    value = clockSettings.dividerDashDottedPartCount.toFloat(),
                    sliderValuePrettyPrint = Float::cutOffDecimalPlaces,
                    valueRange =
                    MIN_DASH_DOTTED_PART_COUNT.toFloat()..MAX_DASH_DOTTED_PART_COUNT.toFloat(),
                    onValueChangeFinished = { newCount ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(dividerDashDottedPartCount = newCount.toInt())
                            )
                        }
                    }
                )
            }
        }



    }
}

