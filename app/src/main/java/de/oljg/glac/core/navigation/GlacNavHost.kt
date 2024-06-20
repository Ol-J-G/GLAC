package de.oljg.glac.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.oljg.glac.alarms.ui.AlarmsListScreen
import de.oljg.glac.alarms.ui.components.CancelSnoozeAlarmDialog
import de.oljg.glac.clock.digital.ui.DigitalAlarmClockScreen
import de.oljg.glac.core.navigation.common.AboutScreen
import de.oljg.glac.core.navigation.common.AlarmClockFullScreen
import de.oljg.glac.core.navigation.common.AlarmClockScreen
import de.oljg.glac.core.navigation.common.AlarmSettingsSubScreen
import de.oljg.glac.core.navigation.common.AlarmsScreen
import de.oljg.glac.core.navigation.common.ClockSettingsSubScreen
import de.oljg.glac.core.navigation.common.CommonSettingsSubScreen
import de.oljg.glac.core.navigation.common.SettingsScreen
import de.oljg.glac.core.temp.DummyScreen
import de.oljg.glac.settings.alarms.ui.AlarmSettingsScreen
import de.oljg.glac.settings.alarms.ui.AlarmSettingsViewModel
import de.oljg.glac.settings.clock.ui.ClockSettingsScreen

@Composable
fun GlacNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AlarmClockFullScreen.route,
        modifier = modifier
    ) {
        composable(route = AlarmClockFullScreen.route) {
            val viewModel: AlarmSettingsViewModel = hiltViewModel()
            val alarms = viewModel.alarmSettingsStateFlow.collectAsState().value.alarms
            val snoozeAlarm = alarms.find { it.isSnoozeAlarm }

            var showCancelSnoozeAlarmDialog by rememberSaveable {
                mutableStateOf(false)
            }

            AnimatedVisibility(visible = showCancelSnoozeAlarmDialog) {
                val alarmViewModel: AlarmSettingsViewModel = hiltViewModel()
                val alarmSettings by alarmViewModel.alarmSettingsStateFlow.collectAsState()
                CancelSnoozeAlarmDialog(
                    onCancelSnoozeAlarm = {
                        snoozeAlarm?.let { alarmViewModel.removeAlarm(alarmSettings, it) }
                        showCancelSnoozeAlarmDialog = false
                    },
                    onDismiss = { showCancelSnoozeAlarmDialog = false },
                    onCloseFullscreenClock = {
                        navController.navigateSingleTopTo(AlarmClockScreen.route)
                    }
                )
            }

            DigitalAlarmClockScreen(
                isSnoozeAlarmActive = snoozeAlarm != null,
                onClick = {
                    when (snoozeAlarm) {
                        null -> navController.navigateSingleTopTo(AlarmClockScreen.route)
                        else -> showCancelSnoozeAlarmDialog = true
                    }
                },
                fullScreen = true
            )
        }
        composable(route = AlarmClockScreen.route) {
            DigitalAlarmClockScreen(
                onClick = { navController.navigateSingleTopTo(AlarmClockFullScreen.route) }
            )
        }
        composable(route = AlarmsScreen.route) {
            AlarmsListScreen()
        }
        navigation(
            route = SettingsScreen.route,
            startDestination = ClockSettingsSubScreen.route
        ) {
            composable(route = ClockSettingsSubScreen.route) {
                ClockSettingsScreen()
            }
            composable(route = AlarmSettingsSubScreen.route) {
                AlarmSettingsScreen()
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

