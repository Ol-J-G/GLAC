package de.oljg.glac.clock.digital.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.components.ClockCharColumn
import de.oljg.glac.clock.digital.ui.components.ColonDivider
import de.oljg.glac.clock.digital.ui.components.LineDivider
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_DIGIT_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.WIDEST_DIGIT
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.WIDEST_LETTER
import de.oljg.glac.clock.digital.ui.utils.ClockParts
import de.oljg.glac.clock.digital.ui.utils.ClockPartsColors
import de.oljg.glac.clock.digital.ui.utils.DividerAttributes
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_HOURS_MINUTES_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.MeasureFontSize
import de.oljg.glac.clock.digital.ui.utils.PreviewState
import de.oljg.glac.clock.digital.ui.utils.calculateMaxCharSizeFont
import de.oljg.glac.clock.digital.ui.utils.calculateMaxCharSizeSevenSegment
import de.oljg.glac.clock.digital.ui.utils.defaultClockCharColors
import de.oljg.glac.clock.digital.ui.utils.dividerCount
import de.oljg.glac.clock.digital.ui.utils.evaluateFontSizeShrinkFactor
import de.oljg.glac.clock.digital.ui.utils.evaluateStartFontSize
import de.oljg.glac.clock.digital.ui.utils.isDaytimeMarkerChar
import de.oljg.glac.core.clock.data.ClockTheme
import de.oljg.glac.core.util.ClockPartsTestTags
import de.oljg.glac.core.util.TestTags
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.PREVIEW_SIZE_FACTOR

@Composable
fun DigitalAlarmClockLandscapeLayout(
    viewModel: ClockSettingsViewModel = hiltViewModel(),
    previewMode: Boolean = false,
    currentTimeFormatted: String,
    clockBoxSize: IntSize,
    hoursMinutesDividerChar: Char = DEFAULT_HOURS_MINUTES_DIVIDER_CHAR,
    minutesSecondsDividerChar: Char = DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR,
    daytimeMarkerDividerChar: Char = DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR,
    fontFamily: FontFamily = FontFamily.SansSerif,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    charColors: Map<Char, Color> = defaultClockCharColors(defaultColor()),
    clockPartsColors: ClockPartsColors = ClockPartsColors(),
    dividerAttributes: DividerAttributes,
    startFontSize: TextUnit = evaluateStartFontSize(
        Configuration.ORIENTATION_LANDSCAPE,
        previewMode
    ),
    clockCharType: ClockCharType = ClockCharType.FONT,
    digitSizeFactor: Float = DEFAULT_DIGIT_SIZE_FACTOR,
    daytimeMarkerSizeFactor: Float = DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,
    clockChar: @Composable (Char, TextUnit, Color, DpSize) -> Unit
) {

    val isFontCharDivider =
        clockCharType == ClockCharType.FONT && dividerAttributes.dividerStyle == DividerStyle.CHAR
    val isSevenSegmentCharDivider =
        clockCharType == ClockCharType.SEVEN_SEGMENT && dividerAttributes.dividerStyle == DividerStyle.CHAR

    /**
     * Set LINE as default divider style for 7-segment clock in case someone would try to do the
     * impossible :> (7-segment can/should actually just be 0-9 (ok, or some letters as well
     * (P, A, I, l(same as 1), O(same as 0,D), S(same as 5), E, B(same as 8), d))
     * (What a nice coincidence that P and A are easily possible
     * => will be used to show PM/AM as P or A (Who needs the M here to distinguish between
     * 'post meridium and 'ante meridium'? :>
     * Just 'post/past/after noon' or 'ante/before noon' should be actually quite easy
     * to understand as well!)
     */
    val finalDividerStyle =
        if (isSevenSegmentCharDivider) DividerStyle.LINE else dividerAttributes.dividerStyle

    // How many dividers are included in time string
    val dividerCount = currentTimeFormatted.dividerCount(
        hoursMinutesDividerChar,
        minutesSecondsDividerChar,
        daytimeMarkerDividerChar
    )

    var maxFontSize by remember { mutableStateOf(startFontSize) }
    var finalFontBoundsSize by remember {
        mutableStateOf(IntSize(0, 0))
    }

    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockSettings.clockThemeName,
        defaultValue = ClockTheme()
    )

    var previewState by remember {
        mutableStateOf(PreviewState())
    }

    if (clockCharType == ClockCharType.FONT &&
        // Re-measure when one of the following changes (needed for settings preview)
        (previewState.currentTimeStringLength != currentTimeFormatted.length ||
                previewState.currentFont != clockTheme.fontName ||
                previewState.currentFontWeight != clockTheme.fontWeight.name ||
                previewState.currentFontStyle != clockTheme.fontStyle.name ||
                previewState.currentDividerStyle != clockTheme.dividerStyle.name ||
                previewState.currentDividerThickness != clockTheme.dividerThickness
        )
    ) {
        /**
         * Calculate biggest font size that fits into clockBox container in landscape layout.
         */
        MeasureFontSize(
            textToMeasure = evaluateTextToMeasure(
                dividerCount = dividerCount,
                widestChar =
                if (currentTimeFormatted.last().isLetter()) WIDEST_LETTER else WIDEST_DIGIT,
                isCharDivider = finalDividerStyle == DividerStyle.CHAR
            ),
            fontSize = startFontSize,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            dividerStrokeWithToTakeIntoAccount =
            if (finalDividerStyle != DividerStyle.CHAR) dividerAttributes.dividerThickness else 0.dp,
            onFontSizeMeasured = { measuredFontSize, measuredSize ->
                maxFontSize = measuredFontSize
                finalFontBoundsSize = measuredSize
                previewState = previewState.copy(
                    currentTimeStringLength = currentTimeFormatted.length,
                    currentFont = clockTheme.fontName,
                    currentFontWeight = clockTheme.fontWeight.name,
                    currentFontStyle = clockTheme.fontStyle.name,
                    currentDividerStyle = clockTheme.dividerStyle.name,
                    currentDividerThickness = clockTheme.dividerThickness
                )
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
                    minutesSecondsDividerChar,
                    hoursMinutesDividerChar,
                    daytimeMarkerDividerChar
                )
            }

    /**
     * Shrink font chars a bit since font measurement with multiple shrinks (*= .97f) is "ok"-fast
     * on the one hand, but on the other hand, it's not very precise.
     * To find a compromise between these two "poles", I decided to shrink font size depending
     * on how many rows (divider count) are used and font weight (e.g. extra bold chars are a
     * bit bigger than regular ones, etc.) to compensate the precision and not to be too slow.
     */
    val fontSizeShrinkFactor = evaluateFontSizeShrinkFactor(dividerCount, fontWeight)

    val (maxCharWidth, maxCharHeight) =
        when (clockCharType) {
            ClockCharType.FONT -> calculateMaxCharSizeFont(
                finalFontBoundsSize = finalFontBoundsSize,
                currentTimeFormatted = finalCurrentTimeFormatted
            )

            else -> calculateMaxCharSizeSevenSegment(
                dividerStyle = dividerAttributes.dividerStyle,
                dividerThickness = dividerAttributes.dividerThickness,
                dividerCount = dividerCount,
                clockBoxSize = clockBoxSize,
                currentTimeFormatted = finalCurrentTimeFormatted
            )
        }

    val charWidth = maxCharWidth * digitSizeFactor
    val charHeight = maxCharHeight * digitSizeFactor
    val charFontSize = maxFontSize * fontSizeShrinkFactor * digitSizeFactor

    val daytimeMarkerCharWidth = maxCharWidth * daytimeMarkerSizeFactor
    val daytimeMarkerCharHeight = maxCharHeight * daytimeMarkerSizeFactor
    val daytimeMarkerFontSize = maxFontSize * fontSizeShrinkFactor * daytimeMarkerSizeFactor

    val digitalClock = stringResource(id = R.string.digital_clock)

    Row(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = digitalClock }
            .testTag(TestTags.DIGITAL_CLOCK_LANDSCAPE_LAYOUT),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        finalCurrentTimeFormatted.forEachIndexed { index, char ->

            val finalFontSize =
                if (char.isDaytimeMarkerChar()) daytimeMarkerFontSize else charFontSize
            val finalCharWidth =
                if (char.isDaytimeMarkerChar()) daytimeMarkerCharWidth else charWidth
            val finalCharHeight =
                if (char.isDaytimeMarkerChar()) daytimeMarkerCharHeight else charHeight

            val testTag = evaluateClockPartUsingIndex(
                formattedTime = finalCurrentTimeFormatted,
                index = index,
                clockParts = ClockPartsTestTags(),
                isFontCharDivider = isFontCharDivider,
                default = ""
            )

            val finalCharColor = evaluateClockPartUsingIndex(
                    formattedTime = finalCurrentTimeFormatted,
                    index = index,
                    clockParts = clockPartsColors,
                    isFontCharDivider = isFontCharDivider,

                    // dividerColor in case of DividerStyle.CHAR => divider is a Char
                    default = if (char.isLetterOrDigit())
                        charColors.getValue(char) else dividerAttributes.dividerColor
                )

            if (dividerAttributes.dividerStyle != DividerStyle.NONE) {
                val finalDividerColor = evaluateDividerColorUsingIndex(
                        formattedTime = finalCurrentTimeFormatted,
                        index = index,
                        clockParts = clockPartsColors,
                        isFontCharDivider = isFontCharDivider,
                        defaultColor = dividerAttributes.dividerColor
                    )

                /**
                 * When not DividerStyle.CHAR is used, draw one of the remaining divider
                 * styles (LINE, DASHED, DOTTED, DASHDOTTED, COLON)
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
                        ) {
                            when (finalDividerStyle) {
                                DividerStyle.COLON ->
                                    ColonDivider(
                                        clockBoxSize = clockBoxSize,
                                        dividerThickness = if (previewMode)
                                            dividerAttributes.dividerThickness * PREVIEW_SIZE_FACTOR
                                        else dividerAttributes.dividerThickness,
                                        dividerColor = finalDividerColor,
                                        dividerRotateAngle = dividerAttributes.dividerRotateAngle,
                                        firstCirclePosition = dividerAttributes.colonFirstCirclePosition,
                                        secondCirclePosition = dividerAttributes.colonSecondCirclePosition,
                                        orientation = Configuration.ORIENTATION_LANDSCAPE
                                    )

                                else ->
                                    LineDivider(
                                        dividerThickness = if (previewMode)
                                            dividerAttributes.dividerThickness * PREVIEW_SIZE_FACTOR
                                        else dividerAttributes.dividerThickness,
                                        clockBoxSize = clockBoxSize,
                                        dividerDashCount = dividerAttributes.dividerDashCount,
                                        dividerColor = finalDividerColor,
                                        dividerStyle = finalDividerStyle,
                                        dividerLineCap = dividerAttributes.dividerLineCap,
                                        dividerLengthPercent = dividerAttributes.dividerLengthPercentage,
                                        dividerDashDottedPartCount = dividerAttributes.dividerDashDottedPartCount,
                                        dividerRotateAngle = dividerAttributes.dividerRotateAngle,
                                        orientation = Configuration.ORIENTATION_LANDSCAPE
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
                 * in case of FONT.
                 * In case of SevenSegmentChar just digits and two letters will be drawn
                 * (DividerStyle.CHAR not possible together with SevenSegmentChar) ...
                 */
                ClockCharColumn(
                    char = char,
                    columnWidth = finalCharWidth,
                    columnHeight = finalCharHeight,
                    charSize = DpSize(charWidth, charHeight),
                    fontSize = finalFontSize,
                    charColor = if (char.isLetterOrDigit()) finalCharColor else finalDividerColor,
                    testTag = if (char.isLetterOrDigit()) testTag else TestTags.CHAR_DIVIDER
                ) { clockChar, clockCharFontSize, clockCharColor, clockCharSize ->
                    clockChar(
                        clockChar,
                        clockCharFontSize,
                        clockCharColor,
                        clockCharSize,
                    )
                }
            } else { // dividerStyle == DividerStyle.NONE
                ClockCharColumn(
                    char = char,
                    columnWidth = finalCharWidth,
                    columnHeight = finalCharHeight,
                    charSize = DpSize(charWidth, charHeight),
                    fontSize = finalFontSize,
                    charColor = finalCharColor,
                    testTag = testTag
                ) { clockChar, clockCharFontSize, clockCharColor, clockCharSize ->
                    clockChar(
                        clockChar,
                        clockCharFontSize,
                        clockCharColor,
                        clockCharSize,
                    )
                }
            }
        }
    }
}


private fun evaluateTextToMeasure(
    dividerCount: Int,
    widestChar: Char,
    isCharDivider: Boolean
): String {
    return if (isCharDivider) {
        buildString {
            when (dividerCount) {
                // e.g. 'hh:mm' => 'MMMMM'
                1 -> (1..5).forEach { _ -> append(widestChar) }

                // e.g. 'hh:mm:ss' OR 'hh:mm_PM' => 'MMMMMMMM'
                2 -> (1..8).forEach { _ -> append(widestChar) }

                // e.g. 'hh:mm:ss_PM' => 'MMMMMMMMMMM'
                3 -> (1..11).forEach { _ -> append(widestChar) }
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
                1 -> (1..4).forEach { _ -> append(widestChar) }

                // e.g. 'hh:mm:ss' OR 'hh:mm_PM' => 'MMMMMM'
                2 -> (1..6).forEach { _ -> append(widestChar) }

                // e.g. '00:00:00_PM' => 'MMMMMMMM'
                3 -> (1..8).forEach { _ -> append(widestChar) }
            }
        }
    }
}

/**
 * Used set test tags and to override default/specified clock char colors for landscape layout
 * clock.
 *
 * Example use case
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
 * @param clockParts Contains [T] for all "parts" of a clock
 * (except dividers => these colors will be evaluated with evaluateDividerColor())
 * @return A [T] for one clock parts part, depending on the [index] of a clockChar in [formattedTime]
 * @see ClockPartsColors, [ClockParts], [ClockPartsTestTags]
 */
private fun <T> evaluateClockPartUsingIndex(
    formattedTime: String,
    index: Int,
    clockParts: ClockParts<T>,
    isFontCharDivider: Boolean,
    default: T
): T {
    val (formattedTimeContainsNoSecondsButDaytimeMarkerAndDividers,
        formattedTimeContainsNoSecondsButDaytimeMarker) =
        evaluateNoSecondsConditions(formattedTime)

    return if (isFontCharDivider)
        when (index) {
            0 -> clockParts.hours?.tens ?: default
            1 -> clockParts.hours?.ones ?: default
            // 2 -> char divider
            3 -> clockParts.minutes?.tens ?: default
            4 -> clockParts.minutes?.ones ?: default
            // 5 -> char divider
            6 ->
                if (formattedTimeContainsNoSecondsButDaytimeMarkerAndDividers)
                    clockParts.daytimeMarker?.anteOrPost ?: default
                else clockParts.seconds?.tens ?: default

            7 ->
                if (formattedTimeContainsNoSecondsButDaytimeMarkerAndDividers)
                    clockParts.daytimeMarker?.meridiem ?: default
                else clockParts.seconds?.ones ?: default

            // 8 -> char divider
            9 -> clockParts.daytimeMarker?.anteOrPost ?: default
            else -> clockParts.daytimeMarker?.meridiem ?: default // last possible incoming index: 10
        }
    else
        when (index) {
            0 -> clockParts.hours?.tens ?: default
            1 -> clockParts.hours?.ones ?: default
            2 -> clockParts.minutes?.tens ?: default
            3 -> clockParts.minutes?.ones ?: default
            4 ->
                if (formattedTimeContainsNoSecondsButDaytimeMarker)
                    clockParts.daytimeMarker?.anteOrPost ?: default
                else clockParts.seconds?.tens ?: default

            5 ->
                if (formattedTimeContainsNoSecondsButDaytimeMarker)
                    clockParts.daytimeMarker?.meridiem ?: default
                else clockParts.seconds?.ones ?: default

            6 -> clockParts.daytimeMarker?.anteOrPost ?: default
            else -> clockParts.daytimeMarker?.meridiem ?: default // last possible incoming index: 7
        }
}


private fun evaluateDividerColorUsingIndex(
    formattedTime: String,
    index: Int,
    clockParts: ClockPartsColors,
    isFontCharDivider: Boolean,
    defaultColor: Color
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

    return if (isFontCharDivider)
        when (index) {
            2 -> clockParts.dividers.hoursMinutes ?: defaultColor
            5 ->
                if (formattedTimeContainsNoSecondsButDaytimeMarkerAndDividers)
                    clockParts.dividers.daytimeMarker ?: defaultColor
                else clockParts.dividers.minutesSeconds ?: defaultColor

            else -> clockParts.dividers.daytimeMarker ?: defaultColor // last possible incoming index: 8
        }
    else
        when (index) {
            2 -> clockParts.dividers.hoursMinutes ?: defaultColor
            4 ->
                if (formattedTimeContainsNoSecondsButDaytimeMarker)
                    clockParts.dividers.daytimeMarker ?: defaultColor
                else clockParts.dividers.minutesSeconds ?: defaultColor

            else -> clockParts.dividers.daytimeMarker ?: defaultColor // last possible incoming index: 6
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
