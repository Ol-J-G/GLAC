package de.oljg.glac.settings.clock.ui.components.sections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.DigitalClockScreen
import de.oljg.glac.clock.digital.ui.utils.screenDetails
import de.oljg.glac.core.ui.components.SettingsSection
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.PREVIEW_SIZE_FACTOR
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockPreview(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    SettingsSection(
        sectionTitle = stringResource(R.string.clock_preview),
        expanded = clockSettings.clockSettingsSectionPreviewIsExpanded,
        onExpandedChange = { isExpanded ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        clockSettingsSectionPreviewIsExpanded = isExpanded
                    )
                )
            }
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
                DigitalClockScreen(previewMode = true)
            }
        }
    }
}
