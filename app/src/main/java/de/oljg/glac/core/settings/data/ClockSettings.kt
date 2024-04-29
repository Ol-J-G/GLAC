package de.oljg.glac.core.settings.data


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
    val clockSettingsSectionDisplayExpanded: Boolean = false
)