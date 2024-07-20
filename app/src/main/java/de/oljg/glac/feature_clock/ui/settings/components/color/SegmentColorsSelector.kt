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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.clock.utils.Segment
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_BORDER_WIDTH
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_ROUNDED_CORNER_SIZE
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.EDGE_PADDING
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.MULTI_COLOR_SELECTOR_PADDING
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.SETTINGS_SECTION_HEIGHT

@Composable
fun SegmentColorsSelector(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )
    val defaultCharColor = defaultColor()

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
                    text = stringResource(R.string.segment_colors)
                )
                Switch(
                    checked = clockTheme.useSegmentColors,
                    onCheckedChange = {
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(useSegmentColors = !clockTheme.useSegmentColors)
                            )
                        )
                    }
                )
            }

            AnimatedVisibility(visible = clockTheme.useSegmentColors) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(
                        vertical = DEFAULT_VERTICAL_SPACE / 2)
                    )
                    Segment.entries.forEach { segment ->
                        ColorSelector(
                            title = when (segment) {
                                Segment.TOP -> stringResource(R.string.top)
                                Segment.CENTER -> stringResource(R.string.center)
                                Segment.BOTTOM -> stringResource(R.string.bottom)
                                Segment.TOP_LEFT -> stringResource(R.string.top_l)
                                Segment.TOP_RIGHT -> stringResource(R.string.top_r)
                                Segment.BOTTOM_LEFT -> stringResource(R.string.bottom_l)
                                Segment.BOTTOM_RIGHT -> stringResource(R.string.bottom_r)
                            },
                            color = clockTheme.segmentColors.getOrDefault(
                                key = segment,
                                defaultValue = clockTheme.charColor ?: defaultCharColor
                            ),
                            defaultColor = clockTheme.charColor ?: defaultCharColor,
                            onResetColor = {
                                onEvent(
                                    ClockSettingsEvent.UpdateThemes(
                                        clockThemeName,
                                        clockTheme.copy(
                                            segmentColors = clockTheme.segmentColors.remove(segment)
                                        )
                                    )
                                )
                            }
                        ) { selectedColor ->
                            onEvent(
                                ClockSettingsEvent.UpdateThemes(
                                    clockThemeName,
                                    clockTheme.copy(
                                        segmentColors = clockTheme.segmentColors.put(
                                            segment,
                                            selectedColor
                                        )
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
