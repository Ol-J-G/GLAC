package de.oljg.glac.core.settings.data


import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_CLOCK_DIGIT_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_COLON_FIRST_CIRCLE_POSITION
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_COLON_SECOND_CIRCLE_POSITION
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DASH_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DASH_DOTTED_PART_COUNT
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DIVIDER_LENGTH_FACTOR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DIVIDER_ROTATE_ANGLE
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_DIVIDER_THICKNESS
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_HOURS_MINUTES_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.DividerLineEnd
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_STROKE_WIDTH
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.core.util.FontWeight
import kotlinx.serialization.Serializable

@Serializable
data class ClockSettings(
    val showSeconds: Boolean = true,
    val showDaytimeMarker: Boolean = false,

    val clockCharType: String = ClockCharType.FONT.name,

    val fontName: String = "D_Din_Regular.ttf",
    val fontWeight: String = FontWeight.NORMAL.name,
    val fontStyle: String = FontStyle.NORMAL.name,

    val sevenSegmentWeight: String = SevenSegmentWeight.REGULAR.name,
    val sevenSegmentStyle: String = SevenSegmentStyle.REGULAR.name,
    val sevenSegmentOutlineSize: Float = DEFAULT_STROKE_WIDTH,
    val drawOffSegments: Boolean = true,

    val digitSizeFactor: Float = DEFAULT_CLOCK_DIGIT_SIZE_FACTOR,
    val daytimeMarkerSizeFactor: Float = DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,

    val dividerStyle: String = DividerStyle.LINE.name,
    val dividerThickness: Int = DEFAULT_DIVIDER_THICKNESS,
    val dividerLengthPercentage: Float = DEFAULT_DIVIDER_LENGTH_FACTOR,
    val dividerDashCount: Int = DEFAULT_DASH_COUNT,
    val dividerDashDottedPartCount: Int = DEFAULT_DASH_DOTTED_PART_COUNT,
    val dividerLineEnd: String = DividerLineEnd.ROUND.name,
    val dividerRotateAngle: Float = DEFAULT_DIVIDER_ROTATE_ANGLE,
    val colonFirstCirclePosition: Float = DEFAULT_COLON_FIRST_CIRCLE_POSITION,
    val colonSecondCirclePosition: Float = DEFAULT_COLON_SECOND_CIRCLE_POSITION,
    val hoursMinutesDividerChar: Char = DEFAULT_HOURS_MINUTES_DIVIDER_CHAR,
    val minutesSecondsDividerChar: Char = DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR,
    val daytimeMarkerDividerChar: Char = DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR,

    val charColor: Int? = null,

    val clockSettingsSectionPreviewIsExpanded: Boolean = false,
    val clockSettingsSectionDisplayIsExpanded: Boolean = false,
    val clockSettingsSectionClockCharIsExpanded: Boolean = false,
    val clockSettingsSectionDividerIsExpanded: Boolean = false,
    val clockSettingsSectionColorsIsExpanded: Boolean = false
)

