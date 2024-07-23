package de.oljg.glac.feature_clock.ui.settings.components.character

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.GlacSwitch
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentDefaults.DEFAULT_OUTLINE_SIZE
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentDefaults.MAX_STROKE_WIDTH
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentDefaults.MIN_STROKE_WIDTH
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentWeight
import de.oljg.glac.feature_clock.ui.clock.utils.isOutline
import de.oljg.glac.feature_clock.ui.settings.components.SettingsSlider
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.prettyPrintPixel

@Composable
fun SevenSegmentSelector(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )

    Column {
        SevenSegmentWeightSelector(
            label = stringResource(id = R.string.weight),
            selectedSevenSegmentWeight = clockTheme.sevenSegmentWeight,
            onNewSevenSegmentWeightSelected = { newSevenSegmentWeight ->
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(
                            sevenSegmentWeight = SevenSegmentWeight.valueOf(
                                newSevenSegmentWeight
                            )
                        )
                    )
                )
            }
        )
        SevenSegmentStyleSelector(
            label = stringResource(id = R.string.style),
            selectedSevenSegmentStyle = clockTheme.sevenSegmentStyle,
            onNewSevenSegmentStyleSelected = { newSevenSegmentStyle ->
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(
                            sevenSegmentStyle = SevenSegmentStyle.valueOf(
                                newSevenSegmentStyle
                            )
                        )
                    )
                )
            }
        )
        AnimatedVisibility(visible = clockTheme.sevenSegmentStyle.isOutline()) {
            Column {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        top = DEFAULT_VERTICAL_SPACE / 2,
                        bottom = DEFAULT_VERTICAL_SPACE
                    )
                )
                SettingsSlider(
                    label = stringResource(R.string.outline_size),
                    value = clockTheme.sevenSegmentOutlineSize,
                    defaultValue = DEFAULT_OUTLINE_SIZE,
                    sliderValuePrettyPrintFun = Float::prettyPrintPixel,
                    onValueChangeFinished = { newValue ->
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(sevenSegmentOutlineSize = newValue)
                            )
                        )
                    },
                    onResetValue = {
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(sevenSegmentOutlineSize = DEFAULT_OUTLINE_SIZE)
                            )
                        )
                    },
                    valueRange = MIN_STROKE_WIDTH..MAX_STROKE_WIDTH
                )
                Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(
                top = DEFAULT_VERTICAL_SPACE / 2,
                bottom = DEFAULT_VERTICAL_SPACE
            )
        )
        GlacSwitch(
            label = stringResource(R.string.off_segments),
            checked = clockTheme.drawOffSegments,
            onCheckedChange = { newValue ->
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(drawOffSegments = newValue)
                    )
                )
            }
        )
    }
}

