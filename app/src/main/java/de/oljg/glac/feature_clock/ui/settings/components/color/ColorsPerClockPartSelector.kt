package de.oljg.glac.feature_clock.ui.settings.components.color

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.ui.ClockSettingsViewModel
import de.oljg.glac.feature_clock.ui.settings.components.color.clockparts.AntePostColorSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.clockparts.DaytimeMarkerDividerColorSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.clockparts.HoursMinutesDividerColorSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.clockparts.HoursOnesColorSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.clockparts.HoursTensColorSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.clockparts.MeridiemColorSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.clockparts.MinutesOnesColorSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.clockparts.MinutesSecondsDividerColorSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.clockparts.MinutesTensColorSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.clockparts.SecondsOnesColorSelector
import de.oljg.glac.feature_clock.ui.settings.components.color.clockparts.SecondsTensColorSelector
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_BORDER_WIDTH
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_ROUNDED_CORNER_SIZE
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.EDGE_PADDING
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.MULTI_COLOR_SELECTOR_PADDING
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.SETTINGS_SECTION_HEIGHT

@Composable
fun ColorsPerClockPartSelector(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )

    Surface(
        modifier = Modifier
            .border(
                width = DEFAULT_BORDER_WIDTH,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(DEFAULT_ROUNDED_CORNER_SIZE)
            )
            .padding(DEFAULT_VERTICAL_SPACE / 2),
    ) {
        Column(modifier = Modifier.padding(MULTI_COLOR_SELECTOR_PADDING)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SETTINGS_SECTION_HEIGHT),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(start = EDGE_PADDING / 2),
                    text = stringResource(R.string.color_per_clock_part)
                )
                Switch(
                    checked = clockTheme.setColorsPerClockPart,
                    onCheckedChange = {
                        viewModel.updateClockTheme(
                            clockSettings, clockThemeName,
                            clockTheme.copy(
                                setColorsPerClockPart = !clockTheme.setColorsPerClockPart
                            )
                        )
                    }
                )
            }

            AnimatedVisibility(visible = clockTheme.setColorsPerClockPart) {
                Column {
                    Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE / 2))
                    HoursTensColorSelector()
                    HoursOnesColorSelector()
                    MinutesTensColorSelector()
                    MinutesOnesColorSelector()
                    SecondsTensColorSelector()
                    SecondsOnesColorSelector()
                    AntePostColorSelector()
                    MeridiemColorSelector()
                    HoursMinutesDividerColorSelector()
                    MinutesSecondsDividerColorSelector()
                    DaytimeMarkerDividerColorSelector()
                }
            }
        }
    }
}
