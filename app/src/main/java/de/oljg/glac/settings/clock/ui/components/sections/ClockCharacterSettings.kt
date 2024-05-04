package de.oljg.glac.settings.clock.ui.components.sections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_CLOCK_DIGIT_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.MIN_CLOCK_DIGIT_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.MIN_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.contains
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.ClockCharTypeSelector
import de.oljg.glac.settings.clock.ui.components.FontSelector
import de.oljg.glac.settings.clock.ui.components.common.SettingsSection
import de.oljg.glac.settings.clock.ui.components.SevenSegmentSelector
import de.oljg.glac.settings.clock.ui.components.common.SettingsSlider
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.prettyPrintPercentage
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockCharacterSettings(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value

    SettingsSection(
        sectionTitle = stringResource(R.string.characters),
        expanded = clockSettings.clockSettingsSectionClockCharIsExpanded,
        onExpandedChange = { expanded ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        clockSettingsSectionClockCharIsExpanded = expanded
                    )
                )
            }
        }
    ) {
        ClockCharTypeSelector(
            label = "${stringResource(R.string.type)}:",
            selectedClockCharType = clockSettings.selectedClockCharType,
            onClockCharTypeSelected = { newClockCharType ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            selectedClockCharType = newClockCharType
                        )
                    )
                }
            }
        )
        Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
        Crossfade(
            targetState = ClockCharType.valueOf(clockSettings.selectedClockCharType),
            animationSpec = TweenSpec(),
            label = "crossfade"
        ) { clockCharType ->
            when (clockCharType) {
                ClockCharType.FONT -> {
                    FontSelector(
                        selectedFontFamily = clockSettings.fontName,
                        onNewFontFamilySelected = { newFontName ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(
                                    clockSettings.copy(fontName = newFontName)
                                )
                            }
                        },
                        onNewFontFamilyImported = { newFontUri ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(
                                    clockSettings.copy(fontName = newFontUri)
                                )
                            }
                        },
                        selectedFontWeight = clockSettings.fontWeight,
                        onNewFontWeightSelected = { newFontWeight ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(
                                    clockSettings.copy(fontWeight = newFontWeight)
                                )
                            }
                        },
                        selectedFontStyle = clockSettings.fontStyle,
                        onNewFontStyleSelected = { newFontStyle ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(
                                    clockSettings.copy(fontStyle = newFontStyle)
                                )
                            }
                        }
                    )
                }

                ClockCharType.SEVEN_SEGMENT -> {
                    SevenSegmentSelector(
                        selectedSevenSegmentWeight = clockSettings.sevenSegmentWeight,
                        onNewSevenSegmentWeightSelected = { newSevenSegmentWeight ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(
                                    clockSettings.copy(
                                        sevenSegmentWeight = newSevenSegmentWeight
                                    )
                                )
                            }
                        },
                        selectedSevenSegmentStyle = clockSettings.sevenSegmentStyle,
                        onNewSevenSegmentStyleSelected = { newSevenSegmentStyle ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(
                                    clockSettings.copy(
                                        sevenSegmentStyle = newSevenSegmentStyle
                                    )
                                )
                            }
                        },
                        isOutlineStyleSelected = clockSettings.sevenSegmentStyle
                            .contains(SevenSegmentStyle.OUTLINE.name),
                        selectedOutlineSize = clockSettings.sevenSegmentOutlineSize,
                        onNewOutlineSizeSelected = { newValue ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(
                                    clockSettings.copy(
                                        sevenSegmentOutlineSize = newValue
                                    )
                                )
                            }
                        },
                        drawOffSegments = clockSettings.drawOffSegments,
                        onDrawOffSegmentsChanged = { newValue ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(
                                    clockSettings.copy(drawOffSegments = newValue)
                                )
                            }
                        }
                    )
                }
            }
        }
        Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
        SettingsSlider(
            label =  "${stringResource(id = R.string.digit)} " +
                    stringResource(id = R.string.size),
            value = clockSettings.digitSizeFactor,
            sliderValuePrettyPrint = Float::prettyPrintPercentage,
            valueRange = MIN_CLOCK_DIGIT_SIZE_FACTOR..DEFAULT_CLOCK_DIGIT_SIZE_FACTOR,
            onValueChangeFinished = { newSizeFactor ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(digitSizeFactor = newSizeFactor)
                    )
                }
            }
        )
        Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
        SettingsSlider(
            label =  "${stringResource(id = R.string.daytime_marker)} " +
                    stringResource(id = R.string.size),
            value = clockSettings.daytimeMarkerSizeFactor,
            sliderValuePrettyPrint = Float::prettyPrintPercentage,
            valueRange = MIN_DAYTIME_MARKER_SIZE_FACTOR..DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,
            onValueChangeFinished = { newSizeFactor ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(daytimeMarkerSizeFactor = newSizeFactor)
                    )
                }
            }
        )
        Spacer(modifier = Modifier.fillMaxWidth().height(DEFAULT_VERTICAL_SPACE))
    }
}
