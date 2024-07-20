package de.oljg.glac.feature_clock.ui.settings.components.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.ExpandableSection
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.clock.utils.ClockCharType
import de.oljg.glac.feature_clock.ui.clock.utils.ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.feature_clock.ui.clock.utils.ClockDefaults.DEFAULT_DIGIT_SIZE_FACTOR
import de.oljg.glac.feature_clock.ui.settings.components.character.ClockCharTypeSelector
import de.oljg.glac.feature_clock.ui.settings.components.character.FontSelector
import de.oljg.glac.feature_clock.ui.settings.components.character.SevenSegmentSelector
import de.oljg.glac.feature_clock.ui.settings.components.common.SettingsSlider
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.prettyPrintPercentage

@Composable
fun ClockCharacterSettings(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )

    ExpandableSection(
        sectionTitle = stringResource(R.string.characters),
        expanded = clockSettings.clockSettingsSectionClockCharIsExpanded,
        onExpandedChange = { expanded ->
            onEvent(ClockSettingsEvent.UpdateClockSettingsSectionClockCharIsExpanded(expanded))
        }
    ) {
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))
        ClockCharTypeSelector(
            selectedClockCharType = clockTheme.clockCharType,
            onClockCharTypeSelected = { newClockCharType ->
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(clockCharType = newClockCharType)
                    )
                )
            }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
        Crossfade(
            targetState = clockTheme.clockCharType,
            animationSpec = TweenSpec(),
            label = "crossfade"
        ) { clockCharType ->
            when (clockCharType) {
                ClockCharType.FONT -> FontSelector(clockSettings, onEvent)
                ClockCharType.SEVEN_SEGMENT -> SevenSegmentSelector(clockSettings, onEvent)
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(
                top = DEFAULT_VERTICAL_SPACE / 2,
                bottom = DEFAULT_VERTICAL_SPACE
            )
        )
        SettingsSlider(
            label = stringResource(R.string.digit_size),
            value = clockTheme.digitSizeFactor,
            defaultValue = DEFAULT_DIGIT_SIZE_FACTOR,
            sliderValuePrettyPrintFun = Float::prettyPrintPercentage,
            onValueChangeFinished = { newSizeFactor ->
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(digitSizeFactor = newSizeFactor)
                    )
                )
            },
            onResetValue = {
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(digitSizeFactor = DEFAULT_DIGIT_SIZE_FACTOR)
                    )
                )
            }
        )

        AnimatedVisibility(visible = clockTheme.showDaytimeMarker) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.daytime_marker_size),
                    value = clockTheme.daytimeMarkerSizeFactor,
                    defaultValue = DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,
                    sliderValuePrettyPrintFun = Float::prettyPrintPercentage,
                    onValueChangeFinished = { newSizeFactor ->
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(daytimeMarkerSizeFactor = newSizeFactor)
                            )
                        )
                    },
                    onResetValue = {
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(
                                    daytimeMarkerSizeFactor = DEFAULT_DAYTIME_MARKER_SIZE_FACTOR)
                            )
                        )
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))
    }
}
