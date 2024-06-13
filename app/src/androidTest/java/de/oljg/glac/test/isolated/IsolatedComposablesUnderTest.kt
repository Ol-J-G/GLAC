@file:Suppress("TestFunctionName")

package de.oljg.glac.test.isolated

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import de.oljg.glac.clock.digital.ui.DigitalAlarmClockLandscapeLayout
import de.oljg.glac.clock.digital.ui.DigitalAlarmClockPortraitLayout
import de.oljg.glac.clock.digital.ui.components.SevenSegmentChar
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.DividerAttributes
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.core.util.screenDetails

/**
 * "Parameterized" DigitalClockLandscapeLayout where test device's screen size is used as
 * clockBoxSize.
 */
@Composable
fun DigitalClockLandscapeLayoutIsolatedFont(
    currentTimeFormatted: String,
    dividerStyle: DividerStyle
) {
    DigitalAlarmClockLandscapeLayout(
        currentTimeFormatted = currentTimeFormatted,
        clockBoxSize = screenDetails().screenSize,
        dividerAttributes = DividerAttributes(
            dividerStyle = dividerStyle,
            dividerColor = defaultColor(),
            dividerThickness = 2.dp,
        )
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
    DigitalAlarmClockPortraitLayout(
        currentTimeWithoutSeparators = currentTimeWithoutSeparators,
        clockBoxSize = screenDetails().screenSize,
        dividerAttributes = DividerAttributes(
            dividerStyle = dividerStyle,
            dividerColor = defaultColor(),
            dividerThickness = 2.dp,
        )
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
    DigitalAlarmClockLandscapeLayout(
        currentTimeFormatted = currentTimeFormatted,
        clockBoxSize = screenDetails().screenSize,
        dividerAttributes = DividerAttributes(
            dividerStyle = dividerStyle,
            dividerColor = defaultColor(),
            dividerThickness = 2.dp,
        ),
        clockCharType = ClockCharType.SEVEN_SEGMENT
    ) { char, _, clockCharColor, clockCharSize ->
        SevenSegmentChar(
            char = char,
            charColor = clockCharColor,
            segmentColors = emptyMap(),
            style = SevenSegmentStyle.REGULAR,
            weight = SevenSegmentWeight.REGULAR,
            outlineSize = SevenSegmentDefaults.DEFAULT_OUTLINE_SIZE,
            charSize = DpSize(clockCharSize.width, clockCharSize.height),
            drawOffSegments = false,
            clockBackgroundColor = MaterialTheme.colorScheme.surface
        )
    }
}



@Composable
fun DigitalClockPortraitLayoutIsolatedSevenSegment(
    currentTimeWithoutSeparators: String,
    dividerStyle: DividerStyle
) {
    DigitalAlarmClockPortraitLayout(
        currentTimeWithoutSeparators = currentTimeWithoutSeparators,
        clockBoxSize = screenDetails().screenSize,
        dividerAttributes = DividerAttributes(
            dividerStyle = dividerStyle,
            dividerColor = defaultColor(),
            dividerThickness = 2.dp,
        ),
        clockCharType = ClockCharType.SEVEN_SEGMENT
    ) { char, _, clockCharColor, clockCharSize ->
        SevenSegmentChar(
            char = char,
            charColor = clockCharColor,
            segmentColors = emptyMap(),
            style = SevenSegmentStyle.REGULAR,
            weight = SevenSegmentWeight.REGULAR,
            outlineSize = SevenSegmentDefaults.DEFAULT_OUTLINE_SIZE,
            charSize = DpSize(clockCharSize.width, clockCharSize.height),
            drawOffSegments = false,
            clockBackgroundColor = MaterialTheme.colorScheme.surface
        )
    }
}


