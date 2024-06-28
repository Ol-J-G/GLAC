package de.oljg.glac.feature_clock.domain.model

import androidx.compose.ui.graphics.Color
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.core.util.FontWeight
import de.oljg.glac.feature_clock.domain.model.serializer.CharColorsSerializer
import de.oljg.glac.feature_clock.domain.model.serializer.ColorSerializer
import de.oljg.glac.feature_clock.domain.model.serializer.SegmentColorsSerializer
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_CLOCK_CHAR_TYPE
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_COLON_FIRST_CIRCLE_POSITION
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_COLON_SECOND_CIRCLE_POSITION
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_DIGIT_SIZE_FACTOR
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_DIVIDER_DASH_COUNT
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_DIVIDER_DASH_DOTTED_PART_COUNT
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_DIVIDER_LENGTH_PERCENTAGE
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_DIVIDER_LINE_END
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_DIVIDER_ROTATE_ANGLE
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_DIVIDER_STYLE
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_DIVIDER_THICKNESS
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_DRAW_OFF_SEGMENTS
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_FONT_NAME
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_FONT_STYLE
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_FONT_WEIGHT
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_HOURS_MINUTES_DIVIDER_CHAR
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_USE_COLORS_PER_CHAR
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_USE_COLORS_PER_CLOCK_PART
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_USE_SEGMENT_COLORS
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_SEVEN_SEGMENT_OUTLINE_SIZE
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_SEVEN_SEGMENT_STYLE
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_SEVEN_SEGMENT_WEIGHT
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_SHOW_DAYTIME_MARKER
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls.DEFAULT_SHOW_SECONDS
import de.oljg.glac.feature_clock.ui.clock.utils.ClockCharType
import de.oljg.glac.feature_clock.ui.clock.utils.ClockPartsColors
import de.oljg.glac.feature_clock.ui.clock.utils.DividerLineEnd
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.feature_clock.ui.clock.utils.Segment
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentWeight
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

@Serializable
data class ClockTheme(
    val showSeconds: Boolean = DEFAULT_SHOW_SECONDS,
    val showDaytimeMarker: Boolean = DEFAULT_SHOW_DAYTIME_MARKER,

    val clockCharType: ClockCharType = DEFAULT_CLOCK_CHAR_TYPE,

    val fontName: String = DEFAULT_FONT_NAME,
    val fontWeight: FontWeight = DEFAULT_FONT_WEIGHT,
    val fontStyle: FontStyle = DEFAULT_FONT_STYLE,

    val sevenSegmentWeight: SevenSegmentWeight = DEFAULT_SEVEN_SEGMENT_WEIGHT,
    val sevenSegmentStyle: SevenSegmentStyle = DEFAULT_SEVEN_SEGMENT_STYLE,
    val sevenSegmentOutlineSize: Float = DEFAULT_SEVEN_SEGMENT_OUTLINE_SIZE,
    val drawOffSegments: Boolean = DEFAULT_DRAW_OFF_SEGMENTS,

    val digitSizeFactor: Float = DEFAULT_DIGIT_SIZE_FACTOR,
    val daytimeMarkerSizeFactor: Float = DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,

    val dividerStyle: DividerStyle = DEFAULT_DIVIDER_STYLE,
    val dividerThickness: Int = DEFAULT_DIVIDER_THICKNESS,
    val dividerLengthPercentage: Float = DEFAULT_DIVIDER_LENGTH_PERCENTAGE,
    val dividerDashCount: Int = DEFAULT_DIVIDER_DASH_COUNT,
    val dividerDashDottedPartCount: Int = DEFAULT_DIVIDER_DASH_DOTTED_PART_COUNT,
    val dividerLineEnd: DividerLineEnd = DEFAULT_DIVIDER_LINE_END,
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
    val useColorsPerChar: Boolean = DEFAULT_USE_COLORS_PER_CHAR,

    val clockPartsColors: ClockPartsColors = ClockPartsColors(),
    val useColorsPerClockPart: Boolean = DEFAULT_USE_COLORS_PER_CLOCK_PART,

    @Serializable(with = SegmentColorsSerializer::class)
    val segmentColors: PersistentMap<Segment, @Serializable(with = ColorSerializer::class) Color> =
            persistentMapOf(),
    val useSegmentColors: Boolean = DEFAULT_USE_SEGMENT_COLORS,

    @Serializable(with = ColorSerializer::class)
    val backgroundColor: Color? = null
)
