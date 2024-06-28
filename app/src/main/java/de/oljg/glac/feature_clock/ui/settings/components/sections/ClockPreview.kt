package de.oljg.glac.feature_clock.ui.settings.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.ExpandableSection
import de.oljg.glac.core.util.screenDetails
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.clock.DigitalAlarmClockScreen
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.PREVIEW_SIZE_FACTOR

@Composable
fun ClockPreview(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    ExpandableSection(
        sectionTitle = stringResource(R.string.clock_preview),
        expanded = clockSettings.clockSettingsSectionPreviewIsExpanded,
        onExpandedChange = { isExpanded ->
            onEvent(ClockSettingsEvent.UpdateClockSettingsSectionPreviewIsExpanded(isExpanded))
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(
                        DpSize(
                            screenDetails().screenWidth * PREVIEW_SIZE_FACTOR,
                            screenDetails().screenHeight * PREVIEW_SIZE_FACTOR
                        )
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                DigitalAlarmClockScreen(
                    clockSettings = clockSettings,
                    previewMode = true
                )
            }
        }
    }
}
