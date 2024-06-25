package de.oljg.glac

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.oljg.glac.core.util.findActivity
import de.oljg.glac.core.util.resetScreenBrightness
import de.oljg.glac.core.util.unlockScreenOrientation
import de.oljg.glac.feature_alarm.domain.media.AlarmSoundPlayer
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.ui.AlarmSettingsEvent
import de.oljg.glac.feature_alarm.ui.AlarmSettingsViewModel
import de.oljg.glac.feature_alarm.ui.components.AlarmReactionDialog
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import de.oljg.glac.feature_alarm.ui.utils.handleAlarmToBeLaunched
import de.oljg.glac.feature_alarm.ui.utils.plus
import de.oljg.glac.feature_clock.ui.clock.DigitalAlarmClockScreen
import de.oljg.glac.ui.theme.GLACTheme
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {
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

            val context = LocalContext.current
            val alarmActivity = context.findActivity()
                ?: return@setContent // Should actually not happen!

            val alarmSoundPlayer by remember {
                mutableStateOf(AlarmSoundPlayer(context))
            }

            val audioManager by remember {
                mutableStateOf(context.getSystemService(AUDIO_SERVICE) as AudioManager)
            }

            var showAlarmReactionDialog by rememberSaveable {
                mutableStateOf(false)
            }

            /**
             * Rememeber user's volume setting (volume will be reset to this value after alarm is
             * stopped/snoozed).
             */
            val streamAlarmVolumeSetByUser by rememberSaveable {
                mutableIntStateOf(audioManager.getStreamVolume(AudioManager.STREAM_ALARM))
            }

            fun handleAlarmStop() {
                unlockScreenOrientation(alarmActivity)
                resetScreenBrightness(alarmActivity)
                alarmSoundPlayer.stop()

                // Reset volume
                audioManager.setStreamVolume(
                    AudioManager.STREAM_ALARM, streamAlarmVolumeSetByUser, 0
                )

                // "Back" to FullScreenClock via starting main activity
                lifecycleScope.launch {
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
                alarmActivity.finish()
            }

            val alarmToBeLaunched = handleAlarmToBeLaunched(alarmSettings, viewModel::onEvent)

            GLACTheme {
                if (alarmToBeLaunched != null) {
                    DigitalAlarmClockScreen(
                        fullScreen = true,
                        alarmMode = true,
                        alarmToBeLaunched = alarmToBeLaunched,
                        alarmSoundPlayer = alarmSoundPlayer,
                        alarmSoundFadeDuration = alarmSettings.alarmSoundFadeDuration,
                        onClick = { showAlarmReactionDialog = true }
                    )
                }

                AnimatedVisibility(visible = showAlarmReactionDialog) {
                    AlarmReactionDialog(
                        alarmToBeLaunched = alarmToBeLaunched!!,
                        scheduledAlarms = alarmSettings.alarms,
                        onSnoozeAlarm = {
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
                            viewModel.onEvent(
                                AlarmSettingsEvent.AddAlarm(
                                    Alarm(
                                        start = LocalDateTime.now().plus(
                                            alarmToBeLaunched.snoozeDuration
                                        ),
                                        alarmSoundUri = alarmToBeLaunched.alarmSoundUri,
                                        isSnoozeAlarm = true,
                                        snoozeDuration = alarmToBeLaunched.snoozeDuration,
                                        isLightAlarm = false,
                                        repetition = Repetition.NONE
                                    )
                                )
                            )
                            handleAlarmStop()
                        },
                        onDismiss = { showAlarmReactionDialog = false },
                        onStopAlarm = { handleAlarmStop() }
                    )
                }
            }
        }
    }
}
