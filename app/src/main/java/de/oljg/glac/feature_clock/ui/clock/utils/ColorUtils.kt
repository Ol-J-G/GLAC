package de.oljg.glac.feature_clock.ui.clock.utils

import androidx.compose.ui.graphics.Color
import com.smarttoolfactory.extendedcolors.model.ColorItem
import com.smarttoolfactory.extendedcolors.util.HSLUtil
import de.oljg.glac.feature_clock.domain.model.serializer.ColorSerializer
import de.oljg.glac.feature_clock.ui.clock.utils.ClockDefaults.CLOCK_CHARS
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentDefaults.DEFAULT_LIGHTNESS_THRESHOLD
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentDefaults.OFFCOLOR_LIGHTNESS_DELTA
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentDefaults.SEVEN_SEGMENT_CHARS
import kotlinx.serialization.Serializable

@Serializable
data class ClockPartsColors(
    override val hours: DigitPairColor = DigitPairColor(),
    override val minutes: DigitPairColor = DigitPairColor(),
    override val seconds: DigitPairColor = DigitPairColor(),
    override val daytimeMarker: DaytimeMarkerColor = DaytimeMarkerColor(),
    val dividers: DividerColor = DividerColor()
) : ClockParts<Color> {

    @Serializable
    data class DigitPairColor(

        @Serializable(with = ColorSerializer::class)
        override val ones: Color? = null,

        @Serializable(with = ColorSerializer::class)
        override val tens: Color? = null
    ) : DigitPair<Color>

    @Serializable
    data class DaytimeMarkerColor( // AM/PM

        @Serializable(with = ColorSerializer::class)
        override val anteOrPost: Color? = null, // 'A' or 'P'

        @Serializable(with = ColorSerializer::class)
        override val meridiem: Color? = null // 'M'
    ) : DaytimeMarker<Color>

    @Serializable
    data class DividerColor(

        // Color for divider between hours and minutes
        @Serializable(with = ColorSerializer::class)
        val hoursMinutes: Color? = null,

        // Color for divider between minutes and seconds/daytime marker
        @Serializable(with = ColorSerializer::class)
        val minutesSeconds: Color? = null,

        // Color for divider between minutes/secondes and daytime marker
        @Serializable(with = ColorSerializer::class)
        val daytimeMarker: Color? = null
    )
}

fun defaultClockCharColors(charColor: Color): Map<Char, Color> {
    return buildMap {
        CLOCK_CHARS.forEach { char ->
            put(char, charColor)
        }
    }
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


/**
 * Uses the superb lib Compose-Extented-Colors to add percentage (.01f == 1%) lighntess to a
 * compose color, to make the color brighter (positive percentage) or darker (negative percentage).
 *
 * First, the color will be converted to HSL color format, just to add the percentage to the
 * lightning part ('HS >L<' , which is index 2).
 * Finally, the lightness changed HSL color will be converted back to a ColorInt, that will be
 * used as param in compose's Color(@ColorInt..) function, that returns an
 * androidx.compose.ui.graphics.Color to use in compose as usual.
 */
private fun Color.addLightness(percentage: Float) =
    Color(HSLUtil.hslToColorInt(
        ColorItem(this).hslArray.apply { this[2] += percentage } )
    )


private fun Color.lighten(percentage: Float) = addLightness(percentage)
private fun Color.darken(percentage: Float) = addLightness(-percentage)

private val Color.lightness: Float get() = ColorItem(this).hslArray[2]


fun Color.lightenOrDarken(
    threshold: Float = DEFAULT_LIGHTNESS_THRESHOLD,
    amount: Float = OFFCOLOR_LIGHTNESS_DELTA
) = when {
    this.lightness < threshold -> this.lighten(amount)
    else -> this.darken(amount)
}
