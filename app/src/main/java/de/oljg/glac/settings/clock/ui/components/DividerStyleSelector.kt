package de.oljg.glac.settings.clock.ui.components

import androidx.compose.runtime.Composable
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.prettyPrintEnumName

@Composable
fun DividerStyleSelector(
    label: String,
    selectedDividerStyle: String,
    onNewDividerStyleSelected: (String) -> Unit
) {
    DropDownSelector(
        label = label,
        selectedValue = selectedDividerStyle,
        onNewValueSelected = onNewDividerStyleSelected,
        values = SettingsDefaults.DIVIDER_STYLES,
        prettyPrintValue = String::prettyPrintEnumName
    )
}
