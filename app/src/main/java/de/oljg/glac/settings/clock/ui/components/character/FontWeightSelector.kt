package de.oljg.glac.settings.clock.ui.components.character

import androidx.compose.runtime.Composable
import de.oljg.glac.core.util.FontWeight
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.FONT_WEIGHTS


@Composable
fun FontWeightSelector(
    label: String,
    selectedFontWeight: FontWeight,
    onNewFontWeightSelected: (String) -> Unit
) {
    DropDownSelector(
        type = FontWeight::class,
        label = label,
        selectedValue = selectedFontWeight.name,
        onNewValueSelected = onNewFontWeightSelected,
        values = FONT_WEIGHTS
    )
}

