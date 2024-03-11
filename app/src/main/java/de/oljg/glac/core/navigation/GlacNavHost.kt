package de.oljg.glac.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.oljg.glac.core.navigation.common.AboutScreen
import de.oljg.glac.core.navigation.common.AlarmsScreen
import de.oljg.glac.core.navigation.common.ClockFullScreen
import de.oljg.glac.core.navigation.common.ClockScreen
import de.oljg.glac.core.navigation.common.SettingsScreen
import de.oljg.glac.core.temp.DummyScreen

@Composable
fun GlacNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ClockFullScreen.route,
        modifier = modifier
    ) {

        //TODO: impl. ClockScreen(isFullscreen: Boolean, ...) => 2 in 1, it's the same screen basically
        composable(route = ClockFullScreen.route) {
            DummyScreen(
                text = ClockFullScreen.route,
                color = Color.Magenta,
                onClick = { navController.navigateSingleTopTo(ClockScreen.route) }
            )
        }
        composable(route = ClockScreen.route) {
            DummyScreen(
                text = ClockScreen.route,
                color = Color.Yellow,
                onClick = { navController.navigateSingleTopTo(ClockFullScreen.route) }
            )
        }
        composable(route = AlarmsScreen.route) {
            DummyScreen(
                text = AlarmsScreen.route,
                color = Color.Red
            )
        }
        composable(route = SettingsScreen.route) {
            DummyScreen(
                text = SettingsScreen.route,
                color = Color.Green
            )
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