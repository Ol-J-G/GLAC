package de.oljg.glac.clock.digital.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_CLOCK_CHAR_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_CLOCK_PADDING
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockPartColors
import de.oljg.glac.clock.digital.ui.utils.DividerAttributes
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DASH_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DASH_DOTTED_PART_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DIVIDER_LENGTH_FACTOR
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.evaluateDividerPadding
import de.oljg.glac.clock.digital.ui.utils.evaluateDividerThickness
import de.oljg.glac.clock.digital.ui.utils.evaluateStartFontSize

@Composable
fun DigitalClock(
    onClick: () -> Unit,
    hourMinuteDividerChar: Char,
    minuteSecondDividerChar: Char,
    daytimeMarkerDividerChar: Char,
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    fontStyle: FontStyle,
    dividerAttributes: DividerAttributes,
    currentTimeFormatted: String,
    dividerCount: Int,
    clockCharType: ClockCharType,
    sevenSegmentStyle: SevenSegmentStyle,
    charColors: Map<Char, Color>,
    clockPartColors: ClockPartColors? = null,
    clockCharSizeFactor: Float,
    daytimeMarkerSizeFactor: Float,
    clockChar: @Composable (Char, TextUnit, Color, DpSize, Int) -> Unit
) {
    var clockBoxSize by remember { mutableStateOf(IntSize.Zero) }
    val currentDisplayOrientation = LocalConfiguration.current.orientation

    // Use specified or default divider thickness
    val finalDividerThickness = evaluateDividerThickness(
        specifiedDividerStyle = dividerAttributes.dividerStyle,
        specifiedDividerThickness = dividerAttributes.dividerThickness,
        dividerCount = dividerCount,
        clockBoxSize = clockBoxSize,
        currentDisplayOrientation = currentDisplayOrientation
    )

    // Use specified or default divider padding
    val finalDividerPadding = evaluateDividerPadding(
        specifiedDividerStyle = dividerAttributes.dividerStyle,
        specifiedDividerPadding = dividerAttributes.dividerPadding,
        finalDividerThickness = finalDividerThickness,
        specifiedDividerThickness = dividerAttributes.dividerThickness
    )

    /**
     * Use specified or default number of dashes in case of DividerStyle.DASHED
     * (Zero or negative value would make no sense!)
     */
    val finalDashCount =
        if (dividerAttributes.dividerDashCount == null ||
            dividerAttributes.dividerDashCount <= 0)
            DEFAULT_DASH_COUNT
        else dividerAttributes.dividerDashCount

    /**
     * Use specified or default dash-dotted parts in case of DividerStyle.DASHDOTTED
     * (Zero or negative value would make no sense!)
     */
    val finalDashDottedPartCount =
        if (dividerAttributes.dividerDashDottedPartCount == null ||
            dividerAttributes.dividerDashDottedPartCount <= 0)
            DEFAULT_DASH_DOTTED_PART_COUNT
        else dividerAttributes.dividerDashDottedPartCount

    /**
     * Use specified param or default value, in case specified param is not
     * in range 0.0f .. 1.0f => %
     */
    val finalDividerLengthPercent =
        if (dividerAttributes.dividerLengthPercent == null ||
            dividerAttributes.dividerLengthPercent <= 0f ||
            dividerAttributes.dividerLengthPercent > 1f)
            DEFAULT_DIVIDER_LENGTH_FACTOR
        else dividerAttributes.dividerLengthPercent
    val finalClockCharSizeFactor = //TODO: maybe allow > 1f in case font measurement went unlucky on some device!? but what should be max instead?
        if (clockCharSizeFactor <= 0f || clockCharSizeFactor > 1f)
            DEFAULT_CLOCK_CHAR_SIZE_FACTOR
        else clockCharSizeFactor
    val finalDaytimeMarkerSizeFactor =
        if (daytimeMarkerSizeFactor <= 0f || daytimeMarkerSizeFactor > 1f)
            DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
        else daytimeMarkerSizeFactor

    val startFontSize = evaluateStartFontSize(currentDisplayOrientation)

    Box(
        modifier = Modifier
            .padding(DEFAULT_CLOCK_PADDING) // TODO: make it configurable => otherwise LINE... divider cannot be from edge to edge without space
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .onGloballyPositioned { layoutCoordinates ->
                clockBoxSize =
                    layoutCoordinates.size
            }.clickable(enabled = true, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (currentDisplayOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            DigitalClockLandscapeLayout(
                hourMinuteDividerChar = hourMinuteDividerChar,
                minuteSecondDividerChar = minuteSecondDividerChar,
                daytimeMarkerDividerChar = daytimeMarkerDividerChar,
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                currentTimeFormatted = currentTimeFormatted,
                clockBoxSize = clockBoxSize,
                dividerStyle = dividerAttributes.dividerStyle,
                dividerDashCount = finalDashCount,
                dividerLineCap = dividerAttributes.dividerLineCap,
                dividerThickness = finalDividerThickness,
                dividerPadding = finalDividerPadding,
                charColors = charColors,
                clockPartColors = clockPartColors,
                dividerColor = dividerAttributes.dividerColor,
                dividerCount = dividerCount,
                dividerLengthPercent = finalDividerLengthPercent,
                dividerDashDottedPartCount = finalDashDottedPartCount,
                startFontSize = startFontSize,
                clockChar = clockChar,
                clockCharType = clockCharType,
                sevenSegmentStyle = sevenSegmentStyle,
                clockCharSizeFactor = finalClockCharSizeFactor,
                daytimeMarkerSizeFactor = finalDaytimeMarkerSizeFactor
            )
        } else { //TODO: maybe introduce a landscapeInPortraitMode (maybe it's big/readable enough with just 'hh:mm'?(or whatever a user might want to have here :>)
            DigitalClockPortraitLayout(
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
                /**
                 * Remove dividers, e.g. 'hh:mm' => 'hhmm'..., because in portrait orientation
                 * it's not useful to show DividerStyle.CHAR dividers (they would have to be rotated,
                 * and this would look kinda terrible imho, and it's not necessary at all.
                 * Instead, a drawn colon will be used by default, or DividerStyle.LINE... are
                 * possible as well).
                 */
                currentTimeWithoutSeparators = buildString {
                    currentTimeFormatted.split(
                        minuteSecondDividerChar,
                        hourMinuteDividerChar,
                        daytimeMarkerDividerChar
                    ).forEach { notASeparatorChar -> append(notASeparatorChar) }
                },
                clockBoxSize = clockBoxSize,
                dividerStyle = dividerAttributes.dividerStyle,
                dividerDashCount = finalDashCount,
                dividerLineCap = dividerAttributes.dividerLineCap,
                dividerThickness = finalDividerThickness,
                dividerPadding = finalDividerPadding,
                charColors = charColors,
                clockPartColors = clockPartColors,
                dividerColor = dividerAttributes.dividerColor,
                dividerCount = dividerCount,
                dividerLengthPercent = finalDividerLengthPercent,
                dividerDashDottedPartCount = finalDashDottedPartCount,
                startFontSize = startFontSize,
                clockChar = clockChar,
                clockCharType = clockCharType,
                clockCharSizeFactor = finalClockCharSizeFactor,
                daytimeMarkerSizeFactor = finalDaytimeMarkerSizeFactor
            )
        }
    }
}

