package de.oljg.glac.settings.clock.ui.components

import androidx.compose.runtime.Composable
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.ALL_FONT_WEIGHTS


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
        values = ALL_FONT_WEIGHTS
    )
}

