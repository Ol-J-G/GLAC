package de.oljg.glac.settings.clock.ui.utils

import androidx.compose.ui.unit.dp
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.core.util.FontWeight

object SettingsDefaults {
    const val PREVIEW_SIZE_FACTOR = .3f

    val SETTINGS_SCREEN_HORIZONTAL_PADDING = 8.dp
    val SETTINGS_SCREEN_VERTICAL_PADDING = 16.dp

    val DROPDOWN_END_PADDING = 4.dp
    val DROPDOWN_ROW_VERTICAL_PADDING = 8.dp

    val ALL_FONT_WEIGHTS = listOf(
        FontWeight.THIN.name,
        FontWeight.EXTRA_LIGHT.name,
        FontWeight.LIGHT.name,
        FontWeight.NORMAL.name,
        FontWeight.MEDIUM.name,
        FontWeight.SEMI_BOLD.name,
        FontWeight.BOLD.name,
        FontWeight.EXTRA_BOLD.name,
        FontWeight.BLACK.name
    )
    val ALL_FONT_STYLES = listOf(
        FontStyle.NORMAL.name,
        FontStyle.ITALIC.name
    )
}