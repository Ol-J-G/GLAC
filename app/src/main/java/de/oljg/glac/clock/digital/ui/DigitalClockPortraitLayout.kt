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
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_CLOCK_DIGIT_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.WIDEST_DIGIT
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.WIDEST_LETTER
import de.oljg.glac.clock.digital.ui.utils.ClockParts
import de.oljg.glac.clock.digital.ui.utils.ClockPartsColors
import de.oljg.glac.clock.digital.ui.utils.DividerAttributes
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.MeasureFontSize
import de.oljg.glac.clock.digital.ui.utils.PreviewState
import de.oljg.glac.clock.digital.ui.utils.defaultClockCharColors
import de.oljg.glac.clock.digital.ui.utils.evaluateFontSizeShrinkFactor
import de.oljg.glac.clock.digital.ui.utils.evaluateStartFontSize
import de.oljg.glac.clock.digital.ui.utils.evalutateDividerCount
import de.oljg.glac.clock.digital.ui.utils.isDaytimeMarkerChar
import de.oljg.glac.clock.digital.ui.utils.pxToDp
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.core.settings.data.ClockTheme
import de.oljg.glac.core.util.ClockPartsTestTags
import de.oljg.glac.core.util.TestTags
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.PREVIEW_SIZE_FACTOR


@Composable
fun DigitalClockPortraitLayout(
    viewModel: ClockSettingsViewModel = hiltViewModel(),
    previewMode: Boolean = false,
    currentTimeWithoutSeparators: String,
    clockBoxSize: IntSize,
    fontFamily: FontFamily = FontFamily.SansSerif,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    charColors: Map<Char, Color> = defaultClockCharColors(defaultColor()),
    clockPartsColors: ClockPartsColors = ClockPartsColors(),
    dividerAttributes: DividerAttributes,
    startFontSize: TextUnit = evaluateStartFontSize(
        Configuration.ORIENTATION_PORTRAIT,
        previewMode
    ),
    clockCharType: ClockCharType = ClockCharType.FONT,
    digitSizeFactor: Float = DEFAULT_CLOCK_DIGIT_SIZE_FACTOR,
    daytimeMarkerSizeFactor: Float = DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,
    clockChar: @Composable (Char, TextUnit, Color, DpSize) -> Unit
) {

    // How many dividers are included in time string
    val dividerCount = evalutateDividerCount(currentTimeWithoutSeparators)

    var maxFontSize by remember { mutableStateOf(startFontSize) }
    var finalFontBoundsSize by remember {
        mutableStateOf(IntSize(0, 0))
    }

    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockSettings.clockThemeName,
        defaultValue = ClockTheme()
    )

    var previewState by remember {
        mutableStateOf(PreviewState())
    }

    val widestChar =
        if (currentTimeWithoutSeparators.last().isLetter()) WIDEST_LETTER else WIDEST_DIGIT

    if (clockCharType == ClockCharType.FONT &&
        // Re-measure when one of the following changes (needed for settings preview)
        (previewState.currentTimeStringLength != currentTimeWithoutSeparators.length ||
                previewState.currentFont != clockTheme.fontName ||
                previewState.currentFontWeight != clockTheme.fontWeight.name ||
                previewState.currentFontStyle != clockTheme.fontStyle.name ||
                previewState.currentDividerStyle != clockTheme.dividerStyle.name ||
                previewState.currentDividerThickness != clockTheme.dividerThickness
                )
    ) {
        /**
         * Calculate biggest font size that fits into clockBox container in portrait layout.
         */
        MeasureFontSize(
            textToMeasure = buildString {
                (1..2).forEach { _ -> append(widestChar) } // e.g. 'MM' => 2
            },
            fontFamily = fontFamily,
            fontSize = startFontSize,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            clockBoxSize = clockBoxSize,
            dividerCount = dividerCount,
            isOrientationPortrait = true,
            dividerStrokeWithToTakeIntoAccount =
            if (dividerAttributes.dividerStyle != DividerStyle.NONE)
                dividerAttributes.dividerThickness
            else 0.dp,

            onFontSizeMeasured = { measuredFontSize, measuredSize ->
                maxFontSize = measuredFontSize
                finalFontBoundsSize = measuredSize
                previewState = previewState.copy(
                    currentTimeStringLength = currentTimeWithoutSeparators.length,
                    currentFont = clockTheme.fontName,
                    currentFontWeight = clockTheme.fontWeight.name,
                    currentFontStyle = clockTheme.fontStyle.name,
                    currentDividerStyle = clockTheme.dividerStyle.name,
                    currentDividerThickness = clockTheme.dividerThickness
                )
            }
        )
    }

    val spaceNeededForDividers =
        if (dividerAttributes.dividerStyle != DividerStyle.NONE)
            (dividerAttributes.dividerThickness * dividerCount)
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
    val sevenSegmentShrinkFactor =
        if (dividerCount == 1) .9f else .95f

    /**
     * Shrink font chars a bit since font measurement with multiple shrinks (*= .97f) is "ok"-fast
     * on the one hand, but on the other hand, it's not very precise.
     * To find a compromise between these two "poles", I decided to shrink font size depending
     * on how many rows (divider count) are used and font weight (e.g. extra bold chars are a
     * bit bigger than regular ones, etc.) to compensate the precision and not to be too slow.
     */
    val fontSizeShrinkFactor = evaluateFontSizeShrinkFactor(dividerCount, fontWeight)

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

    val charWidth = maxCharWidth * digitSizeFactor
    val charHeight = maxCharHeight * digitSizeFactor
    val charFontSize = maxFontSize * fontSizeShrinkFactor * digitSizeFactor

    val daytimeMarkerCharWidth = maxCharWidth * daytimeMarkerSizeFactor
    val daytimeMarkerCharHeight = maxCharHeight * daytimeMarkerSizeFactor
    val daytimeMarkerFontSize = maxFontSize * fontSizeShrinkFactor * daytimeMarkerSizeFactor

    val digitalClock = stringResource(id = R.string.digital_clock)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = digitalClock }
            .testTag(TestTags.DIGITAL_CLOCK_PORTRAIT_LAYOUT),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly //TODO_LATER: let user decide (same in landscape) SpaceEvenly should be default
    ) {
        currentTimeWithoutSeparators.forEachIndexed { index, char ->

            val finalFontSize =
                if (char.isDaytimeMarkerChar()) daytimeMarkerFontSize else charFontSize
            val finalCharWidth =
                if (char.isDaytimeMarkerChar()) daytimeMarkerCharWidth else charWidth
            val finalCharHeight =
                if (char.isDaytimeMarkerChar()) daytimeMarkerCharHeight else charHeight

            val testTag = evaluateClockPartUsingIndex(
                formattedTime = currentTimeWithoutSeparators,
                index = index,
                clockParts = ClockPartsTestTags(),
                firstDefault = "", secondDefault = ""
            )

            // In case [clockPartColors] are specified, use them on top of [charColors].
            val clockPartColorPair = evaluateClockPartUsingIndex(
                    formattedTime = currentTimeWithoutSeparators,
                    index = index,
                    clockParts = clockPartsColors,
                    firstDefault = charColors.getValue(
                        currentTimeWithoutSeparators[index]
                    ),
                    secondDefault = charColors.getValue(
                        currentTimeWithoutSeparators[
                            if(index + 1 == currentTimeWithoutSeparators.length) index
                            else index + 1
                        ]
                    )
                )

            val finalDividerColor = evaluateDividerColorUsingIndex(
                    formattedTime = currentTimeWithoutSeparators,
                    index = index,
                    clockPartsColors = clockPartsColors,
                    defaultDividerColor = dividerAttributes.dividerColor
                )

            /**
             * Draw 'A' or 'P' ONLY in case of ClockCharType.SEVEN_SEGMENT in last row as single
             * column (last char in currentTimeWithoutSeparators must be a letter).
             */
            if (index == currentTimeWithoutSeparators.length - 1 &&
                char.isLetter() &&
                clockCharType == ClockCharType.SEVEN_SEGMENT
            ) {

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClockCharColumn( // Single char: ante or post
                        char = currentTimeWithoutSeparators[index],
                        columnWidth = finalCharWidth,
                        columnHeight = finalCharHeight,
                        charSize = DpSize(charWidth, charHeight),
                        fontSize = finalFontSize,
                        charColor = clockPartColorPair.first,
                        testTag = testTag.first
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

            if (index + 1 == currentTimeWithoutSeparators.length) return@forEachIndexed

            if (index % 2 == 0) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClockCharColumn( // 1st char (left): tens, ante or post
                        char = currentTimeWithoutSeparators[index],
                        columnWidth = finalCharWidth,
                        columnHeight = finalCharHeight,
                        charSize = DpSize(charWidth, charHeight),
                        fontSize = finalFontSize,
                        charColor = clockPartColorPair.first,
                        testTag = testTag.first
                    ) { clockChar, clockCharFontSize, clockCharColor, clockCharSize ->
                        clockChar(
                            clockChar,
                            clockCharFontSize,
                            clockCharColor,
                            clockCharSize,
                        )
                    }
                    ClockCharColumn( // 2nd char (right): ones or meridiem
                        char = currentTimeWithoutSeparators[index + 1],
                        columnWidth = finalCharWidth,
                        columnHeight = finalCharHeight,
                        charSize = DpSize(charWidth, charHeight),
                        fontSize = finalFontSize,
                        charColor = clockPartColorPair.second,
                        testTag = testTag.second
                    ) { clockChar, clockCharFontSize, clockCharColor, clockCharSize ->
                        clockChar(
                            clockChar,
                            clockCharFontSize,
                            clockCharColor,
                            clockCharSize,
                        )
                    }
                }

                if (index + 1 < currentTimeWithoutSeparators.length - 1) { // don't draw a divider at bottom of screen
                    if (dividerAttributes.dividerStyle != DividerStyle.NONE) {
                        when (dividerAttributes.dividerStyle) {
                            DividerStyle.COLON ->
                                /**
                                 * Draw a colon-like divider (also as replacement for
                                 * DividerStyle.CHAR! => otherwise FONT chars would have
                                 * to be rotated => kinda ugly/weird imho)
                                 */
                                ColonDivider(
                                    clockBoxSize = clockBoxSize,
                                    dividerThickness = if (previewMode)
                                        dividerAttributes.dividerThickness * PREVIEW_SIZE_FACTOR
                                    else dividerAttributes.dividerThickness,
                                    dividerColor = finalDividerColor ,

                                    // != 0f => not useful (yet) in portrait
                                    dividerRotateAngle = 0f,

                                    /**
                                     * Special kind of 'colon-circle' placement
                                     * => 10% distance from the edges, is quite in harmony
                                     * (imho)
                                     */
                                    firstCirclePosition = dividerAttributes.colonFirstCirclePosition,
                                    secondCirclePosition = dividerAttributes.colonSecondCirclePosition,
                                    orientation = Configuration.ORIENTATION_PORTRAIT
                                )

                            else ->
                                LineDivider(
                                    dividerThickness = if (previewMode)
                                        dividerAttributes.dividerThickness * PREVIEW_SIZE_FACTOR
                                    else dividerAttributes.dividerThickness,
                                    clockBoxSize = clockBoxSize,
                                    dividerDashCount = dividerAttributes.dividerDashCount,
                                    dividerColor = finalDividerColor,
                                    dividerStyle = dividerAttributes.dividerStyle,
                                    dividerLineCap = dividerAttributes.dividerLineCap,
                                    dividerLengthPercent = dividerAttributes.dividerLengthPercentage,
                                    dividerDashDottedPartCount = dividerAttributes.dividerDashDottedPartCount,

                                    // != 0f => not useful (yet) in portrait
                                    dividerRotateAngle = 0f,
                                    orientation = Configuration.ORIENTATION_PORTRAIT
                                )
                        }
                    }
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
private fun <T> evaluateClockPartUsingIndex(
    formattedTime: String,
    index: Int,
    clockParts: ClockParts<T>,
    firstDefault: T,
    secondDefault: T
): Pair<T, T> {
    return when (index) {
        0 -> Pair(
            clockParts.hours?.tens ?: firstDefault,
            clockParts.hours?.ones ?: secondDefault
        )
        2 -> Pair(
            clockParts.minutes?.tens ?: firstDefault,
            clockParts.minutes?.ones ?: secondDefault
        )
        4 ->
            if (formattedTime.length in (5..6) && formattedTime.toCharArray().last().isLetter())
                Pair(
                    clockParts.daytimeMarker?.anteOrPost ?: firstDefault,
                    clockParts.daytimeMarker?.meridiem ?: secondDefault
                )
            else Pair(
                clockParts.seconds?.tens ?: firstDefault,
                clockParts.seconds?.ones ?: secondDefault
            )

        else -> Pair(
            clockParts.daytimeMarker?.anteOrPost ?: firstDefault,
            clockParts.daytimeMarker?.meridiem ?: secondDefault
        ) // 6
    }
}


private fun evaluateDividerColorUsingIndex(
    formattedTime: String,
    index: Int,
    clockPartsColors: ClockPartsColors,
    defaultDividerColor: Color
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
        0 -> clockPartsColors.dividers.hoursMinutes ?: defaultDividerColor
        2 ->
            if (formattedTime.length in (5..6) && formattedTime.toCharArray().last().isLetter())
                clockPartsColors.dividers.daytimeMarker ?: defaultDividerColor
            else clockPartsColors.dividers.minutesSeconds ?: defaultDividerColor

        else -> clockPartsColors.dividers.daytimeMarker ?: defaultDividerColor// 4
    }
}
