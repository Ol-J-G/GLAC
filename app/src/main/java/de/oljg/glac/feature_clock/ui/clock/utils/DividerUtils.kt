package de.oljg.glac.feature_clock.ui.clock.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_COLON_FIRST_CIRCLE_POSITION
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_COLON_SECOND_CIRCLE_POSITION
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_DASH_COUNT
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_DASH_DOTTED_PART_COUNT
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_HOURS_MINUTES_DIVIDER_CHAR
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_LENGTH_PERCENTAGE
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR


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
    LINE,
    DASHED_LINE,
    DOTTED_LINE,
    DASHDOTTED_LINE,
    COLON,
    CHAR
}

fun DividerStyle.isNeitherNoneNorChar() = this != DividerStyle.NONE && this != DividerStyle.CHAR

fun DividerStyle.isLineBased() = this.name.contains(DividerStyle.LINE.name)

fun DividerStyle.isLineOrDashedLine() = this == DividerStyle.LINE || this == DividerStyle.DASHED_LINE

fun DividerStyle.isRotatable() = this.isLineBased() || this == DividerStyle.COLON

enum class DividerLineEnd {
    ROUND,
    ANGULAR
}

data class DividerAttributes(
    val dividerStyle: DividerStyle = DividerStyle.LINE,
    val dividerThickness: Dp = Dp.Unspecified,
    val dividerColor: Color, // must be set in a composable (no access here to MaterialTheme...)
    val dividerLineCap: StrokeCap = StrokeCap.Round,
    val dividerLengthPercentage: Float = DEFAULT_LENGTH_PERCENTAGE,
    val dividerDashCount: Int = DEFAULT_DASH_COUNT,
    val dividerDashDottedPartCount: Int = DEFAULT_DASH_DOTTED_PART_COUNT,
    val dividerRotateAngle: Float = 0f,
    val colonFirstCirclePosition: Float = DEFAULT_COLON_FIRST_CIRCLE_POSITION,
    val colonSecondCirclePosition: Float = DEFAULT_COLON_SECOND_CIRCLE_POSITION,
    val hoursMinutesDividerChar: Char = DEFAULT_HOURS_MINUTES_DIVIDER_CHAR,
    val minutesSecondsDividerChar: Char = DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR,
    val daytimeMarkerDividerChar: Char = DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR
)


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

    const val MIN_DIVIDER_THICKNESS = 1
    const val MAX_DIVIDER_THICKNESS = 128
    const val DEFAULT_THICKNESS = 2

    const val DEFAULT_LENGTH_PERCENTAGE = .90f

    const val MIN_DASH_COUNT = 2
    const val MAX_DASH_COUNT = 40
    const val DEFAULT_DASH_COUNT = 7

    const val MIN_DASH_DOTTED_PART_COUNT = 1
    const val MAX_DASH_DOTTED_PART_COUNT = 20
    const val DEFAULT_DASH_DOTTED_PART_COUNT = 7

    const val MIN_DIVIDER_ROTATE_ANGLE = -45f
    const val MAX_DIVIDER_ROTATE_ANGLE = 45f
    const val DEFAULT_ROTATE_ANGLE = 0f

    const val DEFAULT_COLON_FIRST_CIRCLE_POSITION = .45f
    const val DEFAULT_COLON_SECOND_CIRCLE_POSITION = .55f

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
}

