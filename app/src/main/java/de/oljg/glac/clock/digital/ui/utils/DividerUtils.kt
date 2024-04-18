package de.oljg.glac.clock.digital.ui.utils

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_HOURS_MINUTES_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR


/**
 * To add a SPACE-like divider, just use NONE and clockCharSizeFactor to shrink clockChars (e.g.
 * by 20%, from 1f to .8f).
 * This will add some space between clockChars, which can be considered as divider, because the
 * chars are distributed evenly with [Arrangement.SpaceEvenly] and centered, in portrait
 * orientation with [Alignment.CenterHorizontally] and in landscape orientation with
 * [Alignment.CenterVertically].
 */
enum class DividerStyle {
    NONE,
    COLON,
    LINE,
    DASHED_LINE,
    DOTTED_LINE,
    DASHDOTTED_LINE,
    CHAR
}


data class DividerAttributes(
    val dividerStyle: DividerStyle = DividerStyle.COLON,
    val dividerThickness: Dp = Dp.Unspecified,
    val dividerPadding: Dp = Dp.Unspecified,
    val dividerColor: Color,
    val dividerLineCap: StrokeCap = StrokeCap.Butt,
    val dividerLengthPercent: Float? = null,
    val dividerDashCount: Int? = null,
    val dividerDashDottedPartCount: Int? = null,
    val rotateAngleLandscape: Float = 0f
)

fun evaluateDividerThickness(
    specifiedDividerStyle: DividerStyle,
    specifiedDividerThickness: Dp,
    dividerCount: Int,
    clockBoxSize: IntSize,
    currentDisplayOrientation: Int
): Dp {
    val currentDisplayOrientationIsPortrait =
        currentDisplayOrientation == Configuration.ORIENTATION_PORTRAIT

    val defaultDividerThickness = when (specifiedDividerStyle) {

        // Let default colon divider thickness depend on available size.
        DividerStyle.COLON -> (
            (clockBoxSize.width + clockBoxSize.height) * DividerDefaults.COLON_BASE_RESIZE_FACTOR
        ).dp

        DividerStyle.LINE -> DividerDefaults.DEFAULT_DIVIDER_THICKNESS_LINE
        DividerStyle.DOTTED_LINE -> DividerDefaults.DEFAULT_DIVIDER_THICKNESS_DOTTED
        DividerStyle.DASHED_LINE -> DividerDefaults.DEFAULT_DIVIDER_THICKNESS_DASHED
        DividerStyle.DASHDOTTED_LINE -> DividerDefaults.DEFAULT_DIVIDER_THICKNESS_DASHDOTTED

        /**
         * No need for any thickness in case of DividerStyle.CHAR.
         * Note:
         * In portrait orientation DividerStyle.CHAR
         * will fall back to DividerStyle.LINE
         * (char divider isn't useful in portrait => use line)
         */
        else -> DividerDefaults.DEFAULT_DIVIDER_THICKNESS_LINE
    }

    /**
     * Resize divider thickness, because the more digits, the smaller the digits
     * e.g. hh:mm => divider must be bigger than in case of hh:mm:ss:PM to
     * result in a kinda harmonic look imho...
     */
    val resizedDefaultDividerThickness = when (dividerCount) {
        3 -> if (currentDisplayOrientationIsPortrait)
            defaultDividerThickness * DividerDefaults.COLON_RESIZE_FACTOR_PORTRAIT_3_DIVIDERS
        else
            defaultDividerThickness * DividerDefaults.COLON_RESIZE_FACTOR_LANDSCAPE_3_DIVIDERS

        2 -> if (currentDisplayOrientationIsPortrait)
            defaultDividerThickness * DividerDefaults.COLON_RESIZE_FACTOR_PORTRAIT_2_DIVIDERS
        else
            defaultDividerThickness * DividerDefaults.COLON_RESIZE_FACTOR_LANDSCAPE_2_DIVIDERS

        else -> defaultDividerThickness // default, thickest divider size at one divider (hh:mm)
    }

    // Finally, use default/resized divider thickness, or the specified divider thickness
    return when (specifiedDividerStyle) {
        DividerStyle.COLON, DividerStyle.CHAR ->
            if (specifiedDividerThickness == Dp.Unspecified) resizedDefaultDividerThickness else specifiedDividerThickness

        else ->
            if (specifiedDividerThickness == Dp.Unspecified) defaultDividerThickness else specifiedDividerThickness
    }
}


fun evaluateDividerPadding(
    specifiedDividerStyle: DividerStyle,
    specifiedDividerPadding: Dp,
    finalDividerThickness: Dp,
    specifiedDividerThickness: Dp
): Dp {
    /*
     * Let default divider padding depend on default divider thickness.
     */
    val defaultDividerPadding = when (specifiedDividerStyle) {
        DividerStyle.COLON, DividerStyle.CHAR -> finalDividerThickness
        DividerStyle.LINE -> if (specifiedDividerThickness == Dp.Unspecified)
            DividerDefaults.DEFAULT_DIVIDER_PADDING_LINE
        else specifiedDividerThickness * DividerDefaults.DEFAULT_PADDING_FACTOR

        DividerStyle.DASHED_LINE -> if (specifiedDividerThickness == Dp.Unspecified)
            DividerDefaults.DEFAULT_DIVIDER_PADDING_DASHED
        else specifiedDividerThickness * DividerDefaults.DEFAULT_PADDING_FACTOR

        DividerStyle.DASHDOTTED_LINE -> if (specifiedDividerThickness == Dp.Unspecified)
            DividerDefaults.DEFAULT_DIVIDER_PADDING_DASHDOTTED
        else specifiedDividerThickness * DividerDefaults.DEFAULT_PADDING_FACTOR

        else -> if (specifiedDividerThickness == Dp.Unspecified)
            DividerDefaults.DEFAULT_DIVIDER_PADDING_DOTTED
        else specifiedDividerThickness * DividerDefaults.DEFAULT_PADDING_FACTOR
    }

    // Finally, use default/resized divider padding, or the specified divider padding
    return if (specifiedDividerPadding == Dp.Unspecified) defaultDividerPadding else specifiedDividerPadding
}

fun String.dividerCount(minutesSecondsDividerChar: Char = DEFAULT_HOURS_MINUTES_DIVIDER_CHAR,
                        hoursMinutesDividerChar: Char = DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR,
                        daytimeMarkerDividerChar: Char = DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR): Int {
    return this.filter { char ->
        char in listOf(
            minutesSecondsDividerChar,
            hoursMinutesDividerChar,
            daytimeMarkerDividerChar
        )
    }.length
}

fun evalutateDividerCount(timeFormattetWithoutSeparators: String): Int {
    return when (timeFormattetWithoutSeparators.length) {
        4 -> 1 // 'HHMM'
        5, 6 -> 2 // 'HHMMSS' or 'HHMMAM'/'HHMMPM', 7-seg: 'HHMMA'/'HHMMP'
        else -> 3 // (7, 8) e.g.: 'HHMMSSAM'/'HHMMSSPM'
    }
}


/**
 * @return
 * - 0f when sevenSegmentStyle is a non-italic style
 * - DEFAULT_ITALIC_ANGLE when sevenSegmentStyle is an italic style (rotate clockwise)
 * - -DEFAULT_ITALIC_ANGLE when sevenSegmentStyle is a reverse italic style (rotate counter-clockwise)
 */
fun evaluateDividerRotateAngle(sevenSegmentStyle: SevenSegmentStyle): Float {
    return when {
        sevenSegmentStyle.isItalic() -> SevenSegmentDefaults.DEFAULT_ITALIC_ANGLE
        sevenSegmentStyle.isReverseItalic() -> -SevenSegmentDefaults.DEFAULT_ITALIC_ANGLE
        else -> 0f
    }
}

object DividerDefaults {
    const val DEFAULT_HOURS_MINUTES_DIVIDER_CHAR = ':'
    const val DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR = ':'
    const val DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR = ' '


    val DEFAULT_DIVIDER_THICKNESS_LINE = 1.dp
    val DEFAULT_DIVIDER_THICKNESS_DASHED = 1.dp
    val DEFAULT_DIVIDER_THICKNESS_DOTTED = 4.dp
    val DEFAULT_DIVIDER_THICKNESS_DASHDOTTED = 2.dp

    const val DEFAULT_PADDING_FACTOR = 2
    val DEFAULT_DIVIDER_PADDING_LINE = DEFAULT_DIVIDER_THICKNESS_LINE * DEFAULT_PADDING_FACTOR
    val DEFAULT_DIVIDER_PADDING_DASHED = DEFAULT_DIVIDER_THICKNESS_DASHED * DEFAULT_PADDING_FACTOR
    val DEFAULT_DIVIDER_PADDING_DOTTED = DEFAULT_DIVIDER_THICKNESS_DOTTED * DEFAULT_PADDING_FACTOR
    val DEFAULT_DIVIDER_PADDING_DASHDOTTED =
        DEFAULT_DIVIDER_THICKNESS_DASHDOTTED * DEFAULT_PADDING_FACTOR

    const val COLON_BASE_RESIZE_FACTOR = .01f
    const val COLON_RESIZE_FACTOR_PORTRAIT_3_DIVIDERS = .6f
    const val COLON_RESIZE_FACTOR_LANDSCAPE_3_DIVIDERS =
        COLON_RESIZE_FACTOR_PORTRAIT_3_DIVIDERS - .2f

    const val COLON_RESIZE_FACTOR_PORTRAIT_2_DIVIDERS = .8f
    const val COLON_RESIZE_FACTOR_LANDSCAPE_2_DIVIDERS =
        COLON_RESIZE_FACTOR_PORTRAIT_2_DIVIDERS - .2f

    const val DEFAULT_DIVIDER_LENGTH_FACTOR = .9f
    const val DEFAULT_DASH_COUNT = 7
    const val DEFAULT_DASH_DOTTED_PART_COUNT = 7

    /**
     * Caution! Don't change two const below...
     * It's NOT dynamical by now, means works only with
     * values below currently, to have a dash dotted line constructed out of
     * dash-dot-dash ('_._') parts where the dot is in the center of 3 dash widths, so e.g.
     * distance between two dashes is DEFAULT_DISTANCE_DASH_TO_DASH_FACTOR = 2,
     * and distance from 1st dash to dot is DEFAULT_DISTANCE_CIRCLE_TO_CIRCLE_FACTOR = 1.5
     * For me, this looks best (most harmonic) as far as I remember on dash-dotted lines I used to
     * draw in achitectural drawings.
     *
     * drawDashDottedLine() might be refactored some times if other dash-dotted patterns would be
     * needed (which is currently not the case)...
     */
    const val DEFAULT_DISTANCE_DASH_TO_DASH_FACTOR = 2f
    const val DEFAULT_DISTANCE_CIRCLE_TO_CIRCLE_FACTOR = 1.5f

    /**
     * Default DividerStyle.COLON positions in percent.
     *
     * The more dividers, the smaller the chars and 'colon-circles'
     * => Distance from 'colon-circle' to 'colon-circle' cannot be the same in every case and
     *    must be adjusted to be in harmony (imho) ...
     *
     * They are constructed as follows:
     * From the center (.5f), minus X for 1st and plus X for the 2nd,
     * e.g. X=0.2 => 1st: 0.5 - 0.2 = 0.3, 2nd: 0.5 + 0.2 = 0.7
     */
    const val DEFAULT_FIRST_CIRCLE_POSITION_AT_ONE_DIVIDER = .3f // X = 0.2 (30% dist from edge)
    const val DEFAULT_SECOND_CIRCLE_POSITION_AT_ONE_DIVIDER = .7f // X = 0.2 (30% dist from edge)

    const val DEFAULT_FIRST_CIRCLE_POSITION_AT_TWO_DIVIDERS = .4f // X = 0.1 (40% dist from edge)
    const val DEFAULT_SECOND_CIRCLE_POSITION_AT_TWO_DIVIDERS = .6f // X = 0.1 (40% dist from edge)

    const val DEFAULT_FIRST_CIRCLE_POSITION_AT_THREE_DIVIDERS =
        .42f // X = 0.08 (42% dist from edge)
    const val DEFAULT_SECOND_CIRCLE_POSITION_AT_THREE_DIVIDERS =
        .58f // X = 0.08 (42% dist from edge)

    const val DEFAULT_FIRST_CIRCLE_POSITION_PORTRAIT_SPECIAL = .1f // X = 0.4 (10% dist from edge)
    const val DEFAULT_SECOND_CIRCLE_POSITION_PORTRAIT_SPECIAL = .9f // X = 0.4 (10% dist from edge)
}

