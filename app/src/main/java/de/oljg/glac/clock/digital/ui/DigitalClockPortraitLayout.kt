package de.oljg.glac.clock.digital.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
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
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.WIDEST_CHAR
import de.oljg.glac.clock.digital.ui.utils.ClockParts
import de.oljg.glac.clock.digital.ui.utils.ClockPartsColors
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_FIRST_CIRCLE_POSITION_PORTRAIT_SPECIAL
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_SECOND_CIRCLE_POSITION_PORTRAIT_SPECIAL
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.MeasureFontSize
import de.oljg.glac.clock.digital.ui.utils.defaultClockCharColors
import de.oljg.glac.clock.digital.ui.utils.evaluateStartFontSize
import de.oljg.glac.clock.digital.ui.utils.evalutateDividerCount
import de.oljg.glac.clock.digital.ui.utils.isDaytimeMarkerChar
import de.oljg.glac.clock.digital.ui.utils.pxToDp
import de.oljg.glac.core.util.ClockPartsTestTags
import de.oljg.glac.core.util.TestTags


@Composable
fun DigitalClockPortraitLayout(
    currentTimeWithoutSeparators: String,
    clockBoxSize: IntSize,
    fontFamily: FontFamily = FontFamily.SansSerif,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    charColors: Map<Char, Color> = defaultClockCharColors(MaterialTheme.colorScheme.onSurface),
    clockPartsColors: ClockPartsColors? = null,
    dividerStyle: DividerStyle = DividerStyle.LINE,
    dividerDashCount: Int = DividerDefaults.DEFAULT_DASH_COUNT,
    dividerLineCap: StrokeCap = StrokeCap.Butt,
    dividerThickness: Dp = DividerDefaults.DEFAULT_DIVIDER_THICKNESS_LINE,
    dividerPadding: Dp = DividerDefaults.DEFAULT_DIVIDER_PADDING_LINE,
    dividerColor: Color = MaterialTheme.colorScheme.onSurface,
    dividerLengthPercent: Float = DividerDefaults.DEFAULT_DIVIDER_LENGTH_FACTOR,
    dividerDashDottedPartCount: Int = DividerDefaults.DEFAULT_DASH_DOTTED_PART_COUNT,
    startFontSize: TextUnit = evaluateStartFontSize(Configuration.ORIENTATION_PORTRAIT),
    clockCharType: ClockCharType = ClockCharType.FONT,
    clockCharSizeFactor: Float = ClockDefaults.DEFAULT_CLOCK_CHAR_SIZE_FACTOR,
    daytimeMarkerSizeFactor: Float = ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,
    clockChar: @Composable (Char, TextUnit, Color, DpSize, Int) -> Unit
) {

    // How many dividers are included in time string
    val dividerCount = evalutateDividerCount(currentTimeWithoutSeparators)

    var maxFontSize by remember { mutableStateOf(startFontSize) }
    var finalFontBoundsSize by remember {
        mutableStateOf(IntSize(0, 0))
    }

    if (clockCharType == ClockCharType.FONT) {
        /**
         * Calculate biggest font size that fits into clockBox container in portrait layout.
         */
        MeasureFontSize(
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
                    context.getString(de.oljg.glac.R.string.digital_clock)
            }
            .testTag(TestTags.DIGITAL_CLOCK_PORTRAIT_LAYOUT),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly //TODO: let user decide (same in landscape) SpaceEvenly should be default
    ) {
        currentTimeWithoutSeparators.forEachIndexed { index, char ->

            val testTag = evaluateClockPart(
                formattedTime = currentTimeWithoutSeparators,
                index = index,
                clockParts = ClockPartsTestTags()
            )

            // In case [clockPartColors] are specified, use them instead of [charColors].
            val clockPartColorPair: Pair<Color, Color>? =
                if (clockPartsColors != null) evaluateClockPart(
                    formattedTime = currentTimeWithoutSeparators,
                    index = index,
                    clockParts = clockPartsColors
                )
                else null

            val finalDividerColor =
                if (clockPartsColors != null) evaluateDividerColor(
                    formattedTime = currentTimeWithoutSeparators,
                    index = index,
                    clockPartsColors = clockPartsColors,
                )
                else dividerColor

            // Draw 'A' or 'P' ONLY in case of ClockCharType.SEVEN_SEGMENT in last row as single column
            if (index == currentTimeWithoutSeparators.length - 1 && clockCharType == ClockCharType.SEVEN_SEGMENT && char.isLetter()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column( // Single char: ante or post
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .requiredSize(
                                if (currentTimeWithoutSeparators[index].isDaytimeMarkerChar()) daytimeMarkerCharWidth else finalCharWidth,
                                if (currentTimeWithoutSeparators[index].isDaytimeMarkerChar()) daytimeMarkerCharHeight else finalCharHeight
                            )
                            .testTag(testTag.first)
                    ) {
                        clockChar(
                            currentTimeWithoutSeparators[index],
                            if (currentTimeWithoutSeparators[index].isDaytimeMarkerChar()) daytimeMarkerFontSize else finalFontSize,
                            clockPartColorPair?.first ?: charColors.getValue(
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
                    Column( // 1st char (left): tens, ante or post
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .requiredSize(
                                if (currentTimeWithoutSeparators[index].isDaytimeMarkerChar()) daytimeMarkerCharWidth else finalCharWidth,
                                if (currentTimeWithoutSeparators[index].isDaytimeMarkerChar()) daytimeMarkerCharHeight else finalCharHeight
                            )
                            .testTag(testTag.first)
                    ) {
                        clockChar(
                            currentTimeWithoutSeparators[index],
                            if (currentTimeWithoutSeparators[index].isDaytimeMarkerChar()) daytimeMarkerFontSize else finalFontSize,
                            clockPartColorPair?.first ?: charColors.getValue(
                                currentTimeWithoutSeparators[index]
                            ),
                            DpSize(finalCharWidth, finalCharHeight),
                            Configuration.ORIENTATION_PORTRAIT
                        )
                    }
                    Column( // 2nd char (right): ones or meridiem
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .requiredSize(
                                if (currentTimeWithoutSeparators[index + 1].isDaytimeMarkerChar()) daytimeMarkerCharWidth else finalCharWidth,
                                if (currentTimeWithoutSeparators[index + 1].isDaytimeMarkerChar()) daytimeMarkerCharHeight else finalCharHeight
                            )
                            .testTag(testTag.second)
                    ) {
                        clockChar(
                            currentTimeWithoutSeparators[index + 1],
                            if (currentTimeWithoutSeparators[index + 1].isDaytimeMarkerChar()) daytimeMarkerFontSize else finalFontSize,
                            clockPartColorPair?.second ?: charColors.getValue(
                                currentTimeWithoutSeparators[index + 1]
                            ),
                            DpSize(finalCharWidth, finalCharHeight),
                            Configuration.ORIENTATION_PORTRAIT
                        )
                    }
                }

                if (index + 1 < currentTimeWithoutSeparators.length - 1) { // don't draw a divider at bottom of screen
                    if (dividerStyle != DividerStyle.NONE) {
                        when (dividerStyle) {
                            DividerStyle.LINE, DividerStyle.CHAR ->
                                LineDivider(
                                    dividerPadding = dividerPadding,
                                    dividerThickness = dividerThickness,
                                    clockBoxSize = clockBoxSize,
                                    dividerDashCount = dividerDashCount,
                                    dividerColor = finalDividerColor,
                                    dividerStyle = dividerStyle,
                                    dividerLineCap = dividerLineCap,
                                    orientation = Configuration.ORIENTATION_PORTRAIT,
                                    dividerLengthPercent = dividerLengthPercent,
                                    dividerDashDottedPartCount = dividerDashDottedPartCount
                                )

                            else ->
                                /**
                                 * Draw a colon-like divider (also as replacement for
                                 * DividerStyle.CHAR! => otherwise FONT chars would have
                                 * to be rotated => kinda ugly/weird imho)
                                 */
                                ColonDivider(
                                    dividerPadding = dividerPadding,
                                    clockBoxSize = clockBoxSize,
                                    dividerThickness = dividerThickness,
                                    dividerColor = finalDividerColor,
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
 * Used to set test tags and to override default/specified clock char colors for portrait layout
 * clock.
 *
 * Example use case:
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
 * @param formattedTime Formatted time string
 * @param index Position of a clockChar within [formattedTime]
 * @param clockParts Contains [T] for all "parts" of a clock (except dividers
 * => these will be evaluated with evaluateDividerColor())
 * @return A pair of [T] for one clock part, depending on the [index] of a clockChar
 * in [formattedTime] (a Pair because of the design of the for loop it is used in - Pair.first,
 * Pair.second comes in handy ...)
 * @see ClockPartsColors, [ClockParts], [ClockPartsTestTags]
 */
private fun <T> evaluateClockPart(
    formattedTime: String,
    index: Int,
    clockParts: ClockParts<T>
): Pair<T, T> {
    return when (index) {
        0 -> Pair(clockParts.hours.tens, clockParts.hours.ones)
        2 -> Pair(clockParts.minutes.tens, clockParts.minutes.ones)
        4 ->
            if (formattedTime.length in (5..6) && formattedTime.toCharArray().last().isLetter())
                Pair(
                    clockParts.daytimeMarker.anteOrPost,
                    clockParts.daytimeMarker.meridiem
                )
            else Pair(clockParts.seconds.tens, clockParts.seconds.ones)

        else -> Pair(clockParts.daytimeMarker.anteOrPost, clockParts.daytimeMarker.meridiem) // 6
    }
}


/**
 * Works similar as portrait variant of evaluateClockPart() ...
 */
private fun evaluateDividerColor(
    formattedTime: String,
    index: Int,
    clockPartsColors: ClockPartsColors
): Color {
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
        0 -> clockPartsColors.dividers.hoursMinutes
        2 ->
            if (formattedTime.length in (5..6) && formattedTime.toCharArray().last().isLetter())
                clockPartsColors.dividers.daytimeMarker
            else clockPartsColors.dividers.minutesSeconds

        else -> clockPartsColors.dividers.daytimeMarker // 4
    }
}