package de.oljg.glac.feature_clock.ui.settings.components.character

import androidx.compose.runtime.Composable
import de.oljg.glac.core.ui.components.DropDownSelector
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.FONT_STYLES
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.SELECTOR_PADDING


@Composable
fun FontStyleSelector(
    label: String,
    selectedFontStyle: FontStyle,
    onNewFontStyleSelected: (String) -> Unit
) {
    DropDownSelector(
        type = FontStyle::class,
        startPadding = SELECTOR_PADDING / 3,
        label = label,
        selectedValue = selectedFontStyle.name,
        onNewValueSelected = onNewFontStyleSelected,
        values = FONT_STYLES,
    )
}
