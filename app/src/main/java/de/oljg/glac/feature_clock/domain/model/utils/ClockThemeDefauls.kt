package de.oljg.glac.feature_clock.domain.model.utils

import de.oljg.glac.core.utils.FontStyle
import de.oljg.glac.core.utils.FontWeight
import de.oljg.glac.feature_clock.ui.clock.utils.ClockCharType
import de.oljg.glac.feature_clock.ui.clock.utils.ClockDefaults
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults
import de.oljg.glac.feature_clock.ui.clock.utils.DividerLineEnd
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentDefaults
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentWeight

object ClockThemeDefauls {
    const val DEFAULT_SHOW_SECONDS = true
    const val DEFAULT_SHOW_DAYTIME_MARKER = false

    val DEFAULT_CLOCK_CHAR_TYPE = ClockCharType.FONT

    const val DEFAULT_FONT_NAME = "D_Din_Regular.ttf" // available in assets/fonts directory
    val DEFAULT_FONT_WEIGHT = FontWeight.NORMAL
    val DEFAULT_FONT_STYLE = FontStyle.NORMAL

    val DEFAULT_SEVEN_SEGMENT_WEIGHT = SevenSegmentWeight.REGULAR
    val DEFAULT_SEVEN_SEGMENT_STYLE = SevenSegmentStyle.REGULAR
    const val DEFAULT_SEVEN_SEGMENT_OUTLINE_SIZE = SevenSegmentDefaults.DEFAULT_OUTLINE_SIZE
    const val DEFAULT_DRAW_OFF_SEGMENTS = true

    const val DEFAULT_DIGIT_SIZE_FACTOR = ClockDefaults.DEFAULT_DIGIT_SIZE_FACTOR
    const val DEFAULT_DAYTIME_MARKER_SIZE_FACTOR = ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR

    val DEFAULT_DIVIDER_STYLE = DividerStyle.LINE
    const val DEFAULT_DIVIDER_THICKNESS = DividerDefaults.DEFAULT_THICKNESS
    const val DEFAULT_DIVIDER_LENGTH_PERCENTAGE = DividerDefaults.DEFAULT_LENGTH_PERCENTAGE
    const val DEFAULT_DIVIDER_DASH_COUNT = DividerDefaults.DEFAULT_DASH_COUNT
    const val DEFAULT_DIVIDER_DASH_DOTTED_PART_COUNT = DividerDefaults.DEFAULT_DASH_DOTTED_PART_COUNT
    val DEFAULT_DIVIDER_LINE_END = DividerLineEnd.ROUND
    const val DEFAULT_DIVIDER_ROTATE_ANGLE = DividerDefaults.DEFAULT_ROTATE_ANGLE
    const val DEFAULT_COLON_FIRST_CIRCLE_POSITION = DividerDefaults.DEFAULT_COLON_FIRST_CIRCLE_POSITION
    const val DEFAULT_COLON_SECOND_CIRCLE_POSITION = DividerDefaults.DEFAULT_COLON_SECOND_CIRCLE_POSITION
    const val DEFAULT_HOURS_MINUTES_DIVIDER_CHAR = DividerDefaults.DEFAULT_HOURS_MINUTES_DIVIDER_CHAR
    const val DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR = DividerDefaults.DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR
    const val DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR = DividerDefaults.DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR

    const val DEFAULT_USE_COLORS_PER_CHAR = false
    const val DEFAULT_USE_COLORS_PER_CLOCK_PART = false
    const val DEFAULT_USE_SEGMENT_COLORS = false
}
