package de.oljg.glac.settings.clock.ui


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.settings.clock.ui.components.ClockCharTypeSelector
import de.oljg.glac.settings.clock.ui.components.ClockPreview
import de.oljg.glac.settings.clock.ui.components.FontSelector
import de.oljg.glac.settings.clock.ui.components.SettingsSection
import de.oljg.glac.settings.clock.ui.components.SettingsSwitch
import de.oljg.glac.settings.clock.ui.components.SevenSegmentStyleSelector
import de.oljg.glac.settings.clock.ui.components.SevenSegmentWeightSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_SCREEN_HORIZONTAL_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_SCREEN_VERTICAL_PADDING
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockSettingsScreen(
    viewModel: ClockSettingsViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column( // outer column
            modifier = Modifier
                .padding(
                    horizontal = SETTINGS_SCREEN_HORIZONTAL_PADDING,
                    vertical = SETTINGS_SCREEN_VERTICAL_PADDING
                ),
            verticalArrangement = Arrangement.Top
        ) {
                SettingsSection(
                    sectionTitle = stringResource(R.string.clock_preview),
                    expanded = clockSettings.clockSettingsSectionPreviewIsExpanded,
                    onExpandedChange = { isExpanded ->
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(
                                    clockSettingsSectionPreviewIsExpanded = isExpanded
                                )
                            )
                        }
                    }
                ) {
                    ClockPreview()
                }

            Row(modifier = Modifier.fillMaxWidth()) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            Column( // inner scrollable column, actual settings
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
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
                        label = stringResource(R.string.show_seconds),
                        checked = clockSettings.showSeconds,
                        onCheckedChange = { newValue ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(clockSettings.copy(showSeconds = newValue))
                            }
                        }
                    )
                    SettingsSwitch(
                        label = stringResource(R.string.show_daytime_marker),
                        checked = clockSettings.showDaytimeMarker,
                        onCheckedChange = { newValue ->
                            coroutineScope.launch {
                                viewModel.updateClockSettings(clockSettings.copy(showDaytimeMarker = newValue))
                            }
                        }
                    )
                }

                SettingsSection(
                    sectionTitle = stringResource(R.string.clock_characters),
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
                    //TODO: introduce ClockChararcterSettings()
                    ClockCharTypeSelector(
                        label = stringResource(R.string.type),
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
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Crossfade(
                        targetState = ClockCharType.valueOf(clockSettings.selectedClockCharType),
                        animationSpec = TweenSpec(),
                        label = "crossfade"
                    ) { clockCharType ->
                        when(clockCharType) {
                            ClockCharType.FONT -> {
                                FontSelector(
                                    selectedFontFamily = clockSettings.fontName,
                                    onNewFontFamilySelected = { newFontName ->
                                        coroutineScope.launch {
                                            viewModel.updateClockSettings(
                                                clockSettings.copy(fontName = newFontName))
                                        }
                                    },
                                    onNewFontFamilyImported = { newFontUri ->
                                        coroutineScope.launch {
                                            viewModel.updateClockSettings(
                                                clockSettings.copy(fontName = newFontUri))
                                        }
                                    },
                                    selectedFontWeight = clockSettings.fontWeight,
                                    onNewFontWeightSelected = { newFontWeight ->
                                        coroutineScope.launch {
                                            viewModel.updateClockSettings(
                                                clockSettings.copy(fontWeight = newFontWeight))
                                        }
                                    },
                                    selectedFontStyle = clockSettings.fontStyle,
                                    onNewFontStyleSelected = { newFontStyle ->
                                        coroutineScope.launch {
                                            viewModel.updateClockSettings(
                                                clockSettings.copy(fontStyle = newFontStyle))
                                        }
                                    }
                                )
                            }
                            ClockCharType.SEVEN_SEGMENT -> {
                                Column {//TODO: introduce SevenSegmentSelector
                                    SevenSegmentWeightSelector(
                                        label = "${stringResource(id = R.string.weight)}:",
                                        selectedSevenSegmentWeight = clockSettings.sevenSegmentWeight,
                                        onNewSevenSegmentWeightSelected = { newSevenSegmentWeight ->
                                            coroutineScope.launch {
                                                viewModel.updateClockSettings(
                                                    clockSettings.copy(
                                                        sevenSegmentWeight = newSevenSegmentWeight
                                                    )
                                                )
                                            }
                                        }
                                    )
                                    SevenSegmentStyleSelector(
                                        label = "${stringResource(id = R.string.style)}:    ",
                                        selectedSevenSegmentStyle = clockSettings.sevenSegmentStyle,
                                        onNewSevenSegmentStyleSelected = { newSevenSegmentStyle ->
                                            coroutineScope.launch {
                                                viewModel.updateClockSettings(
                                                    clockSettings.copy(
                                                        sevenSegmentStyle = newSevenSegmentStyle
                                                    )
                                                )
                                            }
                                        }
                                    )

                                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                                    SettingsSwitch(
                                        label = stringResource(R.string.off_segments),
                                        checked = clockSettings.drawOffSegments,
                                        onCheckedChange = { newValue ->
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

                    }



                }

            }
        }


    }
}
