package de.oljg.glac.clock.digital.ui.utils

import androidx.compose.ui.unit.dp
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DAYTIME_MARKER_CHARS


enum class ClockCharType {
    FONT,
    SEVEN_SEGMENT
}


fun Char.isDaytimeMarkerChar(): Boolean {
    return this in DAYTIME_MARKER_CHARS
}


object ClockDefaults {

    /**
     * Should be the widest char in most non-monospace fonts
     * (or "W", but this char ins't used in a digital clock, but: '0-9APM')
     */
    const val WIDEST_CHAR: Char = 'M'

    /**
     * E.g. used to set colors per char.
     * Without divider chars (they have an own color property)
     */
    val DIGIT_CHARS = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    val DAYTIME_MARKER_CHARS = listOf('A', 'P', 'M')
    val CLOCK_CHARS = DIGIT_CHARS + DAYTIME_MARKER_CHARS

    val DEFAULT_CLOCK_PADDING = 16.dp
    const val DEFAULT_CLOCK_CHAR_SIZE_FACTOR = 1f // 100% (max size)
    const val DEFAULT_DAYTIME_MARKER_SIZE_FACTOR = 1f // 100% (max size)
}

