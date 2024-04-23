package de.oljg.glac.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.oljg.glac.clock.digital.ui.DigitalClockScreen
import de.oljg.glac.core.navigation.common.AboutScreen
import de.oljg.glac.core.navigation.common.AlarmSettingsSubScreen
import de.oljg.glac.core.navigation.common.AlarmsScreen
import de.oljg.glac.core.navigation.common.ClockFullScreen
import de.oljg.glac.core.navigation.common.ClockScreen
import de.oljg.glac.core.navigation.common.ClockSettingsSubScreen
import de.oljg.glac.core.navigation.common.CommonSettingsSubScreen
import de.oljg.glac.core.navigation.common.SettingsScreen
import de.oljg.glac.core.temp.DummyScreen
import de.oljg.glac.settings.clock.ui.ClockSettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GlacNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ClockFullScreen.route,
        modifier = modifier
    ) {
        composable(route = ClockFullScreen.route) {
            DigitalClockScreen(
                onClick = { navController.navigateSingleTopTo(ClockScreen.route) },
                fullScreen = true,
            )
        }
        composable(route = ClockScreen.route) {
            DigitalClockScreen(
                onClick = { navController.navigateSingleTopTo(ClockFullScreen.route) },
            )
        }
        composable(route = AlarmsScreen.route) {
            DummyScreen(
                text = AlarmsScreen.route,
                color = Color.Red
            )
        }
        navigation(
            route = SettingsScreen.route,
            startDestination = ClockSettingsSubScreen.route
        ) {
            composable(route = ClockSettingsSubScreen.route) {
                ClockSettingsScreen()
            }
            composable(route = AlarmSettingsSubScreen.route) {
                DummyScreen(
                    text = AlarmSettingsSubScreen.route,
                    color = Color.Green.copy(alpha = .7f)
                )
            }
            composable(route = CommonSettingsSubScreen.route) {
                DummyScreen(
                    text = CommonSettingsSubScreen.route,
                    color = Color.Green.copy(alpha = .3f)
                )
            }
        }
        composable(route = AboutScreen.route) {
            DummyScreen(
                text = AboutScreen.route,
                color = Color.Gray
            )
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }

