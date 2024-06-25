package de.oljg.glac.feature_clock.ui.settings.components.character

import androidx.compose.runtime.Composable
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.feature_clock.ui.settings.components.common.DropDownSelector
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.FONT_STYLES


@Composable
fun FontStyleSelector(
    label: String,
    selectedFontStyle: FontStyle,
    onNewFontStyleSelected: (String) -> Unit
) {
    DropDownSelector(
        type = FontStyle::class,
        startPadding = DIALOG_DEFAULT_PADDING / 3,
        label = label,
        selectedValue = selectedFontStyle.name,
        onNewValueSelected = onNewFontStyleSelected,
        values = FONT_STYLES,
    )
}

