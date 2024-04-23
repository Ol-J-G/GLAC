package de.oljg.glac.core.settings.data

import kotlinx.serialization.Serializable

@Serializable
data class ClockSettings(
    val showSeconds: Boolean = true,
    val showDaytimeMarker: Boolean = false
)