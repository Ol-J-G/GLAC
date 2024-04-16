package de.oljg.glac.core.util

import de.oljg.glac.clock.digital.ui.utils.ClockParts
import de.oljg.glac.clock.digital.ui.utils.DaytimeMarker
import de.oljg.glac.clock.digital.ui.utils.DigitClockPart
import de.oljg.glac.core.util.TestTags.DAYTIME_MARKER_ANTE_OR_POST
import de.oljg.glac.core.util.TestTags.DAYTIME_MARKER_MERIDIEM
import de.oljg.glac.core.util.TestTags.HOURS_ONES
import de.oljg.glac.core.util.TestTags.HOURS_TENS
import de.oljg.glac.core.util.TestTags.MINUTES_ONES
import de.oljg.glac.core.util.TestTags.MINUTES_TENS
import de.oljg.glac.core.util.TestTags.SECONDS_ONES
import de.oljg.glac.core.util.TestTags.SECONDS_TENS

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
}

data class ClockPartsTestTags(
    override val hours: DigitTestTag = DigitTestTag(tens = HOURS_TENS, ones = HOURS_ONES),
    override val minutes: DigitTestTag = DigitTestTag(tens = MINUTES_TENS, ones = MINUTES_ONES),
    override val seconds: DigitTestTag = DigitTestTag(tens = SECONDS_TENS, ones = SECONDS_ONES),
    override val daytimeMarker: DaytimeMarkerTestTag =
        DaytimeMarkerTestTag(
            anteOrPost = DAYTIME_MARKER_ANTE_OR_POST,
            meridiem = DAYTIME_MARKER_MERIDIEM
        ),
): ClockParts {
    data class DigitTestTag(
        override val ones: String,
        override val tens: String
    ) : DigitClockPart

    data class DaytimeMarkerTestTag( // AM/PM
        override val anteOrPost: String, // 'A' or 'P'
        override val meridiem: String // 'M'
    ) : DaytimeMarker
}