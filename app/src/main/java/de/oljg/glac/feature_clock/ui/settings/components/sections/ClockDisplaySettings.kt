package de.oljg.glac.feature_clock.ui.settings.components.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.ExpandableSection
import de.oljg.glac.core.ui.components.GlacSwitch
import de.oljg.glac.core.util.TestTags.CLOCK_SETTINGS_DISPLAY_EXPANDABLE_SECTION
import de.oljg.glac.core.util.TestTags.SHOW_SECONDS_SWITCH
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE

@Composable
fun ClockDisplaySettings(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )

    ExpandableSection(
        sectionTitle = stringResource(R.string.display),
        expanded = clockSettings.clockSettingsSectionDisplayIsExpanded,
        onExpandedChange = { expanded ->
            onEvent(ClockSettingsEvent.UpdateClockSettingsSectionDisplayIsExpanded(expanded))
        },
        testTag = CLOCK_SETTINGS_DISPLAY_EXPANDABLE_SECTION
    ) {
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        GlacSwitch(
            label = stringResource(R.string.seconds),
            checked = clockTheme.showSeconds,
            onCheckedChange = { newValue ->
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(showSeconds = newValue)
                    )
                )
            },
            testTag = SHOW_SECONDS_SWITCH
        )
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        GlacSwitch(
            label = stringResource(R.string.daytime_marker),
            checked = clockTheme.showDaytimeMarker,
            onCheckedChange = { newValue ->
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(showDaytimeMarker = newValue)
                    )
                )
            }
        )
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
    }
}
