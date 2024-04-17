package de.oljg.glac.test.isolated

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import de.oljg.glac.clock.digital.ui.DigitalClockLandscapeLayout
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.evaluateScreenDetails

/**
 * "Parameterized" DigitalClockLandscapeLayout where test device's screen size is used as
 * clockBoxSize.
 */
@Composable
fun DigitalClockLandscapeLayoutIsolated(
    currentTimeFormatted: String,
    dividerStyle: DividerStyle
) {
    DigitalClockLandscapeLayout(
        currentTimeFormatted = currentTimeFormatted,
        clockBoxSize = evaluateScreenDetails().screenSize,
        dividerStyle = dividerStyle
    ) { char, finalFontSize, clockCharColor, _, _ ->
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