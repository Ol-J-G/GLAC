package de.oljg.glac.core.settings.data


import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_CLOCK_DIGIT_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
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

    val selectedClockCharType: String = ClockCharType.FONT.name,

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
    val dividerThickness: Float = 1f,

    val clockSettingsSectionPreviewIsExpanded: Boolean = false,
    val clockSettingsSectionDisplayIsExpanded: Boolean = false,
    val clockSettingsSectionClockCharIsExpanded: Boolean = false,
    val clockSettingsSectionDividerIsExpanded: Boolean = false
)

