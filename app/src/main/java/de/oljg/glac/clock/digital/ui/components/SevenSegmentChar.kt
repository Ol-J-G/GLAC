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
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_STROKE_WIDTH_BOLD
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_STROKE_WIDTH_LIGHT
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_STROKE_WIDTH_REGULAR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_STROKE_WIDTH_SEMIBOLD
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_STROKE_WIDTH_THIN
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_WEIGHT_FACTOR_BOLD
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_WEIGHT_FACTOR_LIGHT
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_WEIGHT_FACTOR_REGULAR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_WEIGHT_FACTOR_SEMIBOLD
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_WEIGHT_FACTOR_THIN
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_ITALIC_FACTOR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_SEVEN_SEGMENT_CHAR_PADDING
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.SEVEN_SEGMENT_CHARS
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.clock.digital.ui.utils.contains
import de.oljg.glac.clock.digital.ui.utils.defaultSegmentColors
import de.oljg.glac.clock.digital.ui.utils.evaluateTransformationMatrix
import de.oljg.glac.clock.digital.ui.utils.isSevenSegmentChar
import de.oljg.glac.clock.digital.ui.utils.setSpecifiedColors
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
                )
            }
        }
    }
}


@Composable
fun SevenSegmentChar(
    modifier: Modifier = Modifier,
    char: Char,
    charColor: Color = MaterialTheme.colorScheme.onSurface,
    segmentColors: Map<Segment, Color> = emptyMap(),
    style: SevenSegmentStyle = SevenSegmentStyle.REGULAR,
    weight: SevenSegmentWeight = SevenSegmentWeight.REGULAR,
    strokeWidth: Float? = null,
    charSize: DpSize? = null,
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
        SevenSegmentWeight.THIN -> DEFAULT_WEIGHT_FACTOR_THIN
        SevenSegmentWeight.LIGHT -> DEFAULT_WEIGHT_FACTOR_LIGHT
        SevenSegmentWeight.REGULAR -> DEFAULT_WEIGHT_FACTOR_REGULAR
        SevenSegmentWeight.SEMIBOLD -> DEFAULT_WEIGHT_FACTOR_SEMIBOLD
        SevenSegmentWeight.BOLD -> DEFAULT_WEIGHT_FACTOR_BOLD
    }

    val defaultStrokeWidth = when (weight) {
        SevenSegmentWeight.THIN -> DEFAULT_STROKE_WIDTH_THIN
        SevenSegmentWeight.LIGHT -> DEFAULT_STROKE_WIDTH_LIGHT
        SevenSegmentWeight.REGULAR -> DEFAULT_STROKE_WIDTH_REGULAR
        SevenSegmentWeight.SEMIBOLD -> DEFAULT_STROKE_WIDTH_SEMIBOLD
        SevenSegmentWeight.BOLD -> DEFAULT_STROKE_WIDTH_BOLD
    }
    val finalStrokeWidth = strokeWidth ?: defaultStrokeWidth

    val finalSegmentColors =
            setSpecifiedColors(segmentColors, defaultSegmentColors(charColor))

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
            if(finalChar.contains(Segment.TOP))
                draw(topSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.TOP), style, finalStrokeWidth)

            if(finalChar.contains(Segment.CENTER))
                draw(centerSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.CENTER), style, finalStrokeWidth)

            if(finalChar.contains(Segment.BOTTOM))
                draw(bottomSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.BOTTOM),style,finalStrokeWidth)

            if(finalChar.contains(Segment.TOP_LEFT))
                draw(topLeftSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.TOP_LEFT), style, finalStrokeWidth)

            if(finalChar.contains(Segment.TOP_RIGHT))
                draw(topRightSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.TOP_RIGHT), style, finalStrokeWidth)

            if(finalChar.contains(Segment.BOTTOM_LEFT))
                draw(bottomLeftSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.BOTTOM_LEFT), style, finalStrokeWidth)

            if(finalChar.contains(Segment.BOTTOM_RIGHT))
                draw(bottomRightSegment(sizeConditions),
                    finalSegmentColors.getValue(Segment.BOTTOM_RIGHT), style, finalStrokeWidth)
            // @formatter:on
        }
    }
}


//TODO: (later) enable both combined? (draw Filled first then Stroke upon, to have e.g a char with outline and colored BG (instead of transparent currently at OUTLINE))
private fun DrawScope.draw(
    segment: Path,
    color: Color,
    style: SevenSegmentStyle,
    strokeWidth: Float
) {
    drawPath(
        path = segment,
        color = color,
        style =
            if (style.name.startsWith(SevenSegmentStyle.OUTLINE.name)) Stroke(width = strokeWidth)
            else Fill
    )
}
