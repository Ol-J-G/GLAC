package de.oljg.glac.settings.clock.ui.components

import androidx.compose.runtime.Composable
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.ALL_FONT_STYLES


@Composable
fun FontStyleSelector(
    label: String,
    selectedFontStyle: String,
    onNewFontStyleSelected: (String) -> Unit
) {
    DropDownSelector(
        label = label,
        selectedValue = selectedFontStyle,
        onNewValueSelected = onNewFontStyleSelected,
        values = ALL_FONT_STYLES
    )
}

