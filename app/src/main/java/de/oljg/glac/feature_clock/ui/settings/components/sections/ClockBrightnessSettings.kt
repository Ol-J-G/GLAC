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
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.utils.ClockSettingsDefaults.DEFAULT_CLOCK_BRIGHTNESS
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.settings.components.common.SettingsSlider
import de.oljg.glac.feature_clock.ui.settings.components.common.SettingsSwitch
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.prettyPrintPercentage

@Composable
fun ClockBrightnessSettings(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    ExpandableSection(
        sectionTitle = stringResource(R.string.brightness),
        expanded = clockSettings.clockSettingsSectionBrigntnessIsExpanded,
        onExpandedChange = { expanded ->
            onEvent(ClockSettingsEvent.UpdateClockSettingsSectionBrightnessIsExpanded(expanded))
        }
    ) {
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        SettingsSwitch(
            label = stringResource(R.string.override_system_brightness),
            checked = clockSettings.overrideSystemBrightness,
            onCheckedChange = { newValue ->
                onEvent(ClockSettingsEvent.UpdateOverrideSystemBrightness(newValue))
            }
        )

        AnimatedVisibility(visible = clockSettings.overrideSystemBrightness) {
            Column {
                Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
                SettingsSlider(
                    label = stringResource(R.string.clock_fullscreen_brightness),
                    value = clockSettings.clockBrightness,
                    defaultValue = DEFAULT_CLOCK_BRIGHTNESS,
                    sliderValuePrettyPrintFun = Float::prettyPrintPercentage,
                    onValueChangeFinished = { newBrightness ->
                        onEvent(ClockSettingsEvent.UpdateClockBrightness(newBrightness))
                    },
                    onResetValue = {
                        onEvent(ClockSettingsEvent.UpdateClockBrightness(DEFAULT_CLOCK_BRIGHTNESS))
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
    }
}
