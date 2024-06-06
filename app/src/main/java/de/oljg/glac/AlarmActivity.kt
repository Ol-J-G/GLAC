package de.oljg.glac

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.AndroidEntryPoint
import de.oljg.glac.alarms.ui.components.AlarmReactionDialog
import de.oljg.glac.alarms.ui.utils.handleAlarmToBeLaunched
import de.oljg.glac.clock.digital.ui.DigitalClockScreen
import de.oljg.glac.clock.digital.ui.utils.findActivity
import de.oljg.glac.ui.theme.GLACTheme

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
                    AlarmReactionDialog(
                        onSnoozeAlarm = { /* TODO */ },
                        onDismiss = { showAlarmReactionDialog = false },
                        onStopAlarm = { alarmActivity?.finish() }
                    )
                }
            }
        }
    }
}
