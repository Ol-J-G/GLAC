package de.oljg.glac.clock.digital.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StampedPathEffectStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DISTANCE_CIRCLE_TO_CIRCLE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DISTANCE_DASH_TO_DASH_FACTOR
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_ITALIC_ANGLE
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.pxToDp
import de.oljg.glac.core.util.TestTags

/**
 * As follows a description of the non-obvious params...
 * @param clockBoxSize Available size, where a [LineDivider] will be drawn in
 * @param dividerDashCount How many dashes will be drawn when
 *        [dividerStyle] == [DividerStyle.DASHED_LINE]
 * @param dividerDashDottedPartCount How may dashDottetParts (e.g.: '_._') will be repeatedly drawn
 */
@Composable
fun LineDivider(
    dividerPadding: Dp,
    dividerThickness: Dp,
    clockBoxSize: IntSize,
    dividerDashCount: Int,
    dividerColor: Color,
    dividerStyle: DividerStyle,
    dividerLineCap: StrokeCap,
    orientation: Int,
    dividerLengthPercent: Float,
    dividerDashDottedPartCount: Int,
    clockCharType: ClockCharType? = null,
    sevenSegmentStyle: SevenSegmentStyle? = null
) {
    Canvas(
        modifier = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Modifier
                .testTag(TestTags.LINE_DIVIDER)
                .padding(dividerPadding)
                .requiredSize(
                    width = clockBoxSize.width.pxToDp(),
                    height = dividerThickness
                )
        } else {
            Modifier
                .testTag(TestTags.LINE_DIVIDER)
                .padding(dividerPadding)
                .requiredSize(
                    width = dividerThickness,
                    height = clockBoxSize.height.pxToDp()
                )
        }
    ) {

        // Common divider calculations
        val dividerLenght =
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                size.width * dividerLengthPercent
            else size.height * dividerLengthPercent
        val gapBetweenEdgeAndDivider =
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                (size.width - dividerLenght) / 2
            else (size.height - dividerLenght) / 2

        // In case of 7-seg italic style and only in landscape o. => rotate divider appropriately
        val dividerRotateAngle =
            if (clockCharType == ClockCharType.SEVEN_SEGMENT &&
                orientation == Configuration.ORIENTATION_LANDSCAPE)
                evaluateDividerRotateAngle(sevenSegmentStyle)
            /**
             * No need to rotate dividers in portrait orientation ((reverse-)italic 7-seg
             * clockChars have straight top/bottom edges; or in case of font)
             */
            else 0f

        if (dividerStyle == DividerStyle.DASHDOTTED_LINE) {
            /**
             * Example:
             * dash-dotted pattern is: '_._'
             * dashLength is the length of one dash ('_')
             */
            val dashLength =
                dividerLenght /
                        (dividerDashDottedPartCount *
                                (DEFAULT_DISTANCE_DASH_TO_DASH_FACTOR *
                                        DEFAULT_DISTANCE_CIRCLE_TO_CIRCLE_FACTOR))

            /**
             * Example:
             * dash-dotted pattern is: '_._'
             * distanceDashToDash is the distance from 1st dash ('_') to second dash ('_')
             */
            val distanceDashToDash = dashLength * DEFAULT_DISTANCE_DASH_TO_DASH_FACTOR

            /**
             * Example:
             * dash-dotted pattern is: '_._', two parts: '_.__._'
             * distanceCircleToCircle is the distance from 1st circle ('.') to second circle ('.')
             */
            val distanceCircleToCircle = dashLength * DEFAULT_DISTANCE_CIRCLE_TO_CIRCLE_FACTOR

            rotate(degrees = dividerRotateAngle, pivot = size.center) {
                drawDashDottedLine(
                    dashDottedPatternParts = dividerDashDottedPartCount,
                    dividerColor = dividerColor,
                    orientation = orientation,
                    dashLength = dashLength,
                    gapBetweenEdgeAndDivider = gapBetweenEdgeAndDivider,
                    dividerThickness = dividerThickness,
                    distanceDashToDash = distanceDashToDash,
                    distanceCircleToCircle = distanceCircleToCircle
                )
            }
        } else { // dividerStyle != DividerStyle.DASHDOTTED_LINE

            // Calculate an equally distributed amount of DASHes
            val dashAndGapCount =
                (dividerDashCount * 2) - 1
            val averageDashWidth = dividerLenght / dashAndGapCount
            val phase = 0f

            /**
             * Calculate an equally distributed amount of DOTs
             * (will lower given divider thickness a bit, but looks more harmonic imho)
             */
            val dotCount = dividerLenght / dividerThickness.toPx()
            val gapFactor = 2

            // even count of dots, odd count of gaps
            val dotAndGapCount = (dotCount.toInt() * gapFactor) - 1
            val averageDotWidth = dividerLenght / dotAndGapCount

            val pathEffect = when (dividerStyle) {
                DividerStyle.DASHED_LINE -> PathEffect.dashPathEffect(
                    intervals = floatArrayOf(
                        averageDashWidth,
                        averageDashWidth
                    ),
                    phase = phase
                )

                DividerStyle.DOTTED_LINE -> PathEffect.stampedPathEffect(
                    shape = createDotPath(
                        orientation = orientation,
                        dotWidth = averageDotWidth
                    ),
                    advance = averageDotWidth * gapFactor,
                    phase = phase,
                    style = StampedPathEffectStyle.Translate,
                )

                else -> null //just LINE remains => default (no pathEffect needed)
            }

            rotate(degrees = dividerRotateAngle, pivot = size.center) {
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    drawLine( // centered horizontal
                        start =
                        Offset(gapBetweenEdgeAndDivider, size.center.y),
                        end = Offset(size.width - gapBetweenEdgeAndDivider, size.center.y),
                        color = dividerColor,
                        strokeWidth = dividerThickness.toPx(),
                        cap = dividerLineCap,
                        pathEffect = pathEffect
                    )
                } else {
                    drawLine( // centered vertical
                        start = Offset(size.center.x, gapBetweenEdgeAndDivider),
                        end = Offset(size.center.x, size.height - gapBetweenEdgeAndDivider),
                        color = dividerColor,
                        strokeWidth = dividerThickness.toPx(),
                        cap = dividerLineCap,
                        pathEffect = pathEffect
                    )
                }
            }
        }
    }
}


private fun createDotPath(
    orientation: Int,
    dotWidth: Float
): Path {
    return Path().apply {
        arcTo(
            startAngleDegrees = 0f,
            sweepAngleDegrees = 359.99f, // 360 will draw nothing^^ ...uhm?!
            forceMoveTo = false,
            rect = Rect(

                // Line will be drawn at size.center, therefore '-dotWidth / 2' is used here
                offset =
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    Offset(0f, -dotWidth / 2)
                else
                    Offset(-dotWidth / 2, 0f),
                size = Size(dotWidth, dotWidth)
            )
        )
        close()
    }
}


private fun DrawScope.drawDashDottedLine(
    dashDottedPatternParts: Int,
    dividerColor: Color,
    orientation: Int,
    dashLength: Float,
    gapBetweenEdgeAndDivider: Float,
    dividerThickness: Dp,
    distanceDashToDash: Float,
    distanceCircleToCircle: Float
) {
    var currentDashPosition = gapBetweenEdgeAndDivider
    var currentCircleCenter = distanceCircleToCircle + gapBetweenEdgeAndDivider
    repeat(times = dashDottedPatternParts) {
        drawRect(
            color = dividerColor,
            topLeft =
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                Offset(x = currentDashPosition, y = 0f)
            else
                Offset(x = 0f, y = currentDashPosition),
            size =
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                Size(width = dashLength, height = dividerThickness.toPx())
            else
                Size(width = dividerThickness.toPx(), height = dashLength)
        )
        drawCircle(
            color = dividerColor,
            radius = dividerThickness.toPx() / 2,
            center =
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                Offset(x = currentCircleCenter, y = size.center.y)
            else
                Offset(x = size.center.x, y = currentCircleCenter)
        )
        drawRect(
            color = dividerColor,
            topLeft =
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                Offset(x = currentDashPosition + distanceDashToDash, y = 0f)
            else
                Offset(x = 0f, y = currentDashPosition + distanceDashToDash),
            size =
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                Size(width = dashLength, height = dividerThickness.toPx())
            else
                Size(width = dividerThickness.toPx(), height = dashLength)
        )
        currentDashPosition += distanceDashToDash + dashLength
        currentCircleCenter += distanceCircleToCircle * 2
    }
}

//TODO: introduce outline variant => appropriate for 7-seg outline styles
@Composable
fun ColonDivider(
    dividerPadding: Dp,
    clockBoxSize: IntSize,
    dividerThickness: Dp,
    dividerColor: Color,
    firstCirclePositionPercent: Float = .33f,
    secondCirclePositionPercent: Float = .66f,
    orientation: Int,
    clockCharType: ClockCharType? = null,
    sevenSegmentStyle: SevenSegmentStyle? = null
) {
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Canvas(
            modifier = Modifier
                .testTag(TestTags.COLON_DIVIDER)
                .padding(top = dividerPadding, bottom = dividerPadding)
                .size(width = clockBoxSize.width.pxToDp(), height = dividerThickness)
        ) {
            drawColon(
                firstCircleCenter = Offset(
                    x = size.width * firstCirclePositionPercent,
                    y = size.center.y
                ),
                secondCircleCenter = Offset(
                    x = size.width * secondCirclePositionPercent,
                    y = size.center.y
                ),
                radius = dividerThickness.toPx() / 2,
                dividerColor
            )
        }
    } else { // ORIENTATION_LANDSCAPE
        Canvas(
            Modifier
                .testTag(TestTags.COLON_DIVIDER)
                .padding(start = dividerPadding, end = dividerPadding)
                .size(width = dividerThickness, height = clockBoxSize.height.pxToDp()),
        ) {

            /**
             * In case of 7-seg italic style and only in landscape orientation
             * => rotate divider appropriately
             */
            val dividerRotateAngle =
                if (clockCharType == ClockCharType.SEVEN_SEGMENT)
                    evaluateDividerRotateAngle(sevenSegmentStyle)
                /**
                 * No need to rotate dividers in portrait orientation ((reverse-)italic 7-seg
                 * clockChars have straight top/bottom edges; or in case of font)
                 */
                else 0f

            rotate(degrees = dividerRotateAngle, pivot = size.center) {
                drawColon(
                    firstCircleCenter = Offset(
                        size.center.x,
                        size.height * firstCirclePositionPercent
                    ),
                    secondCircleCenter = Offset(
                        size.center.x,
                        size.height * secondCirclePositionPercent
                    ),
                    radius = dividerThickness.toPx() / 2,
                    dividerColor
                )
            }
        }
    }
}


private fun DrawScope.drawColon(
    firstCircleCenter: Offset,
    secondCircleCenter: Offset,
    radius: Float,
    dividerColor: Color
) {
    drawCircle(
        color = dividerColor,
        center = firstCircleCenter,
        radius = radius
    )
    drawCircle(
        color = dividerColor,
        center = secondCircleCenter,
        radius = radius
    )
}


/**
 * @return
 * - 0f when sevenSegmentStyle is a non-italic style
 * - DEFAULT_ITALIC_ANGLE when sevenSegmentStyle is an italic style (rotate clockwise)
 * - -DEFAULT_ITALIC_ANGLE when sevenSegmentStyle is a reverse italic style (rotate counter-clockwise)
 */
private fun evaluateDividerRotateAngle(sevenSegmentStyle: SevenSegmentStyle?): Float {
    return if (sevenSegmentStyle != null &&
            sevenSegmentStyle.name.contains(SevenSegmentStyle.ITALIC.name))
        when (sevenSegmentStyle) {
            SevenSegmentStyle.REVERSE_ITALIC,
            SevenSegmentStyle.OUTLINE_REVERSE_ITALIC -> -DEFAULT_ITALIC_ANGLE

            else -> DEFAULT_ITALIC_ANGLE
        }
    else 0f
}
