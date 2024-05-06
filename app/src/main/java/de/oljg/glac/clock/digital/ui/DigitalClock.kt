package de.oljg.glac.clock.digital.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_CLOCK_DIGIT_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_CLOCK_PADDING
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockPartsColors
import de.oljg.glac.clock.digital.ui.utils.DividerAttributes
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_HOURS_MINUTES_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.defaultClockCharColors
import de.oljg.glac.clock.digital.ui.utils.evaluateDividerRotateAngle

@Composable
fun DigitalClock(
    previewMode: Boolean = false,
    onClick: () -> Unit = {},
    hoursMinutesDividerChar: Char = DEFAULT_HOURS_MINUTES_DIVIDER_CHAR,
    minutesSecondsDividerChar: Char = DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR,
    daytimeMarkerDividerChar: Char = DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR,
    fontFamily: FontFamily = FontFamily.SansSerif,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    dividerAttributes: DividerAttributes =
        DividerAttributes(dividerColor = MaterialTheme.colorScheme.onSurface),
    currentTimeFormatted: String,
    clockCharType: ClockCharType = ClockCharType.FONT,
    sevenSegmentStyle: SevenSegmentStyle = SevenSegmentStyle.REGULAR,
    charColors: Map<Char, Color> = defaultClockCharColors(MaterialTheme.colorScheme.onSurface),
    clockPartsColors: ClockPartsColors? = null,
    digitSizeFactor: Float = DEFAULT_CLOCK_DIGIT_SIZE_FACTOR,
    daytimeMarkerSizeFactor: Float = DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,
    clockChar: @Composable (Char, TextUnit, Color, DpSize) -> Unit
) {
    val currentDisplayOrientation = LocalConfiguration.current.orientation
    var clockBoxSize by remember { mutableStateOf(IntSize.Zero) }

    // In case of 7-seg italic style and only in landscape o. => rotate divider appropriately
    val dividerRotateAngle =
        if (clockCharType == ClockCharType.SEVEN_SEGMENT)
            evaluateDividerRotateAngle(sevenSegmentStyle)

        /**
         * No need to rotate dividers with ClockCharType.FONT (fonts have unknown/different italic
         * angles).
         * TODO_LATER: allow anyways, but let user rotate manually
         */
        else 0f

    val finalDividerAttributes = dividerAttributes.copy(
        dividerRotateAngle = dividerRotateAngle,
    )

    Box(
        modifier = Modifier
            .padding(DEFAULT_CLOCK_PADDING)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = if (previewMode) 2.dp else 0.dp,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
            .onGloballyPositioned { layoutCoordinates ->
                clockBoxSize = layoutCoordinates.size
            }
            .clickable(enabled = true, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (currentDisplayOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            DigitalClockLandscapeLayout(
                previewMode = previewMode,
                clockBoxSize = clockBoxSize,
                hoursMinutesDividerChar = hoursMinutesDividerChar,
                minutesSecondsDividerChar = minutesSecondsDividerChar,
                daytimeMarkerDividerChar = daytimeMarkerDividerChar,
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                currentTimeFormatted = currentTimeFormatted,
                dividerAttributes = finalDividerAttributes,
                charColors = charColors,
                clockPartsColors = clockPartsColors,
                digitSizeFactor = digitSizeFactor,
                daytimeMarkerSizeFactor = daytimeMarkerSizeFactor,
                clockCharType = clockCharType,
                clockChar = clockChar
            )
        } else { //TODO_LATER: maybe introduce a landscapeInPortraitMode (maybe it's big/readable enough with just 'hh:mm'?(or whatever a user might want to have here :>)
            DigitalClockPortraitLayout(
                previewMode = previewMode,
                clockBoxSize = clockBoxSize,
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                /**
                 * Remove dividers, e.g. 'hh:mm' => 'hhmm'..., because in portrait orientation
                 * it's not useful to show DividerStyle.CHAR dividers (they would have to be rotated,
                 * and this would look kinda terrible imho, and it's not necessary at all.
                 * Instead, a drawn colon will be used by default, or DividerStyle.LINE... are
                 * possible as well).
                 */
                currentTimeWithoutSeparators = buildString {
                    currentTimeFormatted.split(
                        minutesSecondsDividerChar,
                        hoursMinutesDividerChar,
                        daytimeMarkerDividerChar
                    ).forEach { notASeparatorChar -> append(notASeparatorChar) }
                },
                dividerAttributes = finalDividerAttributes,
                charColors = charColors,
                clockPartsColors = clockPartsColors,
                digitSizeFactor = digitSizeFactor,
                daytimeMarkerSizeFactor = daytimeMarkerSizeFactor,
                clockCharType = clockCharType,
                clockChar = clockChar
            )
        }
    }
}

