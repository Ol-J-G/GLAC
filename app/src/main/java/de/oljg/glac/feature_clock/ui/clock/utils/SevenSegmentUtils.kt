package de.oljg.glac.feature_clock.ui.clock.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import de.oljg.glac.core.util.CommonClockUtils.DIGIT_CHARS
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentDefaults.SEGMENT_CHARS_MAP
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentDefaults.SEVEN_SEGMENT_CHARS
import kotlin.math.atan

enum class Segment {
    TOP,
    CENTER,
    BOTTOM,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT
}

enum class SevenSegmentWeight {
    THIN,
    EXTRA_LIGHT,
    LIGHT,
    REGULAR,
    MEDIUM,
    SEMI_BOLD,
    BOLD,
    EXTRA_BOLD,
    BLACK
}

enum class SevenSegmentStyle {
    REGULAR,
    ITALIC,
    REVERSE_ITALIC,
    OUTLINE,
    OUTLINE_ITALIC,
    OUTLINE_REVERSE_ITALIC
}

fun SevenSegmentStyle.isItalic() =
        this == SevenSegmentStyle.ITALIC || this == SevenSegmentStyle.OUTLINE_ITALIC

fun SevenSegmentStyle.isReverseItalic() =
        this == SevenSegmentStyle.REVERSE_ITALIC || this == SevenSegmentStyle.OUTLINE_REVERSE_ITALIC

fun SevenSegmentStyle.isItalicOrReverseItalic() = this.isItalic() || this.isReverseItalic()

fun SevenSegmentStyle.isOutline() = this == SevenSegmentStyle.OUTLINE
        || this == SevenSegmentStyle.OUTLINE_ITALIC
        || this == SevenSegmentStyle.OUTLINE_REVERSE_ITALIC

fun Char.contains(segment: Segment): Boolean {
    return when (segment) {
        Segment.TOP -> this in SEGMENT_CHARS_MAP.getValue(Segment.TOP)
        Segment.CENTER -> this in SEGMENT_CHARS_MAP.getValue(Segment.CENTER)
        Segment.BOTTOM -> this in SEGMENT_CHARS_MAP.getValue(Segment.BOTTOM)
        Segment.TOP_LEFT -> this in SEGMENT_CHARS_MAP.getValue(Segment.TOP_LEFT)
        Segment.TOP_RIGHT -> this in SEGMENT_CHARS_MAP.getValue(Segment.TOP_RIGHT)
        Segment.BOTTOM_LEFT -> this in SEGMENT_CHARS_MAP.getValue(Segment.BOTTOM_LEFT)
        Segment.BOTTOM_RIGHT -> this in SEGMENT_CHARS_MAP.getValue(Segment.BOTTOM_RIGHT)
    }
}

fun Char.isSevenSegmentChar(): Boolean {
    return this in SEVEN_SEGMENT_CHARS
}

/**
 * val transformationFactors: FloatArray = floatArrayOf(
 *                 ScaleX |      SkewY |          ? | Perspective0(X)
 *                  SkewX |     ScaleY |          ? | Perspective1(Y)
 *                      ? |          ? |     ScaleZ |               ?
 *             TranslateX | TranslateY | TranslateZ | Perspective2(Z)
 *         )
 * * ScaleX/Y/Z => factor (% [1f => 100%])
 * * SkewX/Y => factor (% [1f => 100%])
 * * TranslateX/Y/Z => distance
 * * PerspectiveX/Y/Z => ?
 *
 * -------------------------------------------------------------------
 *
 * To transform a clockChar to a right-leaning one (italic) with using
 * negative skewX factor.
 * After skew, it's moved out of center and to compensate that, using
 * positive translateX distance.
 */
fun italicTransformation(distance: Float, direction: Float = 1f): FloatArray {
    return floatArrayOf(
        1f, 0f, 0f, 0f,
        SevenSegmentDefaults.DEFAULT_ITALIC_FACTOR * -direction, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        distance * SevenSegmentDefaults.DEFAULT_ITALIC_FACTOR * direction, 0f, 0f, 1f
    )
}


/**
 * To transform a clockChar to a left-leaning one (reverse italic) with using
 * positive skewX factor.
 * After skew it's moved out of center and to compensate that, using
 * negative translateX distance.
 */
fun reverseItalicTransformation(distance: Float): FloatArray {
    return italicTransformation(distance = distance, direction = -1f)
}


fun evaluateTransformationMatrix(sevenSegmentStyle: SevenSegmentStyle, distance: Float): Matrix {
    return when (sevenSegmentStyle) {
        SevenSegmentStyle.ITALIC,
        SevenSegmentStyle.OUTLINE_ITALIC
        -> Matrix(italicTransformation(distance))

        SevenSegmentStyle.REVERSE_ITALIC,
        SevenSegmentStyle.OUTLINE_REVERSE_ITALIC
        -> Matrix(reverseItalicTransformation(distance))

        else -> Matrix() // => no transformation (for non-italic styles)
    }
}


fun defaultSegmentColors(charColor: Color): Map<Segment, Color> {
    return mapOf(
        Pair(Segment.TOP, charColor),
        Pair(Segment.CENTER, charColor),
        Pair(Segment.BOTTOM, charColor),
        Pair(Segment.TOP_LEFT, charColor),
        Pair(Segment.TOP_RIGHT, charColor),
        Pair(Segment.BOTTOM_LEFT, charColor),
        Pair(Segment.BOTTOM_RIGHT, charColor)
    )
}


@Composable
fun calculateMaxCharSizeSevenSegment(
    dividerStyle: DividerStyle,
    dividerThickness: Dp,
    dividerCount: Int,
    clockBoxSize: IntSize,
    currentTimeFormatted: String
): Pair<Dp, Dp> {
    val spaceNeededForDividers =
            if (dividerStyle != DividerStyle.NONE) dividerThickness * dividerCount
            else 0.dp

    val availableWidthForSevenSegmentClockChar =
            clockBoxSize.width.pxToDp() - spaceNeededForDividers

    val availableHeightForSevenSegmentClockChar = clockBoxSize.height.pxToDp()

    /**
     * Calculate initial size (width must be always correct, but height could be too big, when
     * respecting aspect ratio (1:2) (w:h)
     */
    var charWidthSevenSegment =
            availableWidthForSevenSegmentClockChar / currentTimeFormatted.length
    var charHeightSevenSegment = charWidthSevenSegment * 2

    /**
     * When height determined throug with above is greater than available height => shrink until
     * it fits, and adjust width accordingly, to keep aspect ratio (1:2) (w:h)
     * (It's the case at 'hh:mm' in landscape)
     */
    while (charHeightSevenSegment > availableHeightForSevenSegmentClockChar) {
        charHeightSevenSegment *= .97f
    }
    charWidthSevenSegment = charHeightSevenSegment / 2

    return Pair(charWidthSevenSegment, charHeightSevenSegment)
}


// @formatter:off
object SevenSegmentDefaults {
    private val SEVEN_SEGMENT_DAYTIME_MARKER_CHARS = listOf('A', 'P')
    val SEVEN_SEGMENT_CHARS = DIGIT_CHARS + SEVEN_SEGMENT_DAYTIME_MARKER_CHARS

    // Which char contains which segement
    val SEGMENT_CHARS_MAP = mapOf(
        Pair(Segment.TOP,           listOf('0',      '2', '3',      '5', '6', '7', '8', '9', 'A', 'P')),
        Pair(Segment.CENTER,        listOf(          '2', '3', '4', '5', '6',      '8', '9', 'A', 'P')),
        Pair(Segment.BOTTOM,        listOf('0',      '2', '3',      '5', '6',      '8', '9'          )),
        Pair(Segment.TOP_LEFT,      listOf('0',                '4', '5', '6',      '8', '9', 'A', 'P')),
        Pair(Segment.TOP_RIGHT,     listOf('0', '1', '2', '3', '4',           '7', '8', '9', 'A', 'P')),
        Pair(Segment.BOTTOM_LEFT,   listOf('0',      '2',                '6',      '8',      'A', 'P')),
        Pair(Segment.BOTTOM_RIGHT,  listOf('0', '1',      '3', '4', '5', '6', '7', '8', '9', 'A'     )),
    )
    val DEFAULT_SEVEN_SEGMENT_CHAR_PADDING = 8.dp

    const val DEFAULT_ASPECT_RATIO = 1f / 2

    // Italic default skew factor is 7%; inclination ~ 4° (atan(.07f).radToDegrees())
    const val DEFAULT_ITALIC_FACTOR = .07f

    // Unfortunately, I didn't find any (non-internal) Kotlin conversion function, so, Java assists
    val DEFAULT_ITALIC_ANGLE = Math.toDegrees(atan(DEFAULT_ITALIC_FACTOR).toDouble()).toFloat()

    const val WEIGHT_FACTOR_THIN = .02f
    const val WEIGHT_FACTOR_EXTRALIGHT = .03f
    const val WEIGHT_FACTOR_LIGHT = .04f
    const val WEIGHT_FACTOR_REGULAR = .05f
    const val WEIGHT_FACTOR_MEDIUM = .06f
    const val WEIGHT_FACTOR_SEMIBOLD = .07f
    const val WEIGHT_FACTOR_BOLD = .08f
    const val WEIGHT_FACTOR_EXTRABOLD = .09f
    const val WEIGHT_FACTOR_BLACK = .0999f

    const val DEFAULT_OUTLINE_SIZE = 2f
    const val MIN_STROKE_WIDTH = 1f
    const val MAX_STROKE_WIDTH = 16f

    /**
     * Percentage of how much brighter/darker the offcolor of a segement is, in comparison to
     * the current background color.
     * * Added in dark mode (brighter)
     * * Substracted in light mode (darker)
     */
    const val OFFCOLOR_LIGHTNESS_DELTA = .01f
    const val OFFSEGMENT_OUTLINE_STROKE_WIDTH = 1f

    // Below this threshold an off color can become lighter, above darker
    const val DEFAULT_LIGHTNESS_THRESHOLD = .1f

    const val CORNER_FACTOR = .3f
    const val VERTICAL_SEGMENT_INNER_CORNER_FACTOR = 1.4f
    const val VERTICAL_SEGMENT_INNER_INDENT_FACTOR = 1.6f
    const val VERTICAL_SEGMENT_OUTER_CORNER_FACTOR = .65f
    const val VERTICAL_SEGMENT_WIDTH_FACTOR = 2.65f
    const val CENTER_SEGMENT_INDENT_FACTOR = 1.55f
    const val CENTER_SEGMENT_HEIGHT_FACTOR = .65f
    const val TOP_AND_BOTTOM_SEGMENT_INDENT_FACTOR = 1.6f
    const val TOP_AND_BOTTOM_SEGMENT_HEIGHT_FACTOR = 1.2f
}
// @formatter:on
