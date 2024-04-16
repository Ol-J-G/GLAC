package de.oljg.glac.test.isolated

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import de.oljg.glac.clock.digital.ui.DigitalClockLandscapeLayout
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults
import de.oljg.glac.ui.theme.GLACTheme

/**
 * "Parameterized" DigitalClockLandscapeLayout wrapped with a Box, same as in
 * DigitalClock as test environment, since clockBoxSize is necessary to drive
 * DigitalClockLandscapeLayout...
 */
@Composable
fun DigitalClockLandscapeLayoutIsolated(currentTimeFormatted: String) {
    GLACTheme {
        var clockBoxSize by remember { mutableStateOf(IntSize.Zero) }
        Box(
            modifier = Modifier
                .padding(ClockDefaults.DEFAULT_CLOCK_PADDING)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .onGloballyPositioned { layoutCoordinates ->
                    clockBoxSize = layoutCoordinates.size
                },
            contentAlignment = Alignment.Center
        ) {
            DigitalClockLandscapeLayout(
                currentTimeFormatted = currentTimeFormatted,
                clockBoxSize = clockBoxSize
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
    }
}