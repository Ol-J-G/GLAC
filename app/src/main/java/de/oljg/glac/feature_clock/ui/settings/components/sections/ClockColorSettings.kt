package de.oljg.glac.feature_clock.ui.settings.components.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.ExpandableSection
import de.oljg.glac.core.util.defaultBackgroundColor
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.clock.utils.ClockCharType
import de.oljg.glac.feature_clock.ui.settings.components.color.ColorSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.ColorsPerCharSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.ColorsPerClockPartSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.SegmentColorsSelector
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE

@Composable
fun ClockColorSettings(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )
    val defaultCharColor = defaultColor()
    val defaultBackgroundColor = defaultBackgroundColor()

    ExpandableSection(
        sectionTitle = stringResource(R.string.colors),
        expanded = clockSettings.clockSettingsSectionColorsIsExpanded,
        onExpandedChange = { expanded ->
            onEvent(ClockSettingsEvent.UpdateClockSettingsSectionColorsIsExpanded(expanded))
        }
    ) {
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        ColorSelector(
            title = stringResource(id = R.string.characters),
            color = clockTheme.charColor ?: defaultCharColor,
            defaultColor = defaultCharColor,
            onResetColor = {
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(charColor = null)
                    )
                )
            }
        ) { selectedColor ->
            onEvent(
                ClockSettingsEvent.UpdateThemes(
                    clockThemeName,
                    clockTheme.copy(charColor = selectedColor)
                )
            )
        }
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        ColorSelector(
            title = stringResource(R.string.background),
            color = clockTheme.backgroundColor ?: defaultBackgroundColor,
            defaultColor = defaultBackgroundColor,
            onResetColor = {
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(backgroundColor = null)
                    )
                )
            },
        ) { selectedColor ->
            onEvent(
                ClockSettingsEvent.UpdateThemes(
                    clockThemeName,
                    clockTheme.copy(backgroundColor = selectedColor)
                )
            )
        }
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        ColorSelector(
            title = stringResource(R.string.dividers),
            color = clockTheme.dividerColor ?: clockTheme.charColor ?: defaultCharColor,
            defaultColor = clockTheme.charColor ?: defaultCharColor,
            onResetColor = {
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(dividerColor = null)
                    )
                )
            },
        ) { selectedColor ->
            onEvent(
                ClockSettingsEvent.UpdateThemes(
                    clockThemeName,
                    clockTheme.copy(dividerColor = selectedColor)
                )
            )
        }
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))
        ColorsPerCharSelector(clockSettings, onEvent)

        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))
        ColorsPerClockPartSelector(clockSettings, onEvent)

        AnimatedVisibility(visible = clockTheme.clockCharType == ClockCharType.SEVEN_SEGMENT) {
            Column {
                Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))
                SegmentColorsSelector(clockSettings, onEvent)
            }
        }
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
    }
}
