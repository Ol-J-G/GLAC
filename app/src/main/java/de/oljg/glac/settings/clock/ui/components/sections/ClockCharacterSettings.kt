package de.oljg.glac.settings.clock.ui.components.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_CLOCK_DIGIT_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.core.clock.data.ClockTheme
import de.oljg.glac.core.ui.components.SettingsSection
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.character.ClockCharTypeSelector
import de.oljg.glac.settings.clock.ui.components.character.FontSelector
import de.oljg.glac.settings.clock.ui.components.character.SevenSegmentSelector
import de.oljg.glac.settings.clock.ui.components.common.SettingsSlider
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.prettyPrintPercentage
import kotlinx.coroutines.launch

@Composable
fun ClockCharacterSettings(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )

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
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))
        ClockCharTypeSelector(
            selectedClockCharType = clockTheme.clockCharType,
            onClockCharTypeSelected = { newClockCharType ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            themes = clockSettings.themes.put(
                                clockThemeName, clockTheme.copy(clockCharType = newClockCharType))
                        )
                    )
                }
            }
        )
        Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
        Crossfade(
            targetState = clockTheme.clockCharType,
            animationSpec = TweenSpec(),
            label = "crossfade"
        ) { clockCharType ->
            when (clockCharType) {
                ClockCharType.FONT -> FontSelector()
                ClockCharType.SEVEN_SEGMENT -> SevenSegmentSelector()
            }
        }
        Divider(
            modifier = Modifier.padding(
                top = DEFAULT_VERTICAL_SPACE / 2,
                bottom = DEFAULT_VERTICAL_SPACE
            )
        )
        SettingsSlider(
            label =  "${stringResource(id = R.string.digit)} " +
                    stringResource(id = R.string.size),
            value = clockTheme.digitSizeFactor,
            defaultValue = DEFAULT_CLOCK_DIGIT_SIZE_FACTOR,
            sliderValuePrettyPrintFun = Float::prettyPrintPercentage,
            onValueChangeFinished = { newSizeFactor ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(themes = clockSettings.themes.put(
                            clockThemeName, clockTheme.copy(digitSizeFactor = newSizeFactor))
                        )
                    )
                }
            },
            onResetValue = {
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(themes = clockSettings.themes.put(
                            clockThemeName, clockTheme.copy(
                                digitSizeFactor = DEFAULT_CLOCK_DIGIT_SIZE_FACTOR))
                        )
                    )
                }
            }
        )

        AnimatedVisibility(visible = clockTheme.showDaytimeMarker) {
            Column {
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = "${stringResource(id = R.string.daytime_marker)} " +
                            stringResource(id = R.string.size),
                    value = clockTheme.daytimeMarkerSizeFactor,
                    defaultValue = DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,
                    sliderValuePrettyPrintFun = Float::prettyPrintPercentage,
                    onValueChangeFinished = { newSizeFactor ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    themes = clockSettings.themes.put(
                                        clockThemeName, clockTheme.copy(
                                            daytimeMarkerSizeFactor = newSizeFactor
                                        )
                                    )
                                )
                            )
                        }
                    },
                    onResetValue = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    themes = clockSettings.themes.put(
                                        clockThemeName, clockTheme.copy(
                                            daytimeMarkerSizeFactor = DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
                                        )
                                    )
                                )
                            )
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))
    }
}
