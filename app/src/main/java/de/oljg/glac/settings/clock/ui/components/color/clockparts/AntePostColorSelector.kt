package de.oljg.glac.settings.clock.ui.components.color.clockparts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.clock.data.ClockTheme
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.color.ColorSelector
import kotlinx.coroutines.launch

@Composable
fun AntePostColorSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )
    val defaultCharColor = defaultColor()
    val currentClockPartsColors = clockTheme.clockPartsColors

    ColorSelector(
        title = stringResource(R.string.ante_shortened) + "/" + stringResource(R.string.post_shortened),
        color = clockTheme.clockPartsColors.daytimeMarker.anteOrPost
            ?: clockTheme.charColor
            ?: defaultCharColor,
        defaultColor = clockTheme.charColor ?: defaultCharColor,
        onResetColor = {
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        themes = clockSettings.themes.put(
                            clockThemeName, clockTheme.copy(
                                clockPartsColors = currentClockPartsColors.copy(
                                    daytimeMarker = currentClockPartsColors.daytimeMarker.copy(
                                        anteOrPost = null
                                    )
                                )
                            )
                        )
                    )
                )
            }
        }
    ) { selectedColor ->
        coroutineScope.launch {
            viewModel.updateClockSettings(
                clockSettings.copy(
                    themes = clockSettings.themes.put(
                        clockThemeName, clockTheme.copy(
                            clockPartsColors = currentClockPartsColors.copy(
                                daytimeMarker = currentClockPartsColors.daytimeMarker.copy(
                                    anteOrPost = selectedColor
                                )
                            )
                        )
                    )
                )
            )
        }
    }
}

