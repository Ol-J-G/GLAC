package de.oljg.glac.core.util

object CommonClockUtils {
    /**
     * E.g. used to set colors per char.
     * Without divider chars (they have an own color property)
     */
    val DIGIT_CHARS = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    val DAYTIME_MARKER_CHARS = listOf('A', 'P', 'M')
    val CLOCK_CHARS = DIGIT_CHARS + DAYTIME_MARKER_CHARS
}
