package de.oljg.glac.feature_clock.ui.settings.components.sections

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.ExpandableSection
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.clock.utils.ClockCharType
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_DASH_COUNT
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_DASH_DOTTED_PART_COUNT
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_LENGTH_PERCENTAGE
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_ROTATE_ANGLE
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_THICKNESS
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.MAX_DASH_COUNT
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.MAX_DASH_DOTTED_PART_COUNT
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.MAX_DIVIDER_ROTATE_ANGLE
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.MAX_DIVIDER_THICKNESS
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.MIN_DASH_COUNT
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.MIN_DASH_DOTTED_PART_COUNT
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.MIN_DIVIDER_ROTATE_ANGLE
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.MIN_DIVIDER_THICKNESS
import de.oljg.glac.feature_clock.ui.clock.utils.DividerLineEnd
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.feature_clock.ui.clock.utils.isLineBased
import de.oljg.glac.feature_clock.ui.clock.utils.isLineOrDashedLine
import de.oljg.glac.feature_clock.ui.clock.utils.isNeitherNoneNorChar
import de.oljg.glac.feature_clock.ui.clock.utils.isRotatable
import de.oljg.glac.feature_clock.ui.settings.components.SettingsSlider
import de.oljg.glac.feature_clock.ui.settings.components.divider.CharDividerPortraitWarning
import de.oljg.glac.feature_clock.ui.settings.components.divider.CharDividerSevenSegmentWarning
import de.oljg.glac.feature_clock.ui.settings.components.divider.ColonDividerOptionsSelector
import de.oljg.glac.feature_clock.ui.settings.components.divider.DividerCharsSelector
import de.oljg.glac.feature_clock.ui.settings.components.divider.DividerLineEndSelector
import de.oljg.glac.feature_clock.ui.settings.components.divider.DividerStyleSelector
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.cutOffDecimalPlaces
import de.oljg.glac.feature_clock.ui.settings.utils.isSevenSegmentItalicOrReverseItalic
import de.oljg.glac.feature_clock.ui.settings.utils.prettyPrintAngle
import de.oljg.glac.feature_clock.ui.settings.utils.prettyPrintPercentage
import de.oljg.glac.feature_clock.ui.settings.utils.prettyPrintPixel

@Composable
fun ClockDividerSettings(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )

    ExpandableSection(
        sectionTitle = stringResource(R.string.divider),
        expanded = clockSettings.clockSettingsSectionDividerIsExpanded,
        onExpandedChange = { expanded ->
            onEvent(ClockSettingsEvent.UpdateClockSettingsSectionDividerIsExpanded(expanded))
        }
    ) {
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        DividerStyleSelector(
            label = stringResource(R.string.style),
            selectedDividerStyle = clockTheme.dividerStyle,
            onNewDividerStyleSelected = { newDividerStyle ->
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(dividerStyle = DividerStyle.valueOf(newDividerStyle))
                    )
                )
            }
        )

        AnimatedVisibility(visible = clockTheme.dividerStyle.isNeitherNoneNorChar()) {
            Column {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        top = DEFAULT_VERTICAL_SPACE / 2,
                        bottom = DEFAULT_VERTICAL_SPACE
                    )
                )
                SettingsSlider(
                    label = stringResource(R.string.thickness),
                    value = clockTheme.dividerThickness.toFloat(),
                    defaultValue = DEFAULT_THICKNESS.toFloat(),
                    sliderValuePrettyPrintFun = Float::prettyPrintPixel,
                    valueRange = MIN_DIVIDER_THICKNESS.toFloat()..MAX_DIVIDER_THICKNESS.toFloat(),
                    onValueChangeFinished = { newSizeFactor ->
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(dividerThickness = newSizeFactor.toInt())
                            )
                        )
                    },
                    onResetValue = {
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(dividerThickness = DEFAULT_THICKNESS)
                            )
                        )
                    }
                )
            }
        }

        AnimatedVisibility(visible = clockTheme.dividerStyle.isLineBased()) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.length),
                    value = clockTheme.dividerLengthPercentage,
                    defaultValue = DEFAULT_LENGTH_PERCENTAGE,
                    sliderValuePrettyPrintFun = Float::prettyPrintPercentage,
                    onValueChangeFinished = { newLength ->
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(dividerLengthPercentage = newLength)
                            )
                        )
                    },
                    onResetValue = {
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(dividerLengthPercentage = DEFAULT_LENGTH_PERCENTAGE)
                            )
                        )
                    }
                )
            }
        }

        AnimatedVisibility(visible = clockTheme.dividerStyle == DividerStyle.DASHED_LINE) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.dashes),
                    value = clockTheme.dividerDashCount.toFloat(),
                    defaultValue = DEFAULT_DASH_COUNT.toFloat(),
                    sliderValuePrettyPrintFun = Float::cutOffDecimalPlaces,
                    valueRange = MIN_DASH_COUNT.toFloat()..MAX_DASH_COUNT.toFloat(),
                    onValueChangeFinished = { newCount ->
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(dividerDashCount = newCount.toInt())
                            )
                        )
                    },
                    onResetValue = {
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(dividerDashCount = DEFAULT_DASH_COUNT)
                            )
                        )
                    }
                )
            }
        }

        AnimatedVisibility(visible = clockTheme.dividerStyle == DividerStyle.DASHDOTTED_LINE) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.dots),
                    value = clockTheme.dividerDashDottedPartCount.toFloat(),
                    defaultValue = DEFAULT_DASH_DOTTED_PART_COUNT.toFloat(),
                    sliderValuePrettyPrintFun = Float::cutOffDecimalPlaces,
                    valueRange = MIN_DASH_DOTTED_PART_COUNT
                        .toFloat()..MAX_DASH_DOTTED_PART_COUNT.toFloat(),
                    onValueChangeFinished = { newCount ->
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(dividerDashDottedPartCount = newCount.toInt())
                            )
                        )
                    },
                    onResetValue = {
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(
                                    dividerDashDottedPartCount = DEFAULT_DASH_DOTTED_PART_COUNT
                                )
                            )
                        )
                    }
                )
            }
        }

        AnimatedVisibility(visible = clockTheme.dividerStyle.isLineOrDashedLine()) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                DividerLineEndSelector(
                    label = stringResource(R.string.line_end),
                    selectedDividerLineEnd = clockTheme.dividerLineEnd,
                    onNewDividerLineEndSelected = { newLineEnd ->
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(
                                    dividerLineEnd = DividerLineEnd.valueOf(newLineEnd)
                                )
                            )
                        )
                    }
                )
                Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
            }
        }

        AnimatedVisibility(
            visible = clockTheme.dividerStyle.isRotatable()
                    && LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                    && !isSevenSegmentItalicOrReverseItalic(
                clockTheme.clockCharType,
                clockTheme.sevenSegmentStyle
            )
        ) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.angle),
                    value = clockTheme.dividerRotateAngle,
                    defaultValue = DEFAULT_ROTATE_ANGLE,
                    sliderValuePrettyPrintFun = Float::prettyPrintAngle,
                    valueRange = MIN_DIVIDER_ROTATE_ANGLE..MAX_DIVIDER_ROTATE_ANGLE,
                    onValueChangeFinished = { newAngle ->
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(dividerRotateAngle = newAngle)
                            )
                        )
                    },
                    onResetValue = {
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(dividerRotateAngle = DEFAULT_ROTATE_ANGLE)
                            )
                        )
                    }
                )
            }
        }

        AnimatedVisibility(visible = clockTheme.dividerStyle == DividerStyle.COLON) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                ColonDividerOptionsSelector(clockSettings, onEvent)
                Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
            }
        }

        AnimatedVisibility(
            visible = clockTheme.dividerStyle == DividerStyle.CHAR
                    && LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                    && clockTheme.clockCharType == ClockCharType.FONT
        ) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                DividerCharsSelector(clockSettings, onEvent)
                HorizontalDivider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE / 2))
            }
        }

        AnimatedVisibility(
            visible = clockTheme.dividerStyle == DividerStyle.CHAR
                    && LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                    && clockTheme.clockCharType == ClockCharType.SEVEN_SEGMENT
        ) {
            CharDividerSevenSegmentWarning()
        }

        AnimatedVisibility(
            visible = clockTheme.dividerStyle == DividerStyle.CHAR
                    && LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
        ) {
            CharDividerPortraitWarning()
        }
    }
}
