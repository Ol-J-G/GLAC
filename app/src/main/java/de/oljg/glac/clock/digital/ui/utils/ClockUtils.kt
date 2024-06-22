package de.oljg.glac.clock.digital.ui.utils

import androidx.compose.ui.unit.dp
import de.oljg.glac.core.util.CommonClockUtils.DAYTIME_MARKER_CHARS


enum class ClockCharType {
    FONT,
    SEVEN_SEGMENT
}


fun Char.isDaytimeMarkerChar(): Boolean {
    return this in DAYTIME_MARKER_CHARS
}


interface ClockParts<T> {
    val hours: DigitPair<T>?
    val minutes: DigitPair<T>?
    val seconds: DigitPair<T>?
    val daytimeMarker: DaytimeMarker<T>?
}
interface DigitPair<T> {
    val ones: T?
    val tens: T?
}
interface DaytimeMarker<T> {
    val anteOrPost: T?
    val meridiem: T?
}


object ClockDefaults {

    /**
     * Should be the widest letter in most non-monospace fonts
     * (or "W", but this char ins't used in a digital clock, but: 'APM')
     */
    const val WIDEST_LETTER: Char = 'M' //TODO_LATER: maybe let user enter a char (in case an imported font is "special"


    // Should be the widest digit in most non-monospace fonts
    const val WIDEST_DIGIT: Char = '8'


    val DEFAULT_CLOCK_PADDING = 0.dp

    const val DEFAULT_DIGIT_SIZE_FACTOR = 1f // 100% (max size)
    const val DEFAULT_DAYTIME_MARKER_SIZE_FACTOR = 1f // 100% (max size)
}

