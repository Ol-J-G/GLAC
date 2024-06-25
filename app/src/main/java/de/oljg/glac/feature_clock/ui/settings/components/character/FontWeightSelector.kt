package de.oljg.glac.feature_clock.ui.settings.components.character

import androidx.compose.runtime.Composable
import de.oljg.glac.core.util.FontWeight
import de.oljg.glac.feature_clock.ui.settings.components.common.DropDownSelector
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.FONT_WEIGHTS


@Composable
fun FontWeightSelector(
    label: String,
    selectedFontWeight: FontWeight,
    onNewFontWeightSelected: (String) -> Unit
) {
    DropDownSelector(
        type = FontWeight::class,
        startPadding = DIALOG_DEFAULT_PADDING / 3,
        label = label,
        selectedValue = selectedFontWeight.name,
        onNewValueSelected = onNewFontWeightSelected,
        values = FONT_WEIGHTS
    )
}

