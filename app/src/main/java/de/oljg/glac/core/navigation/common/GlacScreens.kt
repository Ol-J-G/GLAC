package de.oljg.glac.core.navigation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector


interface GlacScreen {
    val route: String
    val tabIconFilled: ImageVector
        get() = GlacDefault.emptyImageVector
    val tabIconOutlined: ImageVector
        get() = GlacDefault.emptyImageVector
}

object ClockScreen: GlacScreen {
    override val route = GlacRoute.CLOCK.name
    override val tabIconFilled = Icons.Filled.AccessTime
    override val tabIconOutlined = Icons.Outlined.AccessTime
}

// used for routing only (not part of tab bar)
object ClockFullScreen: GlacScreen {
    override val route = GlacRoute.FULLSCREEN_CLOCK.name
}

object AlarmsScreen: GlacScreen {
    override val route = GlacRoute.ALARMS.name
    override val tabIconFilled = Icons.Filled.Alarm
    override val tabIconOutlined = Icons.Outlined.Alarm
}

object SettingsScreen: GlacScreen {
    override val route = GlacRoute.SETTINGS.name
    override val tabIconFilled = Icons.Filled.Settings
    override val tabIconOutlined = Icons.Outlined.Settings
}

object AboutScreen: GlacScreen {
    override val route = GlacRoute.ABOUT.name
    override val tabIconFilled = Icons.Filled.Info
    override val tabIconOutlined = Icons.Outlined.Info
}

val tabRowScreens = listOf(ClockScreen, AlarmsScreen, SettingsScreen, AboutScreen)
