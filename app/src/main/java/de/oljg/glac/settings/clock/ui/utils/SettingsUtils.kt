package de.oljg.glac.settings.clock.ui.utils

import androidx.compose.ui.unit.dp
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.core.util.FontWeight
import java.util.Locale

// E.g.: places = 1 => formats 1.23456f to "1.2"
fun Float.format(places: Int) =
    String.format(Locale.getDefault(), "%.${places}f", this)

object SettingsDefaults {
    const val PREVIEW_SIZE_FACTOR = .3f

    val SETTINGS_SCREEN_HORIZONTAL_PADDING = 8.dp
    val SETTINGS_SCREEN_VERTICAL_PADDING = 16.dp

    val DROPDOWN_END_PADDING = 4.dp
    val DROPDOWN_ROW_VERTICAL_PADDING = 8.dp

    val FONT_WEIGHTS = FontWeight.entries.map { weight -> weight.name }
    val FONT_STYLES = FontStyle.entries.map { style -> style.name }

    val SEVEN_SEGMENT_WEIGHTS = SevenSegmentWeight.entries.map { weight -> weight.name }
    val SEVEN_SEGMENT_STYLES = SevenSegmentStyle.entries.map { style -> style.name }

    val CLOCK_CHAR_TYPES = ClockCharType.entries.map { type -> type.name }
}