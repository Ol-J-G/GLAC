@file:Suppress("TestFunctionName")

package de.oljg.glac.test.isolated

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import de.oljg.glac.clock.digital.ui.DigitalClockLandscapeLayout
import de.oljg.glac.clock.digital.ui.DigitalClockPortraitLayout
import de.oljg.glac.clock.digital.ui.components.SevenSegmentChar
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.clock.digital.ui.utils.evaluateScreenDetails

/**
 * "Parameterized" DigitalClockLandscapeLayout where test device's screen size is used as
 * clockBoxSize.
 */
@Composable
fun DigitalClockLandscapeLayoutIsolatedFont(
    currentTimeFormatted: String,
    dividerStyle: DividerStyle
) {
    DigitalClockLandscapeLayout(
        currentTimeFormatted = currentTimeFormatted,
        clockBoxSize = evaluateScreenDetails().screenSize,
        dividerStyle = dividerStyle
    ) { char, finalFontSize, clockCharColor, _ ->
        Text(
            text = char.toString(),
            fontSize = finalFontSize,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            color = clockCharColor,
        )
    }
}

@Composable
fun DigitalClockPortraitLayoutIsolatedFont(
    currentTimeWithoutSeparators: String,
    dividerStyle: DividerStyle
) {
    DigitalClockPortraitLayout(
        currentTimeWithoutSeparators = currentTimeWithoutSeparators,
        clockBoxSize = evaluateScreenDetails().screenSize,
        dividerStyle = dividerStyle
    ) { char, finalFontSize, clockCharColor, _ ->
        Text(
            text = char.toString(),
            fontSize = finalFontSize,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            color = clockCharColor,
        )
    }
}


@Composable
fun DigitalClockLandscapeLayoutIsolatedSevenSegment(
    currentTimeFormatted: String,
    dividerStyle: DividerStyle
) {
    DigitalClockLandscapeLayout(
        currentTimeFormatted = currentTimeFormatted,
        clockBoxSize = evaluateScreenDetails().screenSize,
        dividerStyle = dividerStyle,
        clockCharType = ClockCharType.SEVEN_SEGMENT
    ) { char, _, clockCharColor, clockCharSize ->
        SevenSegmentChar(
            char = char,
            charSize = DpSize(clockCharSize.width, clockCharSize.height),
            charColor = clockCharColor,
            segmentColors = emptyMap(),
            style = SevenSegmentStyle.REGULAR,
            weight = SevenSegmentWeight.REGULAR,
            strokeWidth = SevenSegmentDefaults.DEFAULT_STROKE_WIDTH_REGULAR
        )
    }
}



@Composable
fun DigitalClockPortraitLayoutIsolatedSevenSegment(
    currentTimeWithoutSeparators: String,
    dividerStyle: DividerStyle
) {
    DigitalClockPortraitLayout(
        currentTimeWithoutSeparators = currentTimeWithoutSeparators,
        clockBoxSize = evaluateScreenDetails().screenSize,
        dividerStyle = dividerStyle,
        clockCharType = ClockCharType.SEVEN_SEGMENT
    ) { char, _, clockCharColor, clockCharSize ->
        SevenSegmentChar(
            char = char,
            charSize = DpSize(clockCharSize.width, clockCharSize.height),
            charColor = clockCharColor,
            segmentColors = emptyMap(),
            style = SevenSegmentStyle.REGULAR,
            weight = SevenSegmentWeight.REGULAR,
            strokeWidth = SevenSegmentDefaults.DEFAULT_STROKE_WIDTH_REGULAR
        )
    }
}


