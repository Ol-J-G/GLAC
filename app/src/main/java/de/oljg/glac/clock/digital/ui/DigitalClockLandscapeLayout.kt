package de.oljg.glac.clock.digital.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import de.oljg.glac.clock.digital.ui.components.ColonDivider
import de.oljg.glac.clock.digital.ui.components.LineDivider
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_FIRST_CIRCLE_POSITION_AT_ONE_DIVIDER
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_FIRST_CIRCLE_POSITION_AT_THREE_DIVIDERS
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_FIRST_CIRCLE_POSITION_AT_TWO_DIVIDERS
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_SECOND_CIRCLE_POSITION_AT_ONE_DIVIDER
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_SECOND_CIRCLE_POSITION_AT_THREE_DIVIDERS
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_SECOND_CIRCLE_POSITION_AT_TWO_DIVIDERS
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.WIDEST_CHAR
import de.oljg.glac.clock.digital.ui.utils.ClockPartColors
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.MeasureFontSize
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.calculateMaxCharSizeFont
import de.oljg.glac.clock.digital.ui.utils.calculateMaxCharSizeSevenSegment
import de.oljg.glac.clock.digital.ui.utils.isDaytimeMarkerChar

@Composable
fun DigitalClockLandscapeLayout(
    hourMinuteDividerChar: Char,
    minuteSecondDividerChar: Char,
    daytimeMarkerDividerChar: Char,
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    fontStyle: FontStyle,
    charColors: Map<Char, Color>,
    clockPartColors: ClockPartColors? = null,
    currentTimeFormatted: String,
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
    sevenSegmentStyle: SevenSegmentStyle,
    clockCharSizeFactor: Float,
    daytimeMarkerSizeFactor: Float
) {

    /**
     * Set COLON as default divider style for 7-segment clock in case someone would try to do the
     * impossible :> (7-segment can/should actually just be 0-9 (ok, or some letters as well
     * (P, A, I, l(same as 1), O(same as 0,D), S(same as 5), E, B(same as 8), d))
     * (What a nice coincidence that P and A are easily possible
     * => will be used to show PM/AM as P or A (Who needs the M here to distinguish between
     * 'post meridium and 'ante meridium'? :>
     * Just 'post/past/after noon' or 'ante/before noon' should be actually quite easy
     * to understand as well!)
     */
    val finalDividerStyle =
        if (dividerStyle == DividerStyle.CHAR && clockCharType == ClockCharType.SEVEN_SEGMENT)
            DividerStyle.COLON
        else dividerStyle

    var maxFontSize by remember { mutableStateOf(startFontSize) }
    var finalFontBoundsSize by remember {
        mutableStateOf(IntSize(0, 0))
    }


    if (clockCharType == ClockCharType.FONT) {
        /**
         * Calculate biggest font size that fits into clockBox container in landscape layout.
         */
        MeasureFontSize(
            textToMeasure = evaluateTextToMeasure(
                dividerCount = dividerCount,
                isCharDivider = finalDividerStyle == DividerStyle.CHAR
            ),
            fontSize = startFontSize,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            dividerStrokeWithToTakeIntoAccount =
                if (finalDividerStyle != DividerStyle.CHAR) dividerThickness else 0.dp,
            dividerPaddingToTakeIntoAccount =
                if (finalDividerStyle != DividerStyle.CHAR) dividerPadding else 0.dp,
            onFontSizeMeasured = { measuredFontSize, measuredSize ->
                maxFontSize = measuredFontSize
                finalFontBoundsSize = measuredSize
            },
            clockBoxSize = clockBoxSize,
            dividerCount = dividerCount,
            isOrientationPortrait = false
        )
    }

    /**
     * Reformat currentTimeFormatted depending on char divider is used to get correct
     * monospace digit width in any case.
     */
    val finalCurrentTimeFormatted =
        if (clockCharType == ClockCharType.FONT && finalDividerStyle == DividerStyle.CHAR)
            currentTimeFormatted
        else
            currentTimeFormatted.filterNot { char ->
                char in listOf(
                    minuteSecondDividerChar,
                    hourMinuteDividerChar,
                    daytimeMarkerDividerChar
                )
            }


    val (maxCharWidth, maxCharHeight) =
        when (clockCharType) {
            ClockCharType.FONT -> calculateMaxCharSizeFont(
                finalFontBoundsSize = finalFontBoundsSize,
                currentTimeFormatted = finalCurrentTimeFormatted
            )

            else -> calculateMaxCharSizeSevenSegment(
                dividerStyle = dividerStyle,
                dividerPadding = dividerPadding,
                dividerThickness = dividerThickness,
                dividerCount = dividerCount,
                clockBoxSize = clockBoxSize,
                currentTimeFormatted = finalCurrentTimeFormatted
            )
        }

    val finalCharWidth = maxCharWidth * clockCharSizeFactor
    val finalCharHeight = maxCharHeight * clockCharSizeFactor
    val finalFontSize = maxFontSize * clockCharSizeFactor

    val daytimeMarkerCharWidth = maxCharWidth * daytimeMarkerSizeFactor
    val daytimeMarkerCharHeight = maxCharHeight * daytimeMarkerSizeFactor
    val daytimeMarkerFontSize = maxFontSize * daytimeMarkerSizeFactor

    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        finalCurrentTimeFormatted.forEachIndexed { index, char ->

            val finalCharColor =
                if (clockPartColors != null) evaluateClockCharColor(
                    formattedTime = finalCurrentTimeFormatted,
                    index = index,
                    clockPartColors = clockPartColors,
                    dividerStyle = dividerStyle,
                    clockCharType = clockCharType
                )
                else charColors.getValue(char)

            if (dividerStyle != DividerStyle.NONE) {
                val finalDividerColor =
                    if (clockPartColors != null) evaluateDividerColor(
                        formattedTime = finalCurrentTimeFormatted,
                        index = index,
                        clockPartColors = clockPartColors,
                        dividerStyle = dividerStyle,
                        clockCharType = clockCharType
                    )
                    else dividerColor

                /**
                 * When not DividerStyle.CHAR is used, finalCurrentTimeFormatted will NOT
                 * contain divider chars, and then, one of the remaining divider styles will come
                 * in place (LINE, DASHED, DOTTED and COLON)
                 */
                if (finalDividerStyle != DividerStyle.CHAR) {

                    /**
                     * Just the correct position to draw a divider between:
                     * - hours, minutes
                     * - minutes, seconds
                     * - seconds, am/pm label
                     * Example indices for 'hh|mm|ss|PM': 01|23|45|67
                     * => Dividers at 2, 4, 6 (drawn before minute.tens, seconds.tens and
                     *                         daytimeMarker)
                     */
                    if (index > 0 && index % 2 == 0) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(
                                    top = dividerPadding,
                                    bottom = dividerPadding
                                )
                        ) {
                            if (finalDividerStyle != DividerStyle.COLON) {
                                LineDivider(
                                    dividerPadding = dividerPadding,
                                    dividerThickness = dividerThickness,
                                    clockBoxSize = clockBoxSize,
                                    dividerDashCount = dividerDashCount,
                                    dividerColor = finalDividerColor,
                                    dividerStyle = finalDividerStyle,
                                    dividerLineCap = dividerLineCap,
                                    orientation = Configuration.ORIENTATION_LANDSCAPE,
                                    dividerLengthPercent = dividerLengthPercent,
                                    dividerDashDottedPartCount = dividerDashDottedPartCount,
                                    clockCharType = clockCharType,
                                    sevenSegmentStyle = sevenSegmentStyle
                                )
                            } else {
                                ColonDivider(
                                    dividerPadding = dividerPadding,
                                    clockBoxSize = clockBoxSize,
                                    dividerThickness = dividerThickness,
                                    dividerColor = finalDividerColor,
                                    orientation = Configuration.ORIENTATION_LANDSCAPE,
                                    clockCharType = clockCharType,
                                    sevenSegmentStyle = sevenSegmentStyle,

                                    //TODO: maybe create params => configurable, to let user adjust it
                                    firstCirclePositionPercent = when (dividerCount) {
                                        1 -> DEFAULT_FIRST_CIRCLE_POSITION_AT_ONE_DIVIDER
                                        2 -> DEFAULT_FIRST_CIRCLE_POSITION_AT_TWO_DIVIDERS
                                        else -> DEFAULT_FIRST_CIRCLE_POSITION_AT_THREE_DIVIDERS
                                    },
                                    secondCirclePositionPercent = when (dividerCount) {
                                        1 -> DEFAULT_SECOND_CIRCLE_POSITION_AT_ONE_DIVIDER
                                        2 -> DEFAULT_SECOND_CIRCLE_POSITION_AT_TWO_DIVIDERS
                                        else -> DEFAULT_SECOND_CIRCLE_POSITION_AT_THREE_DIVIDERS
                                    }
                                )
                            }
                        }
                    }
                }

                /**
                 * clockChar can be a Text composable or SevenSegmentChar, depending on config
                 *
                 * Note:
                 * When DividerStyle.CHAR is used, finalCurrentTimeFormatted will contain
                 * divider chars, which will be drawn as every other String via Text composable
                 * in case of FONT
                 * In case of SevenSegmentChar just digits and two letters will be drawn
                 * (DividerStyle.CHAR not possible together with SevenSegmentChar) ...
                 */
                Column( //TODO: extract composable, maybe "CharColumn", with clockChar as trailing lambda!??
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .requiredSize( // important to enforce monospace!
                            if (char.isDaytimeMarkerChar()) daytimeMarkerCharWidth else finalCharWidth,
                            if (char.isDaytimeMarkerChar()) daytimeMarkerCharHeight else finalCharHeight
                        )
                ) {
                    clockChar(
                        char,
                        if (char.isDaytimeMarkerChar()) daytimeMarkerFontSize else finalFontSize,
                        if (char.isDigit() || char.isLetter()) finalCharColor else finalDividerColor,
                        DpSize(finalCharWidth, finalCharHeight),
                        Configuration.ORIENTATION_LANDSCAPE
                    )
                }

            } else { // dividerStyle == DividerStyle.NONE
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .requiredSize( // important to enforce monospace!
                            if (char.isDaytimeMarkerChar()) daytimeMarkerCharWidth else finalCharWidth,
                            if (char.isDaytimeMarkerChar()) daytimeMarkerCharHeight else finalCharHeight
                        )
                ) {
                    clockChar(
                        char,
                        if (char.isDaytimeMarkerChar()) daytimeMarkerFontSize else finalFontSize,
                        finalCharColor,
                        DpSize(finalCharWidth, finalCharHeight),
                        Configuration.ORIENTATION_LANDSCAPE
                    )
                }
            }
        }
    }
}


private fun evaluateTextToMeasure(dividerCount: Int, isCharDivider: Boolean): String {
    return if (isCharDivider) {
        buildString {
            when (dividerCount) {
                // e.g. 'hh:mm' => 'MMMMM'
                1 -> (1..5).forEach { _ -> append(WIDEST_CHAR) }

                // e.g. 'hh:mm:ss' OR 'hh:mm_PM' => 'MMMMMMMM'
                2 -> (1..8).forEach { _ -> append(WIDEST_CHAR) }

                // e.g. 'hh:mm:ss_PM' => 'MMMMMMMMMMM'
                3 -> (1..11).forEach { _ -> append(WIDEST_CHAR) }
            }
        }
    } else {

        /**
         * In case of no DividerStyle.CHAR divider is used, it's not necessary to keep the widest
         * char into account for dividers to measure.
         *
         * Note:
         * The space needed for non-DividerStyle.CHAR dividers will be respected, but not here,
         * since they are no chars (see [MeasureFontSize]).
         */
        buildString {
            when (dividerCount) {
                // e.g. 'hh|mm' => 'MMMM'
                1 -> (1..4).forEach { _ -> append(WIDEST_CHAR) }

                // e.g. 'hh:mm:ss' OR 'hh:mm_PM' => 'MMMMMM'
                2 -> (1..6).forEach { _ -> append(WIDEST_CHAR) }

                // e.g. '00:00:00_PM' => 'MMMMMMMM'
                3 -> (1..8).forEach { _ -> append(WIDEST_CHAR) }
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
 * Example: [formattedTime] = "123456AM", indices: 01 23 45 67
 * (0=hours.tens.color, 1=hours.ones.color, etc.)
 *
 * For easier understanding, as follows some examples...
 *
 * Some common cases:
 * - showDivider && ClockCharType.FONT, with seconds and daytimeMarker
 *  => [formattedTime]: '12:34:56:AM'
 * (divider chars are included, among other things to find max font size ...)
 *  => [formattedTime].length == 11 && last char of [formattedTime] is letter
 *  => indices: 0 1 [2] 3 4 [5] 6 7 [8] 9 10 (2,5,8 => divider chars)
 *  => char at index 9, 10 will be set to anteOrPost, meridiem color
 *
 * - showDivider && ClockCharType.SEVEN_SEGMENT (else branch), with seconds
 *  => [formattedTime]: '123456A'
 * (divider chars not included => no CHAR divider intended/allowed for 7-segment, just all others)
 *  => [formattedTime].length == 7 && last char of [formattedTime] is letter
 *  => indices: 01 23 45 6
 *  => char at index 6 will be set to anteOrPost color (no meridiem in 7-seg)
 *
 * Some special cases:
 * - showDivider && ClockCharType.FONT, without seconds and daytimemarker
 *  => [formattedTime]: '12:34:AM'
 *  => [formattedTime].length == 8 && last char of [formattedTime] is letter
 *  => indices: 0 1 [2] 3 4 [5] 6 7 (2,5,8 => divider chars)
 *  => char at index 6, 7 will be set to anteOrPost, meridiem color
 *
 * - showDivider && ClockCharType.SEVEN_SEGMENT (else branch), without seconds
 * => [formattedTime]: '1234A'
 * => [formattedTime].length == 5 && last char of [formattedTime] is letter
 * => indices: 01 23 4
 * => char at index 4 will be set to anteOrPost color (no meridiem in 7-seg)
 *
 * - !showDivider && ClockCharType.FONT (else branch), without seconds and daytimemarker
 *  => [formattedTime]: '1234AM'
 *  => [formattedTime].length == 6 && last char of [formattedTime] is letter
 *  => indices: 01 23 45
 *  => char at index 4, 5 will be set to anteOrPost, meridiem color
 *
 * @param formattedTime Current time formatted
 * @param index Position of a clockChar within [formattedTime]
 * @param clockPartColors Contains colors for all "parts" of a clock
 * (except dividers => these colors will be evaluated with evaluateDividerColor())
 * @return A color for one clock parts part, depending on the [index] of a clockChar in [formattedTime]
 * @see ClockPartColors
 */
private fun evaluateClockCharColor(
    formattedTime: String,
    index: Int,
    clockPartColors: ClockPartColors,
    dividerStyle: DividerStyle,
    clockCharType: ClockCharType
): Color {
    val (formattedTimeContainsNoSecondsButDaytimeMarkerAndDividers,
        formattedTimeContainsNoSecondsButDaytimeMarker) =
        evaluateNoSecondsConditions(formattedTime)

    return if (dividerStyle != DividerStyle.NONE &&
        clockCharType == ClockCharType.FONT &&
        dividerStyle == DividerStyle.CHAR)
        when (index) {
            0 -> clockPartColors.hours.tens
            1 -> clockPartColors.hours.ones
            // 2 -> divider
            3 -> clockPartColors.minutes.tens
            4 -> clockPartColors.minutes.ones
            // 5 -> divider
            6 ->
                if (formattedTimeContainsNoSecondsButDaytimeMarkerAndDividers)
                    clockPartColors.daytimeMarker.anteOrPost
                else clockPartColors.seconds.tens

            7 ->
                if (formattedTimeContainsNoSecondsButDaytimeMarkerAndDividers)
                    clockPartColors.daytimeMarker.meridiem
                else clockPartColors.seconds.ones

            // 8 -> divider
            9 -> clockPartColors.daytimeMarker.anteOrPost
            else -> clockPartColors.daytimeMarker.meridiem // last possible incoming index: 10
        }
    else
        when (index) {
            0 -> clockPartColors.hours.tens
            1 -> clockPartColors.hours.ones
            2 -> clockPartColors.minutes.tens
            3 -> clockPartColors.minutes.ones
            4 ->
                if (formattedTimeContainsNoSecondsButDaytimeMarker)
                    clockPartColors.daytimeMarker.anteOrPost
                else clockPartColors.seconds.tens

            5 ->
                if (formattedTimeContainsNoSecondsButDaytimeMarker)
                    clockPartColors.daytimeMarker.meridiem
                else clockPartColors.seconds.ones

            6 -> clockPartColors.daytimeMarker.anteOrPost
            else -> clockPartColors.daytimeMarker.meridiem // last possible incoming index: 7
        }
}

/**
 * Works similar as landscape variant of evaluateClockCharColor() ...
 */
private fun evaluateDividerColor(
    formattedTime: String,
    index: Int,
    clockPartColors: ClockPartColors,
    dividerStyle: DividerStyle,
    clockCharType: ClockCharType
): Color {
    /**
     * 'No seconds' special cases
     *
     * formattedTime.length == 8 && with daytime marker && DividerStyle.CHAR
     * => FONT, without seconds, e.g. formattedTime: '12:34:AM'
     * => index of daytime marker divider = 5
     *
     * formattedTime.length == 5 && with daytime marker
     * => 7-seg, without seconds, e.g. formattedTime: '1234A'
     * => index of daytime marker divider = 4 (else branch)
     *
     * formattedTime.length == 6 && with daytime marker && DividerStyle.LINE (and every other non-CHAR one)
     *  => FONT, without seconds, e.g. formattedTime: '1234AM'
     *  => index of daytime marker divider = 4 (else branch)
     */
    val (formattedTimeContainsNoSecondsButDaytimeMarkerAndDividers,
        formattedTimeContainsNoSecondsButDaytimeMarker) =
        evaluateNoSecondsConditions(formattedTime)

    return if (clockCharType == ClockCharType.FONT && dividerStyle == DividerStyle.CHAR)
        when (index) {
            2 -> clockPartColors.dividers.hoursMinutes
            5 ->
                if (formattedTimeContainsNoSecondsButDaytimeMarkerAndDividers)
                    clockPartColors.dividers.daytimeMarker
                else clockPartColors.dividers.minutesSeconds

            else -> clockPartColors.dividers.daytimeMarker // // last possible incoming index: 8
        }
    else
        when (index) {
            2 -> clockPartColors.dividers.hoursMinutes
            4 ->
                if (formattedTimeContainsNoSecondsButDaytimeMarker)
                    clockPartColors.dividers.daytimeMarker
                else clockPartColors.dividers.minutesSeconds

            else -> clockPartColors.dividers.daytimeMarker // // last possible incoming index: 6
        }
}


private fun evaluateNoSecondsConditions(formattedTime: String): Pair<Boolean, Boolean> {
    val formattedTimeLastCharIsLetter = formattedTime.toCharArray().last().isLetter()

    /**
     * E.g.: [formattedTime]: '12:34:AM' => length == 8 (ClockCharType.FONT && DividerStyle.CHAR)
     */
    val containsNoSecondsButDaytimeMarkerAndDividers =
        formattedTime.length == 8 && formattedTimeLastCharIsLetter

    /**
     * E.g.:
     * - [formattedTime]: '1234AM' => length == 6 (ClockCharType.FONT)
     * - [formattedTime]: '1234A' => length == 5 (ClockCharType.SEVEN_SEGMENT)
     */
    val containsNoSecondsButDaytimeMarker =
        formattedTime.length in (5..6) && formattedTimeLastCharIsLetter
    return Pair(containsNoSecondsButDaytimeMarkerAndDividers, containsNoSecondsButDaytimeMarker)
}
