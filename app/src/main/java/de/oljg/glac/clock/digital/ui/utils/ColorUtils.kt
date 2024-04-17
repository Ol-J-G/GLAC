package de.oljg.glac.clock.digital.ui.utils

import androidx.compose.ui.graphics.Color
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.CLOCK_CHARS
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.SEVEN_SEGMENT_CHARS

//data class ClockPartColors(
//    val hours: DigitColor,
//    val minutes: DigitColor,
//    val seconds: DigitColor,
//    val daytimeMarker: DaytimeMarkerColor,
//    val dividers: DividerColor
//) {
//    data class DigitColor(
//        val ones: Color,
//        val tens: Color
//    )
//
//    data class DaytimeMarkerColor( // AM/PM
//        val anteOrPost: Color, // 'A' or 'P'
//        val meridiem: Color // 'M'
//    )
//
//    data class DividerColor(
//
//        // Color for divider between hours and minutes
//        val hoursMinutes: Color,
//
//        // Color for divider between minutes and seconds/daytime marker
//        val minutesSeconds: Color,
//
//        // Color for divider between minutes/secondes and daytime marker
//        val daytimeMarker: Color
//    )
//}

data class ClockPartsColors(
    override val hours: DigitPairColor,
    override val minutes: DigitPairColor,
    override val seconds: DigitPairColor,
    override val daytimeMarker: DaytimeMarkerColor,
    val dividers: DividerColor
): ClockParts<Color> {
    data class DigitPairColor(
        override val ones: Color,
        override val tens: Color
    ): DigitPair<Color>

    data class DaytimeMarkerColor( // AM/PM
        override val anteOrPost: Color, // 'A' or 'P'
        override val meridiem: Color // 'M'
    ): DaytimeMarker<Color>

    data class DividerColor(

        // Color for divider between hours and minutes
        val hoursMinutes: Color,

        // Color for divider between minutes and seconds/daytime marker
        val minutesSeconds: Color,

        // Color for divider between minutes/secondes and daytime marker
        val daytimeMarker: Color
    )
}


fun defaultClockCharColors(charColor: Color): Map<Char, Color> {
    return buildMap {
        CLOCK_CHARS.forEach { char ->
            put(char, charColor)
        }
    }.toMap()
}


/**
 * Set clockChar colors (key T=[Char]) or 7-segment segments colors (key T=[Segment]), to enable
 * using one color for all clockChars/segments or individual colors for each clockChar/segment.
 *
 * E.g.: This way, a user can have rather default colors, or in case of 7-segment, all '2'
 * clockChars green and Segment.TOP for all clockChars yellow (where the same config would result
 * in case of [ClockCharType.FONT] in only the green '2's, since there are no segments...
 *
 * @see [CLOCK_CHARS], [SEVEN_SEGMENT_CHARS], [Segment], [defaultClockCharColors],
 * [defaultSegmentColors]
 * @return An immutable color map with default values from [defaultColors] map, where colors with
 * the same key from (specified) [colors] map are replaced/overwritten; when [colors] is empty,
 * unchanged [defaultColors].
 */
fun <T> setSpecifiedColors(colors: Map<T, Color>, defaultColors: Map<T, Color>): Map<T, Color> {
    if (colors.isEmpty()) return defaultColors
    val mutatedColors = defaultColors.toMutableMap()
    colors.forEach { (type, color) ->
        mutatedColors[type] = color
    }
    return mutatedColors.toMap()
}
