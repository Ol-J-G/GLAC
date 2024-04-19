package de.oljg.glac.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.DigitalClockScreen
import de.oljg.glac.core.navigation.common.AboutScreen
import de.oljg.glac.core.navigation.common.AlarmSettingsScreen
import de.oljg.glac.core.navigation.common.AlarmsScreen
import de.oljg.glac.core.navigation.common.ClockFullScreen
import de.oljg.glac.core.navigation.common.ClockScreen
import de.oljg.glac.core.navigation.common.ClockSettingsScreen
import de.oljg.glac.core.navigation.common.CommonSettingsScreen
import de.oljg.glac.core.navigation.common.SettingsScreen
import de.oljg.glac.core.temp.DummyScreen

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

        val dDinfontfamily = FontFamily(
            Font(R.font.d_din_regular, weight = FontWeight.Normal),
            Font(
                R.font.d_din_italic,
                weight = FontWeight.Normal,
                style = FontStyle.Italic
            ),
            Font(R.font.d_din_bold, weight = FontWeight.Bold)
        )

        composable(route = ClockFullScreen.route) {
            DigitalClockScreen(
                fullScreen = true,
                onClick = { navController.navigateSingleTopTo(ClockScreen.route) },
                fontFamily = dDinfontfamily
            )
        }
        composable(route = ClockScreen.route) {
            DigitalClockScreen(
                fullScreen = false,
                onClick = { navController.navigateSingleTopTo(ClockFullScreen.route) },
                fontFamily = dDinfontfamily
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
            startDestination = ClockSettingsScreen.route
        ) {
            composable(route = ClockSettingsScreen.route) {
                DummyScreen(
                    text = ClockSettingsScreen.route,
                    color = Color.Green
                )
            }
            composable(route = AlarmSettingsScreen.route) {
                DummyScreen(
                    text = AlarmSettingsScreen.route,
                    color = Color.Green.copy(alpha = .7f)
                )
            }
            composable(route = CommonSettingsScreen.route) {
                DummyScreen(
                    text = CommonSettingsScreen.route,
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