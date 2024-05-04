package de.oljg.glac.settings.clock.ui.utils

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.core.util.FontWeight
import java.util.Locale

// E.g.: places = 1 => formats 1.23456f to "1.2"
fun Float.format(places: Int) =
    String.format(Locale.getDefault(), "%.${places}f", this)


fun Float.prettyPrintOnePlace() = this.format(places = 1)


/**
 * Examples:
 * 0.00f => 0 %
 * 0.07f => 7 %
 * 0.12f => 12 %
 * 1.00f => 100 %
 */
fun Float.prettyPrintPercentage(): String {
    val percentage = if (this == 1f) "100"
    else this.format(places = 2).replace(Regex("0[,.]0?"),"")
    return buildString {
        append(percentage)
        append(" %")
    }
}

fun String.prettyPrintEnumName(): String {
    val words = this.lowercase().split('_')
    return buildString {
        words.forEachIndexed { index, word ->
            append(word.capitalize(androidx.compose.ui.text.intl.Locale.current))

            /**
             * _Example_
             * Input        : "WORD_WORD"
             * Don't do this: "Word Word "
             * Do this      : "Word Word" (<= blank only between words)
             */
            if (index + 1 != words.size) append(' ')
        }
    }
}

object SettingsDefaults {
    const val PREVIEW_SIZE_FACTOR = .3f

    val SETTINGS_SCREEN_HORIZONTAL_OUTER_PADDING = 8.dp
    val SETTINGS_SCREEN_VERTICAL_OUTER_PADDING = 16.dp

    val SETTINGS_SCREEN_PREVIEW_DIVIDER_PADDING = 8.dp
    val DEFAULT_VERTICAL_SPACE = 16.dp
    val SETTINGS_HORIZONTAL_PADDING = 16.dp

    val DROPDOWN_END_PADDING = 4.dp
    val DROPDOWN_ROW_VERTICAL_PADDING = 8.dp
    val TRAILING_ICON_END_PADDING = 12.dp
    val DEFAULT_ROUNDED_CORNER_SIZE = 8.dp
    val DEFAULT_BORDER_WIDTH = 1.dp

    val RADIO_BUTTON_ROW_HEIGHT = 56.dp
    val SETTINGS_SECTION_HEIGHT = 48.dp
    val DEFAULT_ICON_BUTTON_SIZE = 22.dp



    val CLOCK_CHAR_TYPE_FONT_SIZE = 18.sp

    val FONT_WEIGHTS = FontWeight.entries.map { weight -> weight.name }
    val FONT_STYLES = FontStyle.entries.map { style -> style.name }

    val SEVEN_SEGMENT_WEIGHTS = SevenSegmentWeight.entries.map { weight -> weight.name }
    val SEVEN_SEGMENT_STYLES = SevenSegmentStyle.entries.map { style -> style.name }

    val CLOCK_CHAR_TYPES = ClockCharType.entries.map { type -> type.name }

    val DIVIDER_STYLES = DividerStyle.entries.map { style -> style.name }
}