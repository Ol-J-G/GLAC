package de.oljg.glac.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.oljg.glac.core.navigation.common.AboutSubScreen
import de.oljg.glac.core.navigation.common.AlarmClockFullScreen
import de.oljg.glac.core.navigation.common.AlarmClockScreen
import de.oljg.glac.core.navigation.common.AlarmSettingsSubScreen
import de.oljg.glac.core.navigation.common.AlarmsScreen
import de.oljg.glac.core.navigation.common.ClockSettingsSubScreen
import de.oljg.glac.core.navigation.common.HelpSubScreen
import de.oljg.glac.core.navigation.common.InfoScreen
import de.oljg.glac.core.navigation.common.SettingsScreen
import de.oljg.glac.feature_about.ui.AboutScreen
import de.oljg.glac.feature_alarm.ui.AlarmSettingsEvent
import de.oljg.glac.feature_alarm.ui.AlarmSettingsViewModel
import de.oljg.glac.feature_alarm.ui.alarms.AlarmsListScreen
import de.oljg.glac.feature_alarm.ui.components.dialog.CancelSnoozeAlarmDialog
import de.oljg.glac.feature_alarm.ui.settings.AlarmSettingsScreen
import de.oljg.glac.feature_clock.ui.ClockSettingsViewModel
import de.oljg.glac.feature_clock.ui.clock.DigitalAlarmClockScreen
import de.oljg.glac.feature_clock.ui.settings.ClockSettingsScreen
import de.oljg.glac.feature_help.ui.HelpScreen

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
            val alarmSettingsViewModel: AlarmSettingsViewModel = hiltViewModel()
            val alarms = alarmSettingsViewModel.alarmSettingsStateFlow.collectAsState().value.alarms
            val snoozeAlarm = alarms.find { it.isSnoozeAlarm }

            var showCancelSnoozeAlarmDialog by rememberSaveable {
                mutableStateOf(false)
            }

            AnimatedVisibility(visible = showCancelSnoozeAlarmDialog) {
                CancelSnoozeAlarmDialog(
                    onCancelSnoozeAlarm = {
                        snoozeAlarm?.let {
                            alarmSettingsViewModel.onEvent(AlarmSettingsEvent.RemoveAlarm(it))
                        }
                        showCancelSnoozeAlarmDialog = false
                    },
                    onDismiss = { showCancelSnoozeAlarmDialog = false },
                    onCloseFullscreenClock = {
                        navController.navigateSingleTopTo(AlarmClockScreen.route)
                    }
                )
            }

            val clockSettingsViewModel: ClockSettingsViewModel = hiltViewModel()
            val clockSettings by clockSettingsViewModel.clockSettingsStateFlow.collectAsState()
            DigitalAlarmClockScreen(
                clockSettings = clockSettings,
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
            val viewModel: ClockSettingsViewModel = hiltViewModel()
            val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
            DigitalAlarmClockScreen(
                clockSettings = clockSettings,
                onClick = { navController.navigateSingleTopTo(AlarmClockFullScreen.route) }
            )
        }
        composable(route = AlarmsScreen.route) {
            val viewModel: AlarmSettingsViewModel = hiltViewModel()
            val alarmSettings by viewModel.alarmSettingsStateFlow.collectAsState()
            AlarmsListScreen(alarmSettings, onEvent = viewModel::onEvent)
        }
        navigation(
            route = SettingsScreen.route,
            startDestination = ClockSettingsSubScreen.route
        ) {
            composable(route = ClockSettingsSubScreen.route) {
                val viewModel: ClockSettingsViewModel = hiltViewModel()
                val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
                ClockSettingsScreen(clockSettings, onEvent = viewModel::onEvent)
            }
            composable(route = AlarmSettingsSubScreen.route) {
                val viewModel: AlarmSettingsViewModel = hiltViewModel()
                val alarmSettings by viewModel.alarmSettingsStateFlow.collectAsState()
                AlarmSettingsScreen(alarmSettings, viewModel::onEvent)
            }
        }
        navigation(
            route = InfoScreen.route,
            startDestination = HelpSubScreen.route
        ) {
            composable(route = HelpSubScreen.route) {
                HelpScreen()
            }
            composable(route = AboutSubScreen.route) {
                AboutScreen()
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }

