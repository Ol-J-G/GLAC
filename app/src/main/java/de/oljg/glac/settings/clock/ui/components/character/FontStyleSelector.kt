package de.oljg.glac.settings.clock.ui.components.character

import androidx.compose.runtime.Composable
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.FONT_STYLES


@Composable
fun FontStyleSelector(
    label: String,
    selectedFontStyle: FontStyle,
    onNewFontStyleSelected: (String) -> Unit
) {
    DropDownSelector(
        type = FontStyle::class,
        label = label,
        selectedValue = selectedFontStyle.name,
        onNewValueSelected = onNewFontStyleSelected,
        values = FONT_STYLES,
    )
}

