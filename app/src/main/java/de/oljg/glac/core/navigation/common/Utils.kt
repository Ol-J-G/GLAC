package de.oljg.glac.core.navigation.common

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class GlacRoute {
    CLOCK,
    FULLSCREEN_CLOCK,
    ALARMS,
    SETTINGS,
    ABOUT
}
object GlacDefault {

    // to prevent null/-able in GlacScreen interface
    val emptyImageVector = ImageVector.Builder(
        name = "empty",
        defaultWidth = 0.dp,
        defaultHeight = 0.dp,
        viewportWidth = 0f,
        viewportHeight = 0f
    ).build()
}
