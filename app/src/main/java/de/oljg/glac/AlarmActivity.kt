package de.oljg.glac

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.alarms.ui.utils.plus
import de.oljg.glac.clock.digital.ui.DigitalClockScreen
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.data.AlarmSettings
import de.oljg.glac.settings.alarms.ui.AlarmSettingsViewModel
import de.oljg.glac.ui.theme.GLACTheme
import kotlin.time.Duration.Companion.days

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * This activity will be triggered by system's AlarmManager, actual compose entry point
         */
        setContent {
            val alarmToBeLaunched = handleAlarmToBeLaunched()

            GLACTheme {
                /**
                 * "Wait until the one re-composition", where alarmToBeLaunched turns != null;
                 * this seems (at my current knowledge) the "best" way to have data from viewmodel
                 * ready to use (e.g. to get light alarm colors from yet to launch alarm, to use
                 * it in DigitalClockScreen's alarm mode (without to use 2 viewmodels in
                 * DigitalClockScreen, instead passing it via param below))
                 */
                if (alarmToBeLaunched != null) {
                    DigitalClockScreen(
                        fullScreen = true,
                        alarmMode = true,
                        alarmToBeLaunched = alarmToBeLaunched,
                        onClick = {} //TODO navigate to AlarmReactionScreen => try to write new AlarmNavHost to navigate between DigitalClockScreen and AlarmReactionScreen
                        //TODO: AlarmReactionScreen => SNOOZE(create and schedule a new alarm in now() + snoozetime (=>to be intoduced)
                        //TODO: | DISMISS(back to DigitalClockScreen) | STOP (finish activity)
                    )
                }

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun handleAlarmToBeLaunched(viewModel: AlarmSettingsViewModel = hiltViewModel()): Alarm? {
    val alarmSettings = viewModel.alarmSettingsFlow.collectAsState(
        initial = AlarmSettings()
    ).value
    var alarmToBeLaunched: Alarm? by remember {
        mutableStateOf(null)
    }

    /**
     * Modify alarms is only possible when flow collection is completed (which is not the case
     * directly after 1st composition of AlarmActivity), and then, when it's completed,
     * alarmSettings.alarms.size turns to a value > 0 (guaranteed => this AlarmActivity is
     * going to be created only when an alarm is present in alarmSettings.alarms) and finally,
     * run next block only once in the background, currentlyLaunchedAlarm will be handled and
     * returned to e.g. use lightAlarmColors in DigitalClockScreen's alarm mode.
     */
    if (alarmSettings.alarms.size != 0 && alarmToBeLaunched == null) {
        LaunchedEffect(Unit) {

            // Can't be null => alarmSettings.alarms.size != 0 => following call with !! is save
            alarmToBeLaunched = alarmSettings.alarms.minByOrNull { it.start }
            val oldAlarm = alarmToBeLaunched!!

            when (oldAlarm.repetition) {

                // No repetition => remove, it's not needed anymore
                Repetition.NONE -> {
                    viewModel.removeAlarm(alarmSettings, oldAlarm)
                }

                // Daily => remove current, add and schedule new repetition one day later
                Repetition.DAILY -> {
                    viewModel.updateAlarm(
                        alarmSettings,
                        oldAlarm,
                        updatedAlarm = oldAlarm.copy(start = oldAlarm.start.plus(1.days))
                    )
                }

                // Weekly => remove current, add and schedule new repetition one week(7d) later
                Repetition.WEEKLY -> {
                    viewModel.updateAlarm(
                        alarmSettings,
                        oldAlarm,
                        updatedAlarm = oldAlarm.copy(start = oldAlarm.start.plus(7.days))
                    )
                }

                // Monthly => remove current, add and schedule new repetition one month later
                Repetition.MONTHLY -> {
                    viewModel.updateAlarm(
                        alarmSettings,
                        oldAlarm,
                        updatedAlarm = oldAlarm.copy(start = oldAlarm.start.plusMonths(1L))
                    )
                }
            }
        }
    }
    return alarmToBeLaunched
}

