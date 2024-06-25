package de.oljg.glac.feature_alarm.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.REPEAT_MODES
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import de.oljg.glac.feature_clock.ui.settings.components.common.DropDownSelector
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING

@Composable
fun RepetitionSelector(
    label: String,
    selectedRepetition: Repetition,
    startPadding: Dp = 0.dp,
    endPadding: Dp = 0.dp,
    onNewRepeatModeSelected: (String) -> Unit
) {
    DropDownSelector(
        topPadding = DIALOG_DEFAULT_PADDING,
        startPadding = startPadding,
        endPadding = endPadding,
        type = Repetition::class,
        label = label,
        selectedValue = selectedRepetition.name,
        onNewValueSelected = onNewRepeatModeSelected,
        values = REPEAT_MODES
    )
}
