package de.oljg.glac.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.oljg.glac.R
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
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GlacNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    @Suppress("UNCHECKED_CAST")
    val clockSettingsViewModel = viewModel<ClockSettingsViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ClockSettingsViewModel(context) as T //TODO: get rid of this => try to use DH+@HiltViewModel and inject repo to VM and use it as hiltViewModel() in screen!??
            }
        }
    )

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
                viewModel = clockSettingsViewModel,
                onClick = { navController.navigateSingleTopTo(ClockScreen.route) },
                fontFamily = dDinfontfamily,
                fullScreen = true,
            )
        }
        composable(route = ClockScreen.route) {
            DigitalClockScreen(
                viewModel = clockSettingsViewModel,
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
            startDestination = ClockSettingsSubScreen.route
        ) {
            composable(route = ClockSettingsSubScreen.route) {
                ClockSettingsScreen(
                    viewModel = clockSettingsViewModel
                )
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

