package de.oljg.glac.settings.clock.ui.utils

import androidx.compose.ui.unit.dp
import de.oljg.glac.core.util.FontWeight

object SettingsDefaults {
    const val PREVIEW_SIZE_FACTOR = .3f

    val SETTINGS_SCREEN_HORIZONTAL_PADDING = 8.dp
    val SETTINGS_SCREEN_VERTICAL_PADDING = 16.dp

    val DROPDOWN_END_PADDING = 4.dp
    val DROPDOWN_ROW_VERTICAL_PADDING = 8.dp

    val ALL_FONT_WEIGHTS = listOf(
        FontWeight.THIN,
        FontWeight.EXTRA_LIGHT,
        FontWeight.LIGHT,
        FontWeight.NORMAL,
        FontWeight.MEDIUM,
        FontWeight.SEMI_BOLD,
        FontWeight.BOLD,
        FontWeight.EXTRA_BOLD,
        FontWeight.BLACK
    )
}