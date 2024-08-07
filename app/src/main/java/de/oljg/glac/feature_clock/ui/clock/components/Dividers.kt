package de.oljg.glac.feature_clock.ui.clock.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
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
import de.oljg.glac.core.utils.TestTags
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_DISTANCE_CIRCLE_TO_CIRCLE_FACTOR
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_DISTANCE_DASH_TO_DASH_FACTOR
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.feature_clock.ui.clock.utils.pxToDp

/**
 * As follows a description of the non-obvious params...
 * @param clockBoxSize Available size, where a [LineDivider] will be drawn in
 * @param dividerDashCount How many dashes will be drawn when
 *        [dividerStyle] == [DividerStyle.DASHED_LINE]
 * @param dividerDashDottedPartCount How may dashDottetParts (e.g.: '_._') will be repeatedly drawn
 */
@Composable
fun LineDivider(
    dividerThickness: Dp,
    clockBoxSize: IntSize,
    dividerDashCount: Int,
    dividerColor: Color,
    dividerStyle: DividerStyle,
    dividerLineCap: StrokeCap,
    dividerLengthPercent: Float,
    dividerDashDottedPartCount: Int,
    dividerRotateAngle: Float = 0f,
    orientation: Int
) {
    Canvas(
        modifier = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Modifier
                .testTag(TestTags.LINE_DIVIDER)
                .requiredSize(
                    width = clockBoxSize.width.pxToDp(),
                    height = dividerThickness
                )
        } else {
            Modifier
                .testTag(TestTags.LINE_DIVIDER)
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
            val dotCount = (dividerLenght / dividerThickness.toPx()).toInt()
            val gapFactor = 2

            // even count of dots, odd count of gaps
            val dotAndGapCount = (dotCount * gapFactor) - 1
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
                    drawLine( // centered horizontally
                        start = Offset(gapBetweenEdgeAndDivider, size.center.y),
                        end = Offset(size.width - gapBetweenEdgeAndDivider, size.center.y),
                        color = dividerColor,
                        strokeWidth = dividerThickness.toPx(),
                        cap = dividerLineCap,
                        pathEffect = pathEffect
                    )
                } else {
                    drawLine( // centered vertically
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

//TODO_LATER: introduce outline variant => appropriate for 7-seg outline styles
@Composable
fun ColonDivider(
    clockBoxSize: IntSize,
    dividerThickness: Dp,
    dividerColor: Color,
    firstCirclePosition: Float = .33f,
    secondCirclePosition: Float = .66f,
    dividerRotateAngle: Float = 0f,
    orientation: Int
) {
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Canvas(
            modifier = Modifier
                .testTag(TestTags.COLON_DIVIDER)
                .size(width = clockBoxSize.width.pxToDp(), height = dividerThickness)
        ) {
            drawColon(
                firstCircleCenter = Offset(
                    x = size.width * firstCirclePosition,
                    y = size.center.y
                ),
                secondCircleCenter = Offset(
                    x = size.width * secondCirclePosition,
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
                .size(width = dividerThickness, height = clockBoxSize.height.pxToDp()),
        ) {
            rotate(degrees = dividerRotateAngle, pivot = size.center) {
                drawColon(
                    firstCircleCenter = Offset(
                        size.center.x,
                        size.height * firstCirclePosition
                    ),
                    secondCircleCenter = Offset(
                        size.center.x,
                        size.height * secondCirclePosition
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
