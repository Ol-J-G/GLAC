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
import de.oljg.glac.core.utils.defaultColor
import de.oljg.glac.core.utils.screenDetails
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.ui.clock.DigitalAlarmClockLandscapeLayout
import de.oljg.glac.feature_clock.ui.clock.DigitalAlarmClockPortraitLayout
import de.oljg.glac.feature_clock.ui.clock.components.SevenSegmentChar
import de.oljg.glac.feature_clock.ui.clock.utils.ClockCharType
import de.oljg.glac.feature_clock.ui.clock.utils.DividerAttributes
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentDefaults
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentWeight

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
        clockSettings = ClockSettings(),
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
        clockSettings = ClockSettings(),
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
        clockSettings = ClockSettings(),
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
        clockSettings = ClockSettings(),
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


