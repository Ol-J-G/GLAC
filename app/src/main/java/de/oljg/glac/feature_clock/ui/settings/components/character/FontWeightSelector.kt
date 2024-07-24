package de.oljg.glac.feature_clock.ui.settings.components.character

import androidx.compose.runtime.Composable
import de.oljg.glac.core.ui.components.DropDownSelector
import de.oljg.glac.core.utils.FontWeight
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.FONT_WEIGHTS
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.SELECTOR_PADDING


@Composable
fun FontWeightSelector(
    label: String,
    selectedFontWeight: FontWeight,
    onNewFontWeightSelected: (String) -> Unit
) {
    DropDownSelector(
        type = FontWeight::class,
        startPadding = SELECTOR_PADDING / 3,
        label = label,
        selectedValue = selectedFontWeight.name,
        onNewValueSelected = onNewFontWeightSelected,
        values = FONT_WEIGHTS
    )
}
