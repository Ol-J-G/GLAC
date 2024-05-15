package de.oljg.glac.settings.clock.ui.components.sections

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DASH_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DASH_DOTTED_PART_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DIVIDER_LENGTH_FACTOR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DIVIDER_ROTATE_ANGLE
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DIVIDER_THICKNESS
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MAX_DASH_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MAX_DASH_DOTTED_PART_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MAX_DIVIDER_ROTATE_ANGLE
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MAX_DIVIDER_THICKNESS
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MIN_DASH_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MIN_DASH_DOTTED_PART_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MIN_DIVIDER_ROTATE_ANGLE
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.MIN_DIVIDER_THICKNESS
import de.oljg.glac.clock.digital.ui.utils.DividerLineEnd
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.isLineBased
import de.oljg.glac.clock.digital.ui.utils.isLineOrDashedLine
import de.oljg.glac.clock.digital.ui.utils.isNeitherNoneNorChar
import de.oljg.glac.clock.digital.ui.utils.isRotatable
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.common.SettingsSection
import de.oljg.glac.settings.clock.ui.components.common.SettingsSlider
import de.oljg.glac.settings.clock.ui.components.divider.CharDividerPortraitWarning
import de.oljg.glac.settings.clock.ui.components.divider.ColonDividerOptionsSelector
import de.oljg.glac.settings.clock.ui.components.divider.DividerCharsSelector
import de.oljg.glac.settings.clock.ui.components.divider.DividerLineEndSelector
import de.oljg.glac.settings.clock.ui.components.divider.DividerStyleSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.cutOffDecimalPlaces
import de.oljg.glac.settings.clock.ui.utils.isSevenSegmentItalicOrReverseItalic
import de.oljg.glac.settings.clock.ui.utils.prettyPrintAngle
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
                        clockSettings.copy(dividerStyle = DividerStyle.valueOf(newDividerStyle))
                    )
                }
            }
        )

        AnimatedVisibility(visible = clockSettings.dividerStyle.isNeitherNoneNorChar()) {
            Column {
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.thickness),
                    value = clockSettings.dividerThickness.toFloat(),
                    defaultValue = DEFAULT_DIVIDER_THICKNESS.toFloat(),
                    sliderValuePrettyPrintFun = Float::prettyPrintPixel,
                    valueRange = MIN_DIVIDER_THICKNESS.toFloat()..MAX_DIVIDER_THICKNESS.toFloat(),
                    onValueChangeFinished = { newSizeFactor ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(dividerThickness = newSizeFactor.toInt())
                            )
                        }
                    },
                    onResetValue = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(dividerThickness = DEFAULT_DIVIDER_THICKNESS)
                            )
                        }
                    }
                )
            }
        }

        AnimatedVisibility(visible = clockSettings.dividerStyle.isLineBased()) {
            Column {
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.length),
                    value = clockSettings.dividerLengthPercentage,
                    defaultValue = DEFAULT_DIVIDER_LENGTH_FACTOR,
                    sliderValuePrettyPrintFun = Float::prettyPrintPercentage,
                    onValueChangeFinished = { newLength ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(dividerLengthPercentage = newLength)
                            )
                        }
                    },
                    onResetValue = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    dividerLengthPercentage = DEFAULT_DIVIDER_LENGTH_FACTOR
                                )
                            )
                        }
                    }
                )
            }
        }

        AnimatedVisibility(visible = clockSettings.dividerStyle == DividerStyle.DASHED_LINE) {
            Column {
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.dashes),
                    value = clockSettings.dividerDashCount.toFloat(),
                    defaultValue = DEFAULT_DASH_COUNT.toFloat(),
                    sliderValuePrettyPrintFun = Float::cutOffDecimalPlaces,
                    valueRange = MIN_DASH_COUNT.toFloat()..MAX_DASH_COUNT.toFloat(),
                    onValueChangeFinished = { newCount ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(dividerDashCount = newCount.toInt())
                            )
                        }
                    },
                    onResetValue = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(dividerDashCount = DEFAULT_DASH_COUNT)
                            )
                        }
                    }
                )
            }
        }

        AnimatedVisibility(visible = clockSettings.dividerStyle == DividerStyle.DASHDOTTED_LINE) {
            Column {
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.dots),
                    value = clockSettings.dividerDashDottedPartCount.toFloat(),
                    defaultValue = DEFAULT_DASH_DOTTED_PART_COUNT.toFloat(),
                    sliderValuePrettyPrintFun = Float::cutOffDecimalPlaces,
                    valueRange =
                    MIN_DASH_DOTTED_PART_COUNT.toFloat()..MAX_DASH_DOTTED_PART_COUNT.toFloat(),
                    onValueChangeFinished = { newCount ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(dividerDashDottedPartCount = newCount.toInt())
                            )
                        }
                    },
                    onResetValue = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    dividerDashDottedPartCount = DEFAULT_DASH_DOTTED_PART_COUNT
                                )
                            )
                        }
                    }
                )
            }
        }

        AnimatedVisibility(visible = clockSettings.dividerStyle.isLineOrDashedLine()) {
            Column {
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                DividerLineEndSelector(
                    label = stringResource(R.string.line_end),
                    selectedDividerLineEnd = clockSettings.dividerLineEnd,
                    onNewDividerLineEndSelected = { newLineEnd ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    dividerLineEnd = DividerLineEnd.valueOf(newLineEnd))
                            )
                        }
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DEFAULT_VERTICAL_SPACE / 2)
                )
            }
        }

        AnimatedVisibility(
            visible = clockSettings.dividerStyle.isRotatable()
                    && LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                    && !isSevenSegmentItalicOrReverseItalic(
                clockSettings.clockCharType,
                clockSettings.sevenSegmentStyle
            )
        ) {
            Column {
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.angle),
                    value = clockSettings.dividerRotateAngle,
                    defaultValue = DEFAULT_DIVIDER_ROTATE_ANGLE,
                    sliderValuePrettyPrintFun = Float::prettyPrintAngle,
                    valueRange = MIN_DIVIDER_ROTATE_ANGLE..MAX_DIVIDER_ROTATE_ANGLE,
                    onValueChangeFinished = { newAngle ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(dividerRotateAngle = newAngle)
                            )
                        }
                    },
                    onResetValue = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    dividerRotateAngle = DEFAULT_DIVIDER_ROTATE_ANGLE
                                )
                            )
                        }
                    }
                )
            }
        }

        AnimatedVisibility(visible = clockSettings.dividerStyle == DividerStyle.COLON) {
            Column {
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                ColonDividerOptionsSelector()
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DEFAULT_VERTICAL_SPACE / 2)
                )
            }
        }

        AnimatedVisibility(
            visible = clockSettings.dividerStyle == DividerStyle.CHAR
                    && LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        ) {
            Column {
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                DividerCharsSelector()
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE / 2))
            }
        }

        AnimatedVisibility(
            visible = clockSettings.dividerStyle == DividerStyle.CHAR
                    && LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
        ) {
            CharDividerPortraitWarning()
        }
    }
}

