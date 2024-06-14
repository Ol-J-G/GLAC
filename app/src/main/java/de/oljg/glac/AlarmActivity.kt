package de.oljg.glac

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import de.oljg.glac.clock.digital.ui.DigitalAlarmClockScreen
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.media.AlarmSoundPlayer
import de.oljg.glac.core.util.findActivity
import de.oljg.glac.core.util.resetScreenBrightness
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
            /**
             * Disable system's back button => user must react on alarm by snooze or stop!
             * (back button would lead to misunderstandings imho => is alarm snoozed or stopped,
             * when user clicks on back button?!?...)
             */
            BackHandler {}
            val viewModel: AlarmSettingsViewModel = hiltViewModel()
            val alarmSettings by viewModel.alarmSettingsStateFlow.collectAsState()

            var showAlarmReactionDialog by rememberSaveable {
                mutableStateOf(false)
            }

            val alarmToBeLaunched = handleAlarmToBeLaunched()

            GLACTheme {
                val alarmSoundPlayer = AlarmSoundPlayer(LocalContext.current)
                if (alarmToBeLaunched != null) {
                    DigitalAlarmClockScreen(
                        fullScreen = true,
                        alarmMode = true,
                        alarmToBeLaunched = alarmToBeLaunched,
                        alarmSoundPlayer = alarmSoundPlayer,
                        onClick = { showAlarmReactionDialog = true }
                    )
                }

                AnimatedVisibility(visible = showAlarmReactionDialog) {
                    val alarmActivity = LocalContext.current.findActivity()
                        ?: return@AnimatedVisibility // Should actually not happen!

                    val snoozeAlarmStart = LocalDateTime.now()
                        .plus(alarmToBeLaunched!!.snoozeDuration)

                    AlarmReactionDialog(
                        snoozeEnabled = isSnoozeAlarmBeforeNextAlarm(
                            snoozeAlarmStart = snoozeAlarmStart,
                            scheduledAlarms = alarmSettings.alarms
                        ),
                        onSnoozeAlarm = {
                            resetScreenBrightness(alarmActivity)
                            /**
                             * Schedule snooze alarm with settings "inherited" from the "original"
                             * alarm that has been snoozed by a user, but, snooze alarms:
                             * - must not be a light alarm!
                             * - must not have a repetition!
                             *
                             * Note that this way a snooze alarm can be snoozed theoretically
                             * unlimited times with inherited duration and alarm sound.
                             * (But everyone will wake up at some point, some sooner, some later,
                             * right? :>)
                             */
                            viewModel.addAlarm(
                                alarmSettings,
                                Alarm(
                                    start = snoozeAlarmStart,
                                    isSnoozeAlarm = true,
                                    snoozeDuration = alarmToBeLaunched.snoozeDuration,
                                    isLightAlarm = false,
                                    repetition = Repetition.NONE,
                                    alarmSoundUri = alarmToBeLaunched.alarmSoundUri
                                )
                            )
                            alarmSoundPlayer.stop()
                            alarmActivity.finish()
                        },
                        onDismiss = { showAlarmReactionDialog = false },
                        onStopAlarm = {
                            resetScreenBrightness(alarmActivity)
                            alarmSoundPlayer.stop()
                            alarmActivity.finish()
                        }
                    )
                }
            }
        }
    }
}
