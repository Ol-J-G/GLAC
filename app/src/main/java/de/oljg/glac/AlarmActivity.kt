package de.oljg.glac

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import de.oljg.glac.alarms.ui.components.AlarmReactionDialog
import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.alarms.ui.utils.handleAlarmToBeLaunched
import de.oljg.glac.alarms.ui.utils.isSnoozeAlarmBeforeNextAlarm
import de.oljg.glac.alarms.ui.utils.plus
import de.oljg.glac.clock.digital.ui.DigitalClockScreen
import de.oljg.glac.clock.digital.ui.utils.findActivity
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.data.AlarmSettings
import de.oljg.glac.settings.alarms.ui.AlarmSettingsViewModel
import de.oljg.glac.ui.theme.GLACTheme
import java.time.LocalDateTime

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * This activity will be triggered by system's AlarmManager, actual compose entry point
         * is below ...
         */
        setContent {
            val viewModel: AlarmSettingsViewModel = hiltViewModel()
            val alarmSettings = viewModel.alarmSettingsFlow.collectAsState(
                initial = AlarmSettings()
            ).value
            var showAlarmReactionDialog by rememberSaveable {
                mutableStateOf(false)
            }
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
                        onClick = { showAlarmReactionDialog = true }
                    )
                }

                AnimatedVisibility(visible = showAlarmReactionDialog) {
                    val alarmActivity = LocalContext.current.findActivity()
                        ?: return@AnimatedVisibility // Should actually not happen!

                    val snoozeAlarmStart = LocalDateTime.now()
                        .plus(alarmToBeLaunched!!.snoozeDuration) // !! is save

                    AlarmReactionDialog(
                        snoozeEnabled = isSnoozeAlarmBeforeNextAlarm(
                            snoozeAlarmStart = snoozeAlarmStart,
                            scheduledAlarms = alarmSettings.alarms
                        ),
                        onSnoozeAlarm = {
                            viewModel.addAlarm(
                                alarmSettings,
                                Alarm(
                                    start = snoozeAlarmStart,
                                    isSnoozeAlarm = true,
                                    isLightAlarm = false,
                                    repetition = Repetition.NONE
                                )
                            )
                            alarmActivity.finish()
                        },
                        onDismiss = { showAlarmReactionDialog = false },
                        onStopAlarm = { alarmActivity.finish() }
                    )
                }
            }
        }
    }
}
