package de.oljg.glac.core.navigation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ErrorOutline
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

fun GlacScreen.isSettingsSubScreen() = this is ClockSettingsSubScreen
        || this is AlarmSettingsSubScreen

fun GlacScreen.isSettingsScreen() = this is SettingsScreen || this.isSettingsSubScreen()

fun GlacScreen.isInfoSubScreen() = this is HelpSubScreen || this is AboutSubScreen

fun GlacScreen.isInfoScreen() = this is InfoScreen || this.isInfoSubScreen()


object AlarmClockScreen : GlacScreen {
    override val route = GlacRoute.CLOCK.name
    override val tabIconSelected = Icons.Filled.AccessTime
    override val tabIconUnselected = Icons.Outlined.AccessTime
}

// used for routing only (not part of tab bar)
object AlarmClockFullScreen : GlacScreen {
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
    override val tabIconSelected = Icons.Filled.AccessTimeFilled
    override val tabIconUnselected = Icons.Filled.AccessTime
}

object AlarmSettingsSubScreen : GlacScreen {
    override val route = GlacRoute.ALARM_SETTINGS.name
    override val tabIconSelected = Icons.Filled.Alarm
    override val tabIconUnselected = Icons.Outlined.Alarm
}

object InfoScreen : GlacScreen {
    override val route = GlacRoute.INFO.name
    override val tabIconSelected = Icons.Filled.Info
    override val tabIconUnselected = Icons.Outlined.Info
}

object HelpSubScreen : GlacScreen {
    override val route = GlacRoute.INFO_HELP.name
    override val tabIconSelected = Icons.AutoMirrored.Filled.Help
    override val tabIconUnselected = Icons.AutoMirrored.Filled.HelpOutline
}

object AboutSubScreen : GlacScreen {
    override val route = GlacRoute.INFO_ABOUT.name
    override val tabIconSelected = Icons.Filled.Error
    override val tabIconUnselected = Icons.Filled.ErrorOutline
}


val glacTabScreens = listOf(AlarmClockScreen, AlarmsScreen, SettingsScreen, InfoScreen)
val glacSettingsSubScreens = listOf(ClockSettingsSubScreen, AlarmSettingsSubScreen)
val glacInfoSubScreens = listOf(HelpSubScreen, AboutSubScreen)
val allGlacScreens = glacTabScreens + glacSettingsSubScreens + glacInfoSubScreens
