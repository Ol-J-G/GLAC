package de.oljg.glac.clock.digital.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import de.oljg.glac.clock.digital.ui.utils.Segment
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_ASPECT_RATIO
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_ITALIC_FACTOR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_OUTLINE_SIZE
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_SEVEN_SEGMENT_CHAR_PADDING
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.OFFCOLOR_LIGHTNESS_DELTA
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.OFFSEGMENT_OUTLINE_STROKE_WIDTH
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.SEVEN_SEGMENT_CHARS
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.WEIGHT_FACTOR_BLACK
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.WEIGHT_FACTOR_BOLD
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.WEIGHT_FACTOR_EXTRABOLD
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.WEIGHT_FACTOR_EXTRALIGHT
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.WEIGHT_FACTOR_LIGHT
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.WEIGHT_FACTOR_MEDIUM
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.WEIGHT_FACTOR_REGULAR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.WEIGHT_FACTOR_SEMIBOLD
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.WEIGHT_FACTOR_THIN
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.clock.digital.ui.utils.contains
import de.oljg.glac.clock.digital.ui.utils.defaultSegmentColors
import de.oljg.glac.clock.digital.ui.utils.evaluateTransformationMatrix
import de.oljg.glac.clock.digital.ui.utils.isOutline
import de.oljg.glac.clock.digital.ui.utils.isSevenSegmentChar
import de.oljg.glac.clock.digital.ui.utils.lightenOrDarken
import de.oljg.glac.clock.digital.ui.utils.setSpecifiedColors
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.ui.theme.GLACTheme

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun PreviewDigit() {
    GLACTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SevenSegmentChar(
                    char = '8',
                    charColor = Color.Yellow,
                    style = SevenSegmentStyle.REGULAR,
                    drawOffSegments = false,
                    clockBackgroundColor = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}


@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun PreviewDigits() {
    GLACTheme {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val numberOfDigits = "01234567890PA"
            numberOfDigits.forEachIndexed { index, char ->
                SevenSegmentChar(
                    Modifier
                        .size(
                            width = maxWidth / numberOfDigits.length,
                            height = maxHeight / numberOfDigits.length
                        )
                        .offset(x = (maxWidth / numberOfDigits.length) * index),
                    char = char,
                    charColor = Color.Yellow,
                    style = SevenSegmentStyle.ITALIC,
                    drawOffSegments = false,
                    clockBackgroundColor = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}


@Composable
fun SevenSegmentChar(
    modifier: Modifier = Modifier,
    char: Char,
    charColor: Color = defaultColor(),
    segmentColors: Map<Segment, Color> = emptyMap(),
    style: SevenSegmentStyle = SevenSegmentStyle.REGULAR,
    weight: SevenSegmentWeight = SevenSegmentWeight.REGULAR,
    outlineSize: Float? = null,
    charSize: DpSize? = null,
    drawOffSegments: Boolean,
    clockBackgroundColor: Color
) {
    val screenOrientation = LocalConfiguration.current.orientation

    // Allow charToDisplay to be 'a' or 'p' and turn it into 'A' or 'P'
    val finalChar = if (char.isLetter()) char.uppercaseChar() else char
    if (!finalChar.isSevenSegmentChar()) // Just contains (uppercase) 'A' and 'P'
        throw IllegalArgumentException(
            "'$char' cannot be displayed as 7-segment character! " +
                    "Valid chars: $SEVEN_SEGMENT_CHARS"
        )

    val aspectRatio =
        if (style.name.contains(SevenSegmentStyle.ITALIC.name)) (1 + DEFAULT_ITALIC_FACTOR) / 2
        else DEFAULT_ASPECT_RATIO

    val weightFactor = when (weight) {
        SevenSegmentWeight.THIN -> WEIGHT_FACTOR_THIN
        SevenSegmentWeight.EXTRA_LIGHT -> WEIGHT_FACTOR_EXTRALIGHT
        SevenSegmentWeight.LIGHT -> WEIGHT_FACTOR_LIGHT
        SevenSegmentWeight.REGULAR -> WEIGHT_FACTOR_REGULAR
        SevenSegmentWeight.MEDIUM -> WEIGHT_FACTOR_MEDIUM
        SevenSegmentWeight.SEMI_BOLD -> WEIGHT_FACTOR_SEMIBOLD
        SevenSegmentWeight.BOLD -> WEIGHT_FACTOR_BOLD
        SevenSegmentWeight.EXTRA_BOLD -> WEIGHT_FACTOR_EXTRABOLD
        SevenSegmentWeight.BLACK -> WEIGHT_FACTOR_BLACK
    }

    val finalOutlineSize = outlineSize ?: DEFAULT_OUTLINE_SIZE

    val finalSegmentColors =
            setSpecifiedColors(segmentColors, defaultSegmentColors(charColor))

    /**
     * "Simulate" the "background" of real 7-Segments built in some real display, where it is
     * commonly rather dark. In this common case, off segements would be lighten for
     * [OFFCOLOR_LIGHTNESS_DELTA] percent.
     *
     * In general, it depends on
     * [de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_LIGHTNESS_THRESHOLD],
     * if [clockBackgroundColor] will become a bit lighter or darker, to serve as offColor.
     */
    val offColor = clockBackgroundColor.lightenOrDarken()

    // Lighten or darken the outline of an off segment a bit more that offColor
    val offOutlineColor =
            clockBackgroundColor.lightenOrDarken(amount = OFFCOLOR_LIGHTNESS_DELTA * 8)

    Canvas(
        modifier =
        if (charSize != null)
            modifier
                .padding(DEFAULT_SEVEN_SEGMENT_CHAR_PADDING)
                .size(charSize.width, charSize.height)
                .aspectRatio(aspectRatio)
                .background(Color.Transparent)
                .semantics { contentDescription = char.toString() }
        else
            modifier
                .padding(DEFAULT_SEVEN_SEGMENT_CHAR_PADDING)
                .fillMaxSize()
                .aspectRatio(aspectRatio)
                .background(Color.Transparent)
                .semantics { contentDescription = char.toString() }
    ) {

        val (canvasWidth, canvasHeight) = Pair(size.width, size.height)
        val sizeConditions = Triple(canvasWidth, canvasHeight, weightFactor)
        val transformationMatrix = evaluateTransformationMatrix(style, canvasWidth)

        withTransform({
            transform(matrix = transformationMatrix)

            /**
             * To compensate the clockChar width after skew transformation, it's necessary to
             * scale it down in case of landscape orientation clock display.
             */
            if (style.name.contains(SevenSegmentStyle.ITALIC.name) &&
                screenOrientation == Configuration.ORIENTATION_LANDSCAPE
            )
                scale(1f - DEFAULT_ITALIC_FACTOR, size.center)
        }) {
            // @formatter:off
            if(drawOffSegments)
                drawOffSegments(sizeConditions, offColor, offOutlineColor, style)

            if(finalChar.contains(Segment.TOP))
                draw(topSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.TOP), style, finalOutlineSize)

            if(finalChar.contains(Segment.CENTER))
                draw(centerSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.CENTER), style, finalOutlineSize)

            if(finalChar.contains(Segment.BOTTOM))
                draw(bottomSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.BOTTOM),style, finalOutlineSize)

            if(finalChar.contains(Segment.TOP_LEFT))
                draw(topLeftSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.TOP_LEFT), style, finalOutlineSize)

            if(finalChar.contains(Segment.TOP_RIGHT))
                draw(topRightSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.TOP_RIGHT), style, finalOutlineSize)

            if(finalChar.contains(Segment.BOTTOM_LEFT))
                draw(bottomLeftSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.BOTTOM_LEFT), style, finalOutlineSize)

            if(finalChar.contains(Segment.BOTTOM_RIGHT))
                draw(bottomRightSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.BOTTOM_RIGHT), style, finalOutlineSize)
            // @formatter:on
        }
    }
}


private fun DrawScope.draw(
    segment: Path,
    color: Color,
    style: SevenSegmentStyle,
    strokeWidth: Float? = null,
    drawOffSegmentOutline: Boolean = false
) {
    drawPath(
        path = segment,
        color = color,
        style =
            when {
                drawOffSegmentOutline -> Stroke(OFFSEGMENT_OUTLINE_STROKE_WIDTH)
                strokeWidth != null && style.isOutline() -> Stroke(strokeWidth)
                else -> Fill
            }
    )
}


private fun DrawScope.drawOffSegments(
    sizeConditions: Triple<Float, Float, Float>,
    offColor: Color,
    offOutlineColor: Color,
    style: SevenSegmentStyle
) {
    draw(topSegment(sizeConditions), offColor, style)
    draw(centerSegment(sizeConditions), offColor, style)
    draw(bottomSegment(sizeConditions), offColor, style)
    draw(topLeftSegment(sizeConditions), offColor, style)
    draw(topRightSegment(sizeConditions), offColor, style)
    draw(bottomLeftSegment(sizeConditions), offColor, style)
    draw(bottomRightSegment(sizeConditions), offColor, style)
    draw(topSegment(sizeConditions), offOutlineColor, style, drawOffSegmentOutline = true)
    draw(centerSegment(sizeConditions), offOutlineColor, style, drawOffSegmentOutline = true)
    draw(bottomSegment(sizeConditions), offOutlineColor, style, drawOffSegmentOutline = true)
    draw(topLeftSegment(sizeConditions), offOutlineColor, style, drawOffSegmentOutline = true)
    draw(topRightSegment(sizeConditions), offOutlineColor, style, drawOffSegmentOutline = true)
    draw(bottomLeftSegment(sizeConditions), offOutlineColor, style, drawOffSegmentOutline = true)
    draw(bottomRightSegment(sizeConditions), offOutlineColor, style, drawOffSegmentOutline = true)
}

