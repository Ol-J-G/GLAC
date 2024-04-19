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
    val tabIconSelected: ImageVector
        get() = GlacDefault.emptyImageVector
    val tabIconUnselected: ImageVector
        get() = GlacDefault.emptyImageVector
}

fun GlacScreen.isSettingsSubScreen(): Boolean {
    return this is ClockSettingsScreen ||
            this is AlarmSettingsScreen ||
            this is CommonSettingsScreen
}

object ClockScreen : GlacScreen {
    override val route = GlacRoute.CLOCK.name
    override val tabIconSelected = Icons.Filled.AccessTime
    override val tabIconUnselected = Icons.Outlined.AccessTime
}

// used for routing only (not part of tab bar)
object ClockFullScreen : GlacScreen {
    override val route = GlacRoute.FULLSCREEN_CLOCK.name
}

object AlarmsScreen : GlacScreen {
    override val route = GlacRoute.ALARMS.name
    override val tabIconSelected = Icons.Filled.Alarm
    override val tabIconUnselected = Icons.Outlined.Alarm
}

object SettingsScreen : GlacScreen {
    override val route = GlacRoute.SETTINGS.name
    override val tabIconSelected = Icons.Filled.Settings
    override val tabIconUnselected = Icons.Outlined.Settings
}

object ClockSettingsScreen : GlacScreen {
    override val route = GlacRoute.CLOCK_SETTINGS.name
    override val tabIconSelected = Icons.Filled.Settings
    override val tabIconUnselected = Icons.Outlined.Settings
}

object AlarmSettingsScreen : GlacScreen {
    override val route = GlacRoute.ALARM_SETTINGS.name
    override val tabIconSelected = Icons.Filled.Settings
    override val tabIconUnselected = Icons.Outlined.Settings
}

object CommonSettingsScreen : GlacScreen {
    override val route = GlacRoute.COMMON_SETTINGS.name
    override val tabIconSelected = Icons.Filled.Settings
    override val tabIconUnselected = Icons.Outlined.Settings
}


object AboutScreen : GlacScreen {
    override val route = GlacRoute.ABOUT.name
    override val tabIconSelected = Icons.Filled.Info
    override val tabIconUnselected = Icons.Outlined.Info
}

val glacScreens = listOf(
    ClockScreen,
    AlarmsScreen,
    SettingsScreen,
    ClockSettingsScreen,
    AlarmSettingsScreen,
    CommonSettingsScreen,
    AboutScreen
)

val glacTabScreens = listOf(ClockScreen, AlarmsScreen, SettingsScreen, AboutScreen)
val settingsSubScreens = listOf(ClockSettingsScreen, AlarmSettingsScreen, CommonSettingsScreen)