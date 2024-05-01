package de.oljg.glac.core.settings.data


import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.core.util.FontWeight
import kotlinx.serialization.Serializable

@Serializable
data class ClockSettings(
    val showSeconds: Boolean = true,
    val showDaytimeMarker: Boolean = false,
    val fontName: String = "D_Din_Regular.ttf", // TODO:  material theme fontfamily as default
    val fontWeight: String = FontWeight.NORMAL.name,
    val fontStyle: String = FontStyle.NORMAL.name,
    val selectedClockCharType: String = ClockCharType.FONT.name,
    val sevenSegmentWeight: String = SevenSegmentWeight.REGULAR.name,
    val sevenSegmentStyle: String = SevenSegmentStyle.REGULAR.name,
    val clockSettingsSectionPreviewIsExpanded: Boolean = false,
    val clockSettingsSectionDisplayIsExpanded: Boolean = false,
    val clockSettingsSectionClockCharIsExpanded: Boolean = false
)