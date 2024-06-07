package de.oljg.glac.alarms.ui.components

import androidx.compose.runtime.Composable
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.REPEAT_MODES
import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING

@Composable
fun RepetitionSelector(
    label: String,
    selectedRepetition: Repetition,
    onNewRepeatModeSelected: (String) -> Unit
) {
    DropDownSelector(
        topPadding = DIALOG_DEFAULT_PADDING,
        startPadding = DIALOG_DEFAULT_PADDING / 3,
        endPadding = DIALOG_DEFAULT_PADDING,
        type = Repetition::class,
        label = label,
        selectedValue = selectedRepetition.name,
        onNewValueSelected = onNewRepeatModeSelected,
        values = REPEAT_MODES
    )
}
