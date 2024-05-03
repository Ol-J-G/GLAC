package de.oljg.glac.settings.clock.ui.components

import androidx.compose.runtime.Composable
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.FONT_STYLES
import de.oljg.glac.settings.clock.ui.utils.prettyPrintEnumName


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
        values = FONT_STYLES,
        prettyPrintValue = String::prettyPrintEnumName
    )
}

