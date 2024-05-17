package de.oljg.glac.core.navigation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BuildCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.BuildCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreTime
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Snooze
import androidx.compose.ui.graphics.vector.ImageVector


interface GlacScreen {
    val route: String
    val tabIconSelected: ImageVector
        get() = GlacDefault.emptyImageVector
    val tabIconUnselected: ImageVector
        get() = GlacDefault.emptyImageVector
}

fun GlacScreen.isSettingsSubScreen() = this is ClockSettingsSubScreen
        || this is AlarmSettingsSubScreen
        || this is CommonSettingsSubScreen

fun GlacScreen.isSettingsScreen() = this is SettingsScreen || this.isSettingsSubScreen()


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

object ClockSettingsSubScreen : GlacScreen {
    override val route = GlacRoute.CLOCK_SETTINGS.name
    override val tabIconSelected = Icons.Filled.MoreTime
    override val tabIconUnselected = Icons.Outlined.MoreTime
}

object AlarmSettingsSubScreen : GlacScreen {
    override val route = GlacRoute.ALARM_SETTINGS.name
    override val tabIconSelected = Icons.Filled.Snooze
    override val tabIconUnselected = Icons.Outlined.Snooze
}

object CommonSettingsSubScreen : GlacScreen {
    override val route = GlacRoute.COMMON_SETTINGS.name
    override val tabIconSelected = Icons.Filled.BuildCircle
    override val tabIconUnselected = Icons.Outlined.BuildCircle
}


object AboutScreen : GlacScreen {
    override val route = GlacRoute.ABOUT.name
    override val tabIconSelected = Icons.Filled.Info
    override val tabIconUnselected = Icons.Outlined.Info
}


val glacTabScreens = listOf(ClockScreen, AlarmsScreen, SettingsScreen, AboutScreen)
val glacSettingsSubScreens =
        listOf(ClockSettingsSubScreen, AlarmSettingsSubScreen, CommonSettingsSubScreen)
val allGlacScreens = glacTabScreens + glacSettingsSubScreens
