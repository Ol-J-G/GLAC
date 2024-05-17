package de.oljg.glac.core.settings.data

import androidx.compose.ui.graphics.Color
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults
import de.oljg.glac.clock.digital.ui.utils.ClockPartsColors
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults
import de.oljg.glac.clock.digital.ui.utils.DividerLineEnd
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.Segment
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.core.util.FontWeight
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

@Serializable
data class ClockTheme(
    val showSeconds: Boolean = true,
    val showDaytimeMarker: Boolean = false,

    val clockCharType: ClockCharType = ClockCharType.FONT,

    val fontName: String = "D_Din_Regular.ttf",
    val fontWeight: FontWeight = FontWeight.NORMAL,
    val fontStyle: FontStyle = FontStyle.NORMAL,

    val sevenSegmentWeight: SevenSegmentWeight = SevenSegmentWeight.REGULAR,
    val sevenSegmentStyle: SevenSegmentStyle = SevenSegmentStyle.REGULAR,
    val sevenSegmentOutlineSize: Float = SevenSegmentDefaults.DEFAULT_OUTLINE_SIZE,
    val drawOffSegments: Boolean = true,

    val digitSizeFactor: Float = ClockDefaults.DEFAULT_CLOCK_DIGIT_SIZE_FACTOR,
    val daytimeMarkerSizeFactor: Float = ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,

    val dividerStyle: DividerStyle = DividerStyle.LINE,
    val dividerThickness: Int = DividerDefaults.DEFAULT_DIVIDER_THICKNESS,
    val dividerLengthPercentage: Float = DividerDefaults.DEFAULT_DIVIDER_LENGTH_FACTOR,
    val dividerDashCount: Int = DividerDefaults.DEFAULT_DASH_COUNT,
    val dividerDashDottedPartCount: Int = DividerDefaults.DEFAULT_DASH_DOTTED_PART_COUNT,
    val dividerLineEnd: DividerLineEnd = DividerLineEnd.ROUND,
    val dividerRotateAngle: Float = DividerDefaults.DEFAULT_DIVIDER_ROTATE_ANGLE,
    val colonFirstCirclePosition: Float = DividerDefaults.DEFAULT_COLON_FIRST_CIRCLE_POSITION,
    val colonSecondCirclePosition: Float = DividerDefaults.DEFAULT_COLON_SECOND_CIRCLE_POSITION,
    val hoursMinutesDividerChar: Char = DividerDefaults.DEFAULT_HOURS_MINUTES_DIVIDER_CHAR,
    val minutesSecondsDividerChar: Char = DividerDefaults.DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR,
    val daytimeMarkerDividerChar: Char = DividerDefaults.DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR,

    @Serializable(with = ColorSerializer::class)
    val charColor: Color? = null,

    @Serializable(with = ColorSerializer::class)
    val dividerColor: Color? = null,

    @Serializable(with = CharColorsSerializer::class)
    val charColors: PersistentMap<Char, @Serializable(with = ColorSerializer::class) Color> =
            persistentMapOf(),
    val setColorsPerChar: Boolean = false,

    val setColorsPerClockPart: Boolean = false,
    val clockPartsColors: ClockPartsColors = ClockPartsColors(),

    @Serializable(with = SegmentColorsSerializer::class)
    val segmentColors: PersistentMap<Segment, @Serializable(with = ColorSerializer::class) Color> =
            persistentMapOf(),
    val setSegmentColors: Boolean = false
)
