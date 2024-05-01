package de.oljg.glac.settings.clock.ui.components

import androidx.compose.runtime.Composable
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.FONT_WEIGHTS
import de.oljg.glac.settings.clock.ui.utils.prettyPrintEnumName


@Composable
fun FontWeightSelector(
    label: String,
    selectedFontWeight: String,
    onNewFontWeightSelected: (String) -> Unit
) {
    DropDownSelector(
        label = label,
        selectedValue = selectedFontWeight,
        onNewValueSelected = onNewFontWeightSelected,
        values = FONT_WEIGHTS,
        prettyPrintValue = String::prettyPrintEnumName
    )
}

