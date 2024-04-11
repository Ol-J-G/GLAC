package de.oljg.glac.clock.digital.ui.components

import androidx.compose.ui.graphics.Path
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.CENTER_SEGMENT_HEIGHT_FACTOR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.CENTER_SEGMENT_INDENT_FACTOR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.CORNER_FACTOR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.TOP_AND_BOTTOM_SEGMENT_HEIGHT_FACTOR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.TOP_AND_BOTTOM_SEGMENT_INDENT_FACTOR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.VERTICAL_SEGMENT_INNER_CORNER_FACTOR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.VERTICAL_SEGMENT_INNER_INDENT_FACTOR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.VERTICAL_SEGMENT_OUTER_CORNER_FACTOR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.VERTICAL_SEGMENT_WIDTH_FACTOR


// @formatter:off
/**
 *    A _____ ... _____ B   ____0f____
 *    /                \
 * F \                 / C
 *    \_____ ... _____/
 *   E                 D
 * |     INNER SIDE      |
 * |                     |
 * 0f                    canvasWidth
 */
fun topSegment(sizeConditions: Triple<Float, Float, Float>): Path {
    val (canvasWidth, _, weightFactor) = sizeConditions
    val horizontalGap = canvasWidth * weightFactor * 2
    val cornerDistance = horizontalGap * CORNER_FACTOR
    val indent = horizontalGap * TOP_AND_BOTTOM_SEGMENT_INDENT_FACTOR
    val segmentHeight = horizontalGap * TOP_AND_BOTTOM_SEGMENT_HEIGHT_FACTOR
    return Path().apply {
        moveTo(/* A */ horizontalGap, 0f)
        lineTo(/* B */ canvasWidth - horizontalGap, 0f)
        lineTo(/* C */ canvasWidth - horizontalGap + cornerDistance, cornerDistance)
        lineTo(/* D */ canvasWidth - indent, segmentHeight)
        lineTo(/* E */ indent, segmentHeight)
        lineTo(/* F */ horizontalGap - cornerDistance, cornerDistance)
        close()
    }
}


/**
 *     B ____ ... ____ C    ____aboveCenterY___
 *     /              \
 *    /                \    ____centerY____
 * A \                 / D
 *    \_____ ... _____/     ____belowCenterY____
 *   F                 E
 * |                     |
 * |                     |
 * 0f                    canvasWidth
 */
fun centerSegment(sizeConditions: Triple<Float, Float, Float>): Path {
    val (canvasWidth, canvasHeight, weightFactor) = sizeConditions
    val horizontalGap = canvasWidth * weightFactor * 2
    val horizontalGapIndented = horizontalGap * CENTER_SEGMENT_INDENT_FACTOR
    val verticalGap = canvasHeight * weightFactor
    val centerY = canvasHeight / 2
    val aboveCenterY = centerY - verticalGap * CENTER_SEGMENT_HEIGHT_FACTOR
    val belowCenterY = centerY + verticalGap * CENTER_SEGMENT_HEIGHT_FACTOR
    return Path().apply {
        moveTo(/* A */ horizontalGap, centerY)
        lineTo(/* B */ horizontalGapIndented, aboveCenterY)
        lineTo(/* C */ canvasWidth - horizontalGapIndented, aboveCenterY)
        lineTo(/* D */ canvasWidth - horizontalGap, centerY)
        lineTo(/* E */ canvasWidth - horizontalGapIndented, belowCenterY)
        lineTo(/* F */ horizontalGapIndented, belowCenterY)
        close()
    }
}

/**
 *        INNER SIDE
 *    E _____ ... _____ D
 *    /                \
 * F \                 / C
 *    \_____ ... _____/    ___canvasHeight___
 *   A                 B
 * |                     |
 * |                     |
 * |                     |
 * 0f                    canvasWidth
 */
fun bottomSegment(sizeConditions: Triple<Float, Float, Float>): Path {
    val (canvasWidth, canvasHeight, weightFactor) = sizeConditions
    val horizontalGap = canvasWidth * weightFactor * 2
    val cornerDistance = horizontalGap * CORNER_FACTOR
    val indent = horizontalGap * TOP_AND_BOTTOM_SEGMENT_INDENT_FACTOR
    val segmentHeight = horizontalGap * TOP_AND_BOTTOM_SEGMENT_HEIGHT_FACTOR
    return Path().apply {
        moveTo(/* A */ horizontalGap, canvasHeight)
        lineTo(/* B */ canvasWidth - horizontalGap, canvasHeight)
        lineTo(/* C */ canvasWidth - horizontalGap + cornerDistance, canvasHeight - cornerDistance)
        lineTo(/* D */ canvasWidth - indent, canvasHeight - segmentHeight)
        lineTo(/* E */ indent, canvasHeight - segmentHeight)
        lineTo(/* F */ horizontalGap - cornerDistance, canvasHeight - cornerDistance)
        close()
    }
}

/**
 * |_____________________________________________|
 * 0f                                            canvasWidth
 * |    F                                        |
 *     /\
 *    /  \
 * A |    | E
 *   |    |
 *   .    .   INNER SIDE
 *   |    |
 * B |    | D
 *   \   /
 *    \/
 *    C       ____centerY____
 *
 */
fun topLeftSegment(sizeConditions: Triple<Float, Float, Float>): Path {
    val (canvasWidth, canvasHeight, weightFactor) = sizeConditions
    val verticalGap = canvasHeight * weightFactor
    val centerY = canvasHeight / 2
    val segmentWidth = canvasWidth * weightFactor * VERTICAL_SEGMENT_WIDTH_FACTOR
    val innerCornerX = canvasWidth * weightFactor * VERTICAL_SEGMENT_INNER_CORNER_FACTOR
    val innerCornerY = centerY - verticalGap * CORNER_FACTOR
    val outerCornerX = canvasWidth * weightFactor * VERTICAL_SEGMENT_OUTER_CORNER_FACTOR
    val outerCornerY = verticalGap * VERTICAL_SEGMENT_OUTER_CORNER_FACTOR
    val indent = verticalGap * VERTICAL_SEGMENT_INNER_INDENT_FACTOR
    val aboveCenterGap = centerY - verticalGap
    return Path().apply {
        moveTo(/* A */ 0f, verticalGap)
        lineTo(/* B */ 0f, aboveCenterGap)
        lineTo(/* C */ innerCornerX, innerCornerY)
        lineTo(/* D */ segmentWidth, aboveCenterGap)
        lineTo(/* E */ segmentWidth, indent)
        lineTo(/* F */ outerCornerX, outerCornerY)
        close()
    }
}

/**
 * |_____________________________________________|
 * 0f                                            canvasWidth
 *                                            F
 *                                           /\
 *                                          /  \
 *                                       E |    | A
 *                                         |    |
 *                          INNER SIDE     .    .
 *                                         |    |
 *                                       D |    | B
 *                                         \   /
 *                                          \/
 *                        ____centerY____   C
 */
fun topRightSegment(sizeConditions: Triple<Float, Float, Float>): Path {
    val (canvasWidth, canvasHeight, weightFactor) = sizeConditions
    val verticalGap = canvasHeight * weightFactor
    val centerY = canvasHeight / 2
    val segmentWidth = canvasWidth - canvasWidth * weightFactor * VERTICAL_SEGMENT_WIDTH_FACTOR
    val innerCornerX = canvasWidth - canvasWidth * weightFactor * VERTICAL_SEGMENT_INNER_CORNER_FACTOR
    val innerCornerY = centerY - verticalGap * CORNER_FACTOR
    val outerCornerX = canvasWidth - canvasWidth * weightFactor * VERTICAL_SEGMENT_OUTER_CORNER_FACTOR
    val outerCornerY = verticalGap * VERTICAL_SEGMENT_OUTER_CORNER_FACTOR
    val indent = verticalGap * VERTICAL_SEGMENT_INNER_INDENT_FACTOR
    val aboveCenterGap = centerY - verticalGap
    return Path().apply {
        moveTo(/* A */ canvasWidth, verticalGap)
        lineTo(/* B */ canvasWidth, aboveCenterGap)
        lineTo(/* C */ innerCornerX, innerCornerY)
        lineTo(/* D */ segmentWidth, aboveCenterGap)
        lineTo(/* E */ segmentWidth, indent)
        lineTo(/* F */ outerCornerX,  outerCornerY)
        close()
    }
}

/**
 * |_____________________________________________|
 * 0f        ____centerY____                     canvasWidth
 * |    F                                        |
 *     /\
 *    /  \
 * A |    | E
 *   |    |
 *   .    .   INNER SIDE
 *   |    |
 * B |    | D
 *   \   /
 *    \/
 *    C      ____canvasHeight____
 *
 */
fun bottomLeftSegment(sizeConditions: Triple<Float, Float, Float>): Path {
    val (canvasWidth, canvasHeight, weightFactor) = sizeConditions
    val verticalGap = canvasHeight * weightFactor
    val centerY = canvasHeight / 2
    val segmentWidth = canvasWidth * weightFactor * VERTICAL_SEGMENT_WIDTH_FACTOR
    val outerCornerX = canvasWidth * weightFactor * VERTICAL_SEGMENT_OUTER_CORNER_FACTOR
    val outerCornerY = canvasHeight - verticalGap * VERTICAL_SEGMENT_OUTER_CORNER_FACTOR
    val innerCornerX = canvasWidth * weightFactor * VERTICAL_SEGMENT_INNER_CORNER_FACTOR
    val innerCornerY = centerY + verticalGap * CORNER_FACTOR
    val indent = canvasHeight - verticalGap * VERTICAL_SEGMENT_INNER_INDENT_FACTOR
    val belowCenterGap = centerY + verticalGap
    val aboveCanvasHeightGap = canvasHeight - verticalGap
    return Path().apply {
        moveTo(/* A */ 0f, belowCenterGap)
        lineTo(/* B */ 0f, aboveCanvasHeightGap)
        lineTo(/* C */ outerCornerX, outerCornerY)
        lineTo(/* D */ segmentWidth, indent)
        lineTo(/* E */ segmentWidth, belowCenterGap)
        lineTo(/* F */ innerCornerX, innerCornerY)
        close()
    }
}

/**
 * |_____________________________________________|
 * 0f                ____centerY____             canvasWidth
 *                                            F
 *                                           /\
 *                                          /  \
 *                                       E |    | A
 *                                         |    |
 *                          INNER SIDE     .    .
 *                                         |    |
 *                                       D |    | B
 *                                         \   /
 *                                          \/
 *                   ____canvasheight____   C
 */
fun bottomRightSegment(sizeConditions: Triple<Float, Float, Float>): Path {
    val (canvasWidth, canvasHeight, weightFactor) = sizeConditions
    val verticalGap = canvasHeight * weightFactor
    val centerY = canvasHeight / 2
    val segmentWidth = canvasWidth - canvasWidth * weightFactor * VERTICAL_SEGMENT_WIDTH_FACTOR
    val outerCornerX = canvasWidth - canvasWidth * weightFactor * VERTICAL_SEGMENT_OUTER_CORNER_FACTOR
    val outerCornerY = canvasHeight - verticalGap * VERTICAL_SEGMENT_OUTER_CORNER_FACTOR
    val innerCornerX = canvasWidth - canvasWidth * weightFactor * VERTICAL_SEGMENT_INNER_CORNER_FACTOR
    val innerCornerY = centerY + verticalGap * CORNER_FACTOR
    val indent = canvasHeight - verticalGap * VERTICAL_SEGMENT_INNER_INDENT_FACTOR
    val belowCenterGap = centerY + verticalGap
    val aboveCanvasHeightGap = canvasHeight - verticalGap
    return Path().apply {
        moveTo(/* A */ canvasWidth, belowCenterGap)
        lineTo(/* B */ canvasWidth, aboveCanvasHeightGap)
        lineTo(/* C */ outerCornerX, outerCornerY)
        lineTo(/* D */ segmentWidth, indent)
        lineTo(/* E */ segmentWidth, belowCenterGap)
        lineTo(/* F */ innerCornerX, innerCornerY)
        close()
    }
}
// @formatter:on
