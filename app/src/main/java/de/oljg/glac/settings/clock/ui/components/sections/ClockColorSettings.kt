package de.oljg.glac.settings.clock.ui.components.sections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.ColorSelector
import de.oljg.glac.settings.clock.ui.components.ColorsPerCharSelector
import de.oljg.glac.settings.clock.ui.components.common.SettingsSection
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockColorSettings(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value
    val defaultCharColor = MaterialTheme.colorScheme.onSurface

    SettingsSection(
        sectionTitle = stringResource(R.string.colors),
        expanded = clockSettings.clockSettingsSectionColorsIsExpanded,
        onExpandedChange = { expanded ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        clockSettingsSectionColorsIsExpanded = expanded
                    )
                )
            }
        }
    ) {
        ColorSelector(
            title = stringResource(id = R.string.characters),
            color = Color(clockSettings.charColor ?: defaultCharColor.toArgb()),
        ) { newColor ->
            coroutineScope.launch {
                viewModel.updateClockSettings(clockSettings.copy(charColor = newColor.toArgb()))
            }
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(DEFAULT_VERTICAL_SPACE / 2))
        ColorSelector(
            title = stringResource(R.string.dividers),
            color = Color(color =
                clockSettings.dividerColor ?:
                clockSettings.charColor ?:
                defaultCharColor.toArgb()),
        ) { newColor ->
            coroutineScope.launch {
                viewModel.updateClockSettings(clockSettings.copy(dividerColor = newColor.toArgb()))
            }
        }
        Divider(modifier = Modifier.padding(top = DEFAULT_VERTICAL_SPACE))
        ColorsPerCharSelector()
    }
}
