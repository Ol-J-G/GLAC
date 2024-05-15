package de.oljg.glac.core.settings.data


import androidx.compose.ui.graphics.Color
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_CLOCK_DIGIT_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockPartsColors
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
import de.oljg.glac.clock.digital.ui.utils.Segment
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_OUTLINE_SIZE
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.core.util.FontWeight
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

//TODO: add theme, maybe val themes = Map<String, ClockSettings>???
@Serializable
data class ClockSettings(
    val showSeconds: Boolean = true,
    val showDaytimeMarker: Boolean = false,

    val clockCharType: ClockCharType = ClockCharType.FONT,

    val fontName: String = "D_Din_Regular.ttf",
    val fontWeight: FontWeight = FontWeight.NORMAL,
    val fontStyle: FontStyle = FontStyle.NORMAL,

    val sevenSegmentWeight: SevenSegmentWeight = SevenSegmentWeight.REGULAR,
    val sevenSegmentStyle: SevenSegmentStyle = SevenSegmentStyle.REGULAR,
    val sevenSegmentOutlineSize: Float = DEFAULT_OUTLINE_SIZE,
    val drawOffSegments: Boolean = true,

    val digitSizeFactor: Float = DEFAULT_CLOCK_DIGIT_SIZE_FACTOR,
    val daytimeMarkerSizeFactor: Float = DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,

    val dividerStyle: DividerStyle = DividerStyle.LINE,
    val dividerThickness: Int = DEFAULT_DIVIDER_THICKNESS,
    val dividerLengthPercentage: Float = DEFAULT_DIVIDER_LENGTH_FACTOR,
    val dividerDashCount: Int = DEFAULT_DASH_COUNT,
    val dividerDashDottedPartCount: Int = DEFAULT_DASH_DOTTED_PART_COUNT,
    val dividerLineEnd: DividerLineEnd = DividerLineEnd.ROUND,
    val dividerRotateAngle: Float = DEFAULT_DIVIDER_ROTATE_ANGLE,
    val colonFirstCirclePosition: Float = DEFAULT_COLON_FIRST_CIRCLE_POSITION,
    val colonSecondCirclePosition: Float = DEFAULT_COLON_SECOND_CIRCLE_POSITION,
    val hoursMinutesDividerChar: Char = DEFAULT_HOURS_MINUTES_DIVIDER_CHAR,
    val minutesSecondsDividerChar: Char = DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR,
    val daytimeMarkerDividerChar: Char = DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR,

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
    val setSegmentColors: Boolean = false,

    val clockSettingsSectionPreviewIsExpanded: Boolean = false,
    val clockSettingsSectionDisplayIsExpanded: Boolean = false,
    val clockSettingsSectionClockCharIsExpanded: Boolean = false,
    val clockSettingsSectionDividerIsExpanded: Boolean = false,
    val clockSettingsSectionColorsIsExpanded: Boolean = false
)

