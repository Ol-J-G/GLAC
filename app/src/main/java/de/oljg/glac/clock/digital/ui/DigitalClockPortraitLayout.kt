package de.oljg.glac.clock.digital.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import de.oljg.glac.clock.digital.ui.components.ColonDivider
import de.oljg.glac.clock.digital.ui.components.LineDivider
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.WIDEST_CHAR
import de.oljg.glac.clock.digital.ui.utils.ClockPartColors
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_FIRST_CIRCLE_POSITION_PORTRAIT_SPECIAL
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_SECOND_CIRCLE_POSITION_PORTRAIT_SPECIAL
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.MeasureFontSize
import de.oljg.glac.clock.digital.ui.utils.isDaytimeMarkerChar
import de.oljg.glac.clock.digital.ui.utils.pxToDp



@Composable
fun DigitalClockPortraitLayout(
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    fontStyle: FontStyle,
    charColors: Map<Char, Color>,
    currentTimeWithoutSeparators: String,
    clockBoxSize: IntSize,
    dividerStyle: DividerStyle,
    dividerDashCount: Int,
    dividerLineCap: StrokeCap,
    dividerThickness: Dp,
    dividerPadding: Dp,
    dividerColor: Color,
    dividerCount: Int,
    dividerLengthPercent: Float,
    dividerDashDottedPartCount: Int,
    startFontSize: TextUnit,
    clockChar: @Composable (Char, TextUnit, Color, DpSize, Int) -> Unit,
    clockCharType: ClockCharType,
    clockPartColors: ClockPartColors?,
    clockCharSizeFactor: Float,
    daytimeMarkerSizeFactor: Float
) {
    var maxFontSize by remember { mutableStateOf(startFontSize) }
    var finalFontBoundsSize by remember {
        mutableStateOf(IntSize(0, 0))
    }

    if (clockCharType == ClockCharType.FONT) {
        /**
         * Calculate biggest font size that fits into clockBox container in portrait layout.
         */
        MeasureFontSize( //TODO: make it invisible after measurement => try if maxFontSize == startFontSize .. else Box{}
            textToMeasure = buildString {
                (1..2).forEach { _ -> append(WIDEST_CHAR) } // e.g. 'MM' => 2
            },
            fontFamily = fontFamily,
            fontSize = startFontSize,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            clockBoxSize = clockBoxSize,
            dividerCount = dividerCount,
            isOrientationPortrait = true,
            dividerStrokeWithToTakeIntoAccount =
            if (dividerStyle != DividerStyle.NONE) dividerThickness else 0.dp,

            dividerPaddingToTakeIntoAccount =
            if (dividerStyle != DividerStyle.NONE) dividerPadding else 0.dp,

            onFontSizeMeasured = { measuredFontSize, measuredSize ->
                maxFontSize = measuredFontSize
                finalFontBoundsSize = measuredSize
            }
        )
    }

    val spaceNeededForDividers =
        if (dividerStyle != DividerStyle.NONE)
            ((2 * dividerPadding + dividerThickness) * dividerCount)
        else 0.dp

    /**
     * Substract the space needed for dividers from available height in case of 7-segment clockChar
     * (Just calculate it in case no FONT clockChar is used to save a tiny bit power :>)
     */
    val availableHeightForSevenSegmentClockChar = if (clockCharType == ClockCharType.FONT) 0.dp else
        clockBoxSize.height.pxToDp() - spaceNeededForDividers

    /**
     * Shrink 7-seg chars a bit to let space for padding because requiredSize() in columns below is
     * needed (=> strange behaviour noticed with size(), when Jetpack Compose decides...)
     */
    val sevenSegmentShrinkFactor = if (dividerCount == 1) .9f else .95f

    val maxCharWidth = when (clockCharType) {

        // A row contains two columns with one FONT digit each
        ClockCharType.FONT -> finalFontBoundsSize.width.pxToDp() / 2

        // Width of 7-segment must be height / 2 to keep aspect ratio of 1:2 (w:h)
        else -> ((availableHeightForSevenSegmentClockChar /
                (dividerCount + 1)) / 2) * sevenSegmentShrinkFactor
    }
    val maxCharHeight = when (clockCharType) {

        ClockCharType.FONT -> finalFontBoundsSize.height.pxToDp()

        // Height of 7-segment clockChar depends on number of dividers
        else -> (availableHeightForSevenSegmentClockChar /
                (dividerCount + 1)) * sevenSegmentShrinkFactor
    }

    val finalCharWidth = maxCharWidth * clockCharSizeFactor
    val finalCharHeight = maxCharHeight * clockCharSizeFactor

    val daytimeMarkerCharWidth = maxCharWidth * daytimeMarkerSizeFactor
    val daytimeMarkerCharHeight = maxCharHeight * daytimeMarkerSizeFactor

    val finalFontSize = maxFontSize * clockCharSizeFactor
    val daytimeMarkerFontSize = maxFontSize * daytimeMarkerSizeFactor

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                contentDescription =
                    context.getString(de.oljg.glac.R.string.digital_clock_in_portrait_layout)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly //TODO: let user decide (same in landscape) SpaceEvenly should be default
    ) {
        currentTimeWithoutSeparators.forEachIndexed { index, char ->

            // In case [clockPartColors] are specified, use them instead of [charColors].
            val clockPartColor =
                if (clockPartColors != null) evaluateClockCharColor(
                    formattedTime = currentTimeWithoutSeparators,
                    index = index,
                    clockPartColors = clockPartColors
                )
                else null

            val finalDividerColor =
                if (clockPartColors != null) evaluateDividerColor(
                    formattedTime = currentTimeWithoutSeparators,
                    index = index,
                    clockPartColors = clockPartColors,
                )
                else dividerColor


            // Draw 'A' or 'P' ONLY in case of ClockCharType.SEVEN_SEGMENT in last row as single column
            if (index == currentTimeWithoutSeparators.length - 1 && clockCharType == ClockCharType.SEVEN_SEGMENT && char.isLetter()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column( // 1st digit
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .requiredSize(
                                if (currentTimeWithoutSeparators[index].isDaytimeMarkerChar()) daytimeMarkerCharWidth else finalCharWidth,
                                if (currentTimeWithoutSeparators[index].isDaytimeMarkerChar()) daytimeMarkerCharHeight else finalCharHeight
                            )
                    ) {
                        clockChar(
                            currentTimeWithoutSeparators[index],
                            if (currentTimeWithoutSeparators[index].isDaytimeMarkerChar()) daytimeMarkerFontSize else finalFontSize,
                            clockPartColor?.first ?: charColors.getValue(
                                currentTimeWithoutSeparators[index]
                            ),
                            DpSize(finalCharWidth, finalCharHeight),
                            Configuration.ORIENTATION_PORTRAIT
                        )
                    }
                }
            }

            if (index + 1 == currentTimeWithoutSeparators.length) return@forEachIndexed

            if (index % 2 == 0) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column( // 1st digit
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .requiredSize(
                                if (currentTimeWithoutSeparators[index].isDaytimeMarkerChar()) daytimeMarkerCharWidth else finalCharWidth,
                                if (currentTimeWithoutSeparators[index].isDaytimeMarkerChar()) daytimeMarkerCharHeight else finalCharHeight
                            )
                    ) {
                        clockChar(
                            currentTimeWithoutSeparators[index],
                            if (currentTimeWithoutSeparators[index].isDaytimeMarkerChar()) daytimeMarkerFontSize else finalFontSize,
                            clockPartColor?.first ?: charColors.getValue(
                                currentTimeWithoutSeparators[index]
                            ),
                            DpSize(finalCharWidth, finalCharHeight),
                            Configuration.ORIENTATION_PORTRAIT
                        )
                    }
                    Column( // 2nd digit
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .requiredSize(
                                if (currentTimeWithoutSeparators[index + 1].isDaytimeMarkerChar()) daytimeMarkerCharWidth else finalCharWidth,
                                if (currentTimeWithoutSeparators[index + 1].isDaytimeMarkerChar()) daytimeMarkerCharHeight else finalCharHeight
                            )
                    ) {
                        clockChar(
                            currentTimeWithoutSeparators[index + 1],
                            if (currentTimeWithoutSeparators[index + 1].isDaytimeMarkerChar()) daytimeMarkerFontSize else finalFontSize,
                            clockPartColor?.second ?: charColors.getValue(
                                currentTimeWithoutSeparators[index + 1]
                            ),
                            DpSize(finalCharWidth, finalCharHeight),
                            Configuration.ORIENTATION_PORTRAIT
                        )
                    }
                }

                if (index + 1 < currentTimeWithoutSeparators.length - 1) { // don't draw a divider at bottom of screen
                    if (dividerStyle != DividerStyle.NONE) {
                        if (dividerStyle != DividerStyle.COLON && dividerStyle != DividerStyle.CHAR) {
                            LineDivider(
                                dividerPadding = dividerPadding,
                                dividerThickness = dividerThickness,
                                clockBoxSize = clockBoxSize,
                                dividerDashCount = dividerDashCount,
                                dividerColor = finalDividerColor ?: dividerColor,
                                dividerStyle = dividerStyle,
                                dividerLineCap = dividerLineCap,
                                orientation = Configuration.ORIENTATION_PORTRAIT,
                                dividerLengthPercent = dividerLengthPercent,
                                dividerDashDottedPartCount = dividerDashDottedPartCount
                            )
                        } else {
                            /**
                             * Draw a colon-like divider (also as replacement for
                             * DividerStyle.CHAR! => otherwise FONT chars would have
                             * to be rotated => kinda ugly/weird imho)
                             */
                            ColonDivider(
                                dividerPadding = dividerPadding,
                                clockBoxSize = clockBoxSize,
                                dividerThickness = dividerThickness,
                                dividerColor = finalDividerColor ?: dividerColor,
                                orientation = Configuration.ORIENTATION_PORTRAIT,

                                /**
                                 * Special kind of 'colon-circle' placement
                                 * => 10% distance from the edges, is quite in harmony
                                 * (imho)
                                 */
                                firstCirclePositionPercent =
                                DEFAULT_FIRST_CIRCLE_POSITION_PORTRAIT_SPECIAL,
                                secondCirclePositionPercent =
                                DEFAULT_SECOND_CIRCLE_POSITION_PORTRAIT_SPECIAL
                            )
                        }
                    } else Box {} // don't draw any divider, draw "nothing" ...
                }
            }
        }
    }
}

/**
 * Used to override default/specified clock char colors for portrait layout clock.
 * This way, a user can set different colors for different "parts" of the clock, where "parts"
 * means 'hours' (tens and ones), 'minutes' (tens and ones), 'seconds' (tens and ones) or
 * 'daytime marker' ((A or P) and M).
 * E.g. hours.tens in yellow, minutes.ones in green, seconds.tens as well as seconds.ones in red,
 * daytimeMarker.anteOrPost in white and dayTime.Marker.meridiem in gray.
 *
 * Example: [formattedTime] = "123456AM", indices: 01 23 45 67 (0=hours.tens, 1=hours.ones, etc.)
 *
 * Special cases:
 * - Without seconds => E.g.: [formattedTime] = "1234PM"
 * => [formattedTime].length == 6 && last char of [formattedTime] is letter
 *
 * - Without seconds 7-segment => E.g.: [formattedTime] = "1234A" or "1234P"
 * => [formattedTime].length == 5 && last char of [formattedTime] is letter
 *
 * @param formattedTime Current time formatted
 * @param index Position of a clockChar within [formattedTime]
 * @param clockPartColors Contains colors for all "parts" of a clock (except dividers
 * => these colors will be evaluated with evaluateDividerColor())
 * @return A pair of colors for one clock part, depending on the [index] of a clockChar
 * in [formattedTime] (a Pair because of the design of the for loop it is used in - Pair.first,
 * Pair.second comes in handy ...)
 * @see ClockPartColors
 */
private fun evaluateClockCharColor(
    formattedTime: String,
    index: Int,
    clockPartColors: ClockPartColors
): Pair<Color, Color>? {
    return when (index) {
        0 -> Pair(clockPartColors.hours.tens, clockPartColors.hours.ones)
        2 -> Pair(clockPartColors.minutes.tens, clockPartColors.minutes.ones)
        4 ->
            if (formattedTime.length in (5..6) && formattedTime.toCharArray().last().isLetter())
                Pair(
                    clockPartColors.daytimeMarker.anteOrPost,
                    clockPartColors.daytimeMarker.meridiem
                )
            else Pair(clockPartColors.seconds.tens, clockPartColors.seconds.ones)

        6 -> Pair(clockPartColors.daytimeMarker.anteOrPost, clockPartColors.daytimeMarker.meridiem)
        else -> null
    }
}


/**
 * Works similar as portrait variant of evaluateClockCharColor() ...
 */
private fun evaluateDividerColor(
    formattedTime: String,
    index: Int,
    clockPartColors: ClockPartColors
): Color? {
    /**
     * 'No seconds' special cases
     *
     * formattedTime.length == 6 && with daytime marker
     * => FONT, without seconds, e.g. formattedTime: '1234AM'
     *
     * formattedTime.length == 5 && with daytime marker
     * => 7-seg, with daytime marker, e.g. formattedTime: '1234A'
     */
    return when (index) {
        0 -> clockPartColors.dividers.hoursMinutes
        2 ->
            if (formattedTime.length in (5..6) && formattedTime.toCharArray().last().isLetter())
                clockPartColors.dividers.daytimeMarker
            else clockPartColors.dividers.minutesSeconds

        4 -> clockPartColors.dividers.daytimeMarker
        else -> null
    }
}