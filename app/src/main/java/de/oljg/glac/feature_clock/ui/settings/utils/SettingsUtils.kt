package de.oljg.glac.feature_clock.ui.settings.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.oljg.glac.R
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.core.util.FontWeight
import de.oljg.glac.feature_clock.ui.clock.utils.ClockCharType
import de.oljg.glac.feature_clock.ui.clock.utils.DividerLineEnd
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentWeight
import de.oljg.glac.feature_clock.ui.clock.utils.isItalicOrReverseItalic
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.FLOAT_PERCENTAGE_BETWEEN_ZERO_ONE
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.INVALID_DIVIDER_CHARS
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.PERCENTAGE_ONLY_2_PLACES
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.PIXEL
import kotlin.math.floor
import kotlin.math.pow


/**
 * Format a Float as follows: Round down and let [places] decimal place remain.
 * [percentage] is an option to format percentages by adding a "0" postfix at
 * '3-length float strings' (e.g. 0.1 => "0.10" => 10%) to easly apply [prettyPrintPercentage]
 * on such edge case values.
 *
 * Note: String.format() still has no option to set roundingMode, so, implemented this function..
 */
fun Float.format(places: Int, percentage: Boolean = false): String {
    if (percentage && places != 2)
        throw IllegalArgumentException(PERCENTAGE_ONLY_2_PLACES)

    if (percentage && this !in 0f..1f)
        throw IllegalArgumentException(FLOAT_PERCENTAGE_BETWEEN_ZERO_ONE)

    if (places == 0) return this.cutOffDecimalPlaces()
    require(places > 0)

    val factor = 10f.pow(places)
    val result = (floor(this * factor) / factor).toString()
    return buildString {
        append(result)
        if (result.length == 3 && percentage) append("0") // e.g. 0.9 => "0.9>0<"
    }

}


/**
 * 12.345678f => "12"
 */
fun Float.cutOffDecimalPlaces() = this.toString().substring(
    startIndex = 0,
    endIndex = this.toInt().toString().length
)


fun Float.prettyPrintPixel() = this.cutOffDecimalPlaces() + " " + PIXEL

fun Float.prettyPrintCirclePosition() = this.prettyPrintPercentage(postfix = "% From Edge")

fun Float.prettyPrintAngle() = this.format(places = 1) + " °"


/**
 * Examples:
 * 0f       => "0.00" => "0 %"
 * 0.07123f => "0.07" => "7 %"
 * 0.12123f => "0.12" => "12 %"
 * 0.9f     => "0.90" => "90 %"
 * 1f       => "1.00" => "100 %"
 */
fun Float.prettyPrintPercentage(postfix: String = " %"): String {
    val percentage = if (this == 1f) "100"
    else this.format(places = 2, percentage = true).replace(Regex("0[,.]0?"), "")
    return buildString {
        append(percentage)
        append(postfix)
    }
}


@Composable
fun FontWeight.translate() = when(this) {
    FontWeight.THIN -> stringResource(R.string.thin)
    FontWeight.EXTRA_LIGHT -> stringResource(R.string.extra) + " " + stringResource(R.string.light)
    FontWeight.LIGHT-> stringResource(R.string.light)
    FontWeight.NORMAL -> stringResource(R.string.normal)
    FontWeight.MEDIUM -> stringResource(R.string.medium)
    FontWeight.SEMI_BOLD -> stringResource(R.string.semi) + " " + stringResource(R.string.bold)
    FontWeight.BOLD -> stringResource(R.string.bold)
    FontWeight.EXTRA_BOLD -> stringResource(R.string.extra) + " " + stringResource(R.string.bold)
    FontWeight.BLACK -> stringResource(R.string.black)
}


@Composable
fun FontStyle.translate() = when(this) {
    FontStyle.NORMAL -> stringResource(R.string.normal)
    FontStyle.ITALIC -> stringResource(R.string.italic)
}


@Composable
fun SevenSegmentStyle.translate() = when(this) {
    SevenSegmentStyle.REGULAR -> stringResource(R.string.regular)
    SevenSegmentStyle.ITALIC -> stringResource(R.string.italic)
    SevenSegmentStyle.REVERSE_ITALIC -> stringResource(R.string.reverse) + " " +
            stringResource(R.string.italic)
    SevenSegmentStyle.OUTLINE -> stringResource(R.string.outline)
    SevenSegmentStyle.OUTLINE_ITALIC -> stringResource(R.string.outline) + " " +
            stringResource(R.string.italic)
    SevenSegmentStyle.OUTLINE_REVERSE_ITALIC -> stringResource(R.string.outline) + " " +
            stringResource(R.string.reverse) + " " +
            stringResource(R.string.italic)
}


@Composable
fun SevenSegmentWeight.translate() = when(this) {
    SevenSegmentWeight.THIN -> stringResource(R.string.thin)
    SevenSegmentWeight.EXTRA_LIGHT -> stringResource(R.string.extra) + " " +
            stringResource(R.string.light)
    SevenSegmentWeight.LIGHT-> stringResource(R.string.light)
    SevenSegmentWeight.REGULAR -> stringResource(R.string.regular)
    SevenSegmentWeight.MEDIUM -> stringResource(R.string.medium)
    SevenSegmentWeight.SEMI_BOLD -> stringResource(R.string.semi) + " " +
            stringResource(R.string.bold)
    SevenSegmentWeight.BOLD -> stringResource(R.string.bold)
    SevenSegmentWeight.EXTRA_BOLD -> stringResource(R.string.extra) + " " +
            stringResource(R.string.bold)
    SevenSegmentWeight.BLACK -> stringResource(R.string.black)
}


@Composable
fun DividerStyle.translate() = when(this) {
    DividerStyle.NONE -> stringResource(R.string.none)
    DividerStyle.LINE -> stringResource(R.string.line)
    DividerStyle.DASHED_LINE -> stringResource(R.string.dashed) + " " +
            stringResource(R.string.line)
    DividerStyle.DOTTED_LINE -> stringResource(R.string.dotted) + " " +
            stringResource(R.string.line)
    DividerStyle.DASHDOTTED_LINE -> stringResource(R.string.dash_dotted) + " " +
            stringResource(R.string.line)
    DividerStyle.COLON -> stringResource(R.string.colon)
    DividerStyle.CHAR -> stringResource(R.string.character)
}


@Composable
fun DividerLineEnd.translate() = when(this) {
    DividerLineEnd.ROUND -> stringResource(R.string.round)
    DividerLineEnd.ANGULAR -> stringResource(R.string.angular)
}


fun String.isValidDividerChar() = this.length == 1 &&
        !this.toCharArray()[0].isLetterOrDigit() &&
        this.toCharArray()[0] !in INVALID_DIVIDER_CHARS


fun isSevenSegmentItalicOrReverseItalic(
    clockCharType: ClockCharType,
    sevenSegmentStyle: SevenSegmentStyle
) = clockCharType == ClockCharType.SEVEN_SEGMENT && sevenSegmentStyle.isItalicOrReverseItalic()


object SettingsDefaults {
    const val DEFAULT_DEBOUNCE_TIMEOUT = 500L // millis

    const val MIN_THEME_NAME_LENGTH = 1
    const val MAX_THEME_NAME_LENGTH = 30

    const val PREVIEW_SIZE_FACTOR = .3f

    val SETTINGS_SCREEN_HORIZONTAL_OUTER_PADDING = 8.dp

    val SETTINGS_SCREEN_PREVIEW_SPACE = 4.dp
    val DEFAULT_VERTICAL_SPACE = 16.dp
    val DEFAULT_HORIZONTAL_SPACE = 8.dp
    val SETTINGS_HORIZONTAL_PADDING = 16.dp

    val DROPDOWN_END_PADDING = 4.dp
    val DROPDOWN_ROW_VERTICAL_PADDING = 8.dp
    val TRAILING_ICON_END_PADDING = 12.dp
    val DEFAULT_ROUNDED_CORNER_SIZE = 8.dp //TODO: many of them will be used also outside clock settings => check => relocate to core
    val DEFAULT_BORDER_WIDTH = 1.dp

    val RADIO_BUTTON_ROW_HEIGHT = 56.dp
    val SETTINGS_SECTION_HEIGHT = 48.dp
    val DEFAULT_ICON_BUTTON_SIZE = 44.dp
    val RESET_BUTTON_SIZE = 36.dp
    val SETTINGS_SLIDER_HEIGHT = 58.dp
    val EDGE_PADDING = 12.dp

    val CLOCK_CHAR_TYPE_FONT_SIZE = 20.sp
    val DROP_DOWN_MENU_ITEM_FONT_SIZE = 18.sp
    const val PIXEL = "Pixel"

    val COLOR_SELECTOR_HEIGHT = 92.dp
    val COLOR_SELECTOR_TF_TOP_PADDING = 8.dp
    val COLOR_SELECTOR_COLOR_SWATCH_SIZE = 40.dp
    val COLOR_SELECTOR_HEX_TEXTFIELD_WIDTH = 112.dp
    val COLOR_SELECTOR_TF_COLOR_SWATCH_SPACE = 8.dp

    val COLOR_PICKER_SLIDER_HEIGHT = 36.dp
    val COLOR_PICKER_HEIGHT_EXTRA_SMALL = 250.dp
    val COLOR_PICKER_HEIGHT_SMALL = 280.dp
    val COLOR_PICKER_HEIGHT_MEDIUM = 350.dp
    val COLOR_PICKER_HEIGHT_LARGE = 550.dp
    val COLOR_PICKER_BUTTON_SPACE = 0.dp
    const val COLOR_PICKER_FLASHING_COLOR_ANIM_DURATION = 825

    val MULTI_COLOR_SELECTOR_PADDING = 4.dp

    val DIALOG_DEFAULT_PADDING = 16.dp
    val DIALOG_BORDER_WIDTH = 1.dp
    val DIALOG_SHAPE = RoundedCornerShape(24.dp)

    // To have a bit space and should not be too hard to get your thumbs on dialog control elements
    const val COLOR_DIALOG_WIDTH = .9f
    val DIALOG_TONAL_ELEVATION = 6.dp

    /**
     * DateTimeFormatter reserved chars and such ones that would lead to an incomplete
     * string literal, etc. (in other words: these chars cannot be part of a time format string)
     */
    val INVALID_DIVIDER_CHARS = listOf('#', '{', '}', '\'', '[', ']', '™')
    val CHAR_SELECTOR_TF_WIDTH = 120.dp
    val CHAR_SELECTOR_TF_TOP_PADDING = 8.dp

    val FONT_WEIGHTS = FontWeight.entries.map { weight -> weight.name }
    val FONT_STYLES = FontStyle.entries.map { style -> style.name }

    val SEVEN_SEGMENT_WEIGHTS = SevenSegmentWeight.entries.map { weight -> weight.name }
    val SEVEN_SEGMENT_STYLES = SevenSegmentStyle.entries.map { style -> style.name }

    val DIVIDER_STYLES = DividerStyle.entries.map { style -> style.name }
    val DIVIDER_STYLES_WITHOUT_CHAR_STYLE = DividerStyle.entries.filterNot { style ->
        style == DividerStyle.CHAR
    }.map { style -> style.name }

    val DIVIDER_LINE_ENDS = DividerLineEnd.entries.map { lineEnd -> lineEnd.name }

    // No need to i18n this
    const val PERCENTAGE_ONLY_2_PLACES = "Use percentage only with 2 places!"
    const val FLOAT_PERCENTAGE_BETWEEN_ZERO_ONE = "Float percentage can only be in 0f..1f!"

    // To use with rememberSavable / to save a Color in a Bundle
    val colorSaver: Saver<Color, Any> = run {
        val colorKey = "color"
        mapSaver(
            save = { color -> mapOf(colorKey to color.toArgb()) },
            restore = { colorMap -> Color((colorMap[colorKey] as Int)) }
        )
    }
}
