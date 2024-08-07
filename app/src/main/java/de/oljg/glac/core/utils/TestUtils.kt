package de.oljg.glac.core.utils

import de.oljg.glac.core.utils.TestTags.DAYTIME_MARKER_ANTE_OR_POST
import de.oljg.glac.core.utils.TestTags.DAYTIME_MARKER_MERIDIEM
import de.oljg.glac.core.utils.TestTags.HOURS_ONES
import de.oljg.glac.core.utils.TestTags.HOURS_TENS
import de.oljg.glac.core.utils.TestTags.MINUTES_ONES
import de.oljg.glac.core.utils.TestTags.MINUTES_TENS
import de.oljg.glac.core.utils.TestTags.SECONDS_ONES
import de.oljg.glac.core.utils.TestTags.SECONDS_TENS
import de.oljg.glac.feature_clock.ui.clock.utils.ClockParts
import de.oljg.glac.feature_clock.ui.clock.utils.DaytimeMarker
import de.oljg.glac.feature_clock.ui.clock.utils.DigitPair

object TestTags {
    const val FONT_MEASUREMENT = "FONT_MEASUREMENT"
    const val DIGITAL_CLOCK_PORTRAIT_LAYOUT = "DIGITAL_CLOCK_PORTRAIT_LAYOUT"
    const val DIGITAL_CLOCK_LANDSCAPE_LAYOUT = "DIGITAL_CLOCK_LANDSCAPE_LAYOUT"

    const val HOURS_TENS = "HOURS_TENS"
    const val HOURS_ONES = "HOURS_ONES"
    const val MINUTES_TENS = "MINUTES_TENS"
    const val MINUTES_ONES = "MINUTES_ONES"
    const val SECONDS_TENS = "SECONDS_TENS"
    const val SECONDS_ONES = "SECONDS_ONES"
    const val DAYTIME_MARKER_ANTE_OR_POST = "DAYTIME_MARKER_ANTE_OR_POST"
    const val DAYTIME_MARKER_MERIDIEM = "DAYTIME_MARKER_MERIDIEM"

    const val CHAR_DIVIDER = "CHAR_DIVIDER"
    const val LINE_DIVIDER = "LINE_DIVIDER"
    const val COLON_DIVIDER = "COLON_DIVIDER"

    const val DIGITAL_CLOCK = "DIGITAL_CLOCK"
    const val CLOCK_TAB = "CLOCK_TAB"
    const val ALARMS_TAB = "ALARMS_TAB"
    const val SETTINGS_TAB = "SETTINGS_TAB"
    const val INFO_TAB = "INFO_TAB"

    const val CLOCK_SETTINGS_SCREEN = "CLOCK_SETTINGS_SCREEN"
    const val CLOCK_PREVIEW_EXPANDABLE_SECTION = "CLOCK_PREVIEW_EXPANDABLE_SECTION"
    const val CLOCK_SETTINGS_DISPLAY_EXPANDABLE_SECTION = "CLOCK_SETTINGS_DISPLAY_EXPANDABLE_SECTION"
    const val SHOW_SECONDS_SWITCH = "SHOW_SECONDS_SWITCH"

}


data class ClockPartsTestTags(
    override val hours: DigitPair<String> =
            DigitPairTestTag(tens = HOURS_TENS, ones = HOURS_ONES),
    override val minutes: DigitPair<String> =
            DigitPairTestTag(tens = MINUTES_TENS, ones = MINUTES_ONES),
    override val seconds: DigitPair<String> =
            DigitPairTestTag(tens = SECONDS_TENS, ones = SECONDS_ONES),
    override val daytimeMarker: DaytimeMarker<String> =
        DaytimeMarkerTestTag(
            anteOrPost = DAYTIME_MARKER_ANTE_OR_POST,
            meridiem = DAYTIME_MARKER_MERIDIEM
        )
) : ClockParts<String> {
    data class DigitPairTestTag(
        override val ones: String,
        override val tens: String
    ) : DigitPair<String>

    data class DaytimeMarkerTestTag( // AM/PM
        override val anteOrPost: String, // 'A' or 'P'
        override val meridiem: String // 'M'
    ) : DaytimeMarker<String>
}
