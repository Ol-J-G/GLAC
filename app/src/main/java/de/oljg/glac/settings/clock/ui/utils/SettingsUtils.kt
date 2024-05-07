package de.oljg.glac.settings.clock.ui.utils

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.DividerLineEnd
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.clock.digital.ui.utils.isItalicOrReverseItalic
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.core.util.FontWeight
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.PIXEL
import kotlin.math.floor


fun Float.prettyPrintAngle() = this.formatToOneDecimalPlace() + " °"

fun Float.formatToOneDecimalPlace(): String {
   return (floor(this * 10) / 10).toString()
}

fun Float.prettyPrintPixel() = this.cutOffDecimalPlaces() + " " + PIXEL

/**
 * 12.345678f => "12"
 */
fun Float.cutOffDecimalPlaces() = this.toString().substring(
    startIndex = 0,
    endIndex = this.toInt().toString().length
)

/**
 * Just needed to pretty print percentage.
 *
 * Note: String.format() still has no option to set roundingMode, so, implemented this function..
 *
 * Examples:
 * 0f       => "0.00"
 * 0.07123f => "0.07"
 * 0.12123f => "0.12"
 * 0.9f     => "0.90"
 * 1f       => "1.00"
 */
fun Float.formatTwoDecimalPlaces(): String {
    require(this in 0f..1f)
    val thisString = this.toString()
    return if(thisString.length == 3)
        thisString.substring(0, 3) + "0"
    else thisString.substring(0, 4)
}

/**
 * Examples:
 * 0f       => "0.00" => "0 %"
 * 0.07123f => "0.07" => "7 %"
 * 0.12123f => "0.12" => "12 %"
 * 0.9f     => "0.90" => "90 %"
 * 1f       => "1.00" => "100 %"
 */
fun Float.prettyPrintPercentage(): String {
    val percentage = if (this == 1f) "100"
    else this.formatTwoDecimalPlaces().replace(Regex("0[,.]0?"),"")
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


fun isSevenSegmentItalicOrReverseItalic(
    clockCharType: ClockCharType,
    sevenSegmentStyle: SevenSegmentStyle
) = clockCharType == ClockCharType.SEVEN_SEGMENT && sevenSegmentStyle.isItalicOrReverseItalic()


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
    val RESET_BUTTON_SIZE = 36.dp
    val SETTINGS_SLIDER_HEIGHT = 58.dp
    val TEXT_ICON_SPACE = 20.dp

    val CLOCK_CHAR_TYPE_FONT_SIZE = 18.sp
    const val PIXEL = "Pixel"

    val FONT_WEIGHTS = FontWeight.entries.map { weight -> weight.name }
    val FONT_STYLES = FontStyle.entries.map { style -> style.name }

    val SEVEN_SEGMENT_WEIGHTS = SevenSegmentWeight.entries.map { weight -> weight.name }
    val SEVEN_SEGMENT_STYLES = SevenSegmentStyle.entries.map { style -> style.name }

    val CLOCK_CHAR_TYPES = ClockCharType.entries.map { type -> type.name }

    val DIVIDER_STYLES = DividerStyle.entries.map { style -> style.name }
    val DIVIDER_STYLES_WITHOUT_CHAR_STYLE = DividerStyle.entries.filterNot { style ->
        style == DividerStyle.CHAR
    }.map { style -> style.name }

    val DIVIDER_LINE_ENDS = DividerLineEnd.entries.map { lineEnd -> lineEnd.name }
}
