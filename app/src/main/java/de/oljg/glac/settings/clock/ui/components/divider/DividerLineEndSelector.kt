package de.oljg.glac.settings.clock.ui.components.divider

import androidx.compose.runtime.Composable
import de.oljg.glac.clock.digital.ui.utils.DividerLineEnd
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults

@Composable
fun DividerLineEndSelector(
    label: String,
    selectedDividerLineEnd: DividerLineEnd,
    onNewDividerLineEndSelected: (String) -> Unit
) {
    DropDownSelector(
        type = DividerLineEnd::class,
        startPadding = SettingsDefaults.DIALOG_DEFAULT_PADDING / 3,
        label = label,
        selectedValue = selectedDividerLineEnd.name,
        onNewValueSelected = onNewDividerLineEndSelected,
        values = SettingsDefaults.DIVIDER_LINE_ENDS
    )
}
