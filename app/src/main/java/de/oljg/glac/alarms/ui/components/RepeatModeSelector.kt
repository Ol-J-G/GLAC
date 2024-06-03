package de.oljg.glac.alarms.ui.components

import androidx.compose.runtime.Composable
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.REPEAT_MODES
import de.oljg.glac.alarms.ui.utils.RepeatMode
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING

@Composable
fun RepeatModeSelector(
    label: String,
    selectedRepeatMode: RepeatMode,
    onNewRepeatModeSelected: (String) -> Unit
) {
    DropDownSelector(
        topPadding = DIALOG_DEFAULT_PADDING,
        startPadding = DIALOG_DEFAULT_PADDING / 3,
        endPadding = DIALOG_DEFAULT_PADDING,
        type = RepeatMode::class,
        label = label,
        selectedValue = selectedRepeatMode.name,
        onNewValueSelected = onNewRepeatModeSelected,
        values = REPEAT_MODES
    )
}
