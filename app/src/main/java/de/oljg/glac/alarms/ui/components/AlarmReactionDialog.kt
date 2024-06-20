package de.oljg.glac.alarms.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.ALARM_REACTION_DIALOG_BUTTON_WEIGHT
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.ALARM_REACTION_DIALOG_DISMISS_BUTTON_WEIGHT
import de.oljg.glac.alarms.ui.utils.isSnoozeAlarmBeforeNextAlarm
import de.oljg.glac.alarms.ui.utils.plus
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.ui.components.GlacDialog
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun AlarmReactionDialog(
    alarmToBeLaunched: Alarm,
    scheduledAlarms: List<Alarm>,
    onSnoozeAlarm: () -> Unit,
    onDismiss: () -> Unit,
    onStopAlarm: () -> Unit
) {
    GlacDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        backgroundColor = Color.Transparent // Let animated colors in the background shine through
    ) {
        var currentTime by remember {
            mutableStateOf(LocalTime.now())
        }
        var snoozeEnabled by rememberSaveable {
            mutableStateOf(true)
        }

        /**
         * Check every second, if it's possible to snooze alarm, if not, disable snooze button.
         */
        LaunchedEffect(currentTime) {
            snoozeEnabled = isSnoozeAlarmBeforeNextAlarm(
                snoozeAlarmStart = LocalDateTime.now().plus(alarmToBeLaunched.snoozeDuration),
                scheduledAlarms = scheduledAlarms
            )
            delay(1000L)
            currentTime = LocalTime.now()
        }
        when(LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_PORTRAIT ->
                PortraitLayout(snoozeEnabled, onSnoozeAlarm, onDismiss, onStopAlarm)
            else -> LandscapeLayout(snoozeEnabled, onSnoozeAlarm, onDismiss, onStopAlarm)
        }
    }
}

@Composable
private fun PortraitLayout(
    snoozeEnabled: Boolean,
    onSnoozeAlarm: () -> Unit,
    onDismiss: () -> Unit,
    onStopAlarm: () -> Unit
) {
    Column(modifier = Modifier.fillMaxHeight()) {
        AlarmReactionDialogButton( // SNOOZE
            modifier = Modifier
                .padding(DIALOG_DEFAULT_PADDING)
                .fillMaxWidth()
                .weight(ALARM_REACTION_DIALOG_BUTTON_WEIGHT),
            label = stringResource(R.string.snooze).uppercase(),
            enabled = snoozeEnabled,
            onClick = onSnoozeAlarm
        )
        AlarmReactionDialogButton( // DISMISS
            modifier = Modifier
                .padding(horizontal = DIALOG_DEFAULT_PADDING)
                .fillMaxWidth()
                .weight(ALARM_REACTION_DIALOG_DISMISS_BUTTON_WEIGHT),
            label = stringResource(R.string.dismiss).uppercase(),
            buttonColors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ),
            onClick = onDismiss
        )
        AlarmReactionDialogButton( // STOP
            modifier = Modifier
                .padding(DIALOG_DEFAULT_PADDING)
                .fillMaxWidth()
                .weight(ALARM_REACTION_DIALOG_BUTTON_WEIGHT),
            label = stringResource(R.string.stop).uppercase(),
            onClick = onStopAlarm
        )
    }
}


@Composable
private fun LandscapeLayout(
    snoozeEnabled: Boolean,
    onSnoozeAlarm: () -> Unit,
    onDismiss: () -> Unit,
    onStopAlarm: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AlarmReactionDialogButton( // SNOOZE
            modifier = Modifier
                .padding(DIALOG_DEFAULT_PADDING)
                .fillMaxHeight()
                .weight(ALARM_REACTION_DIALOG_BUTTON_WEIGHT),
            label = stringResource(R.string.snooze).uppercase(),
            enabled = snoozeEnabled,
            onClick = onSnoozeAlarm
        )
        AlarmReactionDialogButton( // DISMISS
            modifier = Modifier
                .padding(vertical = DIALOG_DEFAULT_PADDING)
                .fillMaxHeight()
                .weight(ALARM_REACTION_DIALOG_DISMISS_BUTTON_WEIGHT),
            label = stringResource(R.string.dismiss).uppercase(),
            buttonColors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ),
            onClick = onDismiss
        )
        AlarmReactionDialogButton( // STOP
            modifier = Modifier
                .padding(DIALOG_DEFAULT_PADDING)
                .fillMaxHeight()
                .weight(ALARM_REACTION_DIALOG_BUTTON_WEIGHT),
            label = stringResource(R.string.stop).uppercase(),
            onClick = onStopAlarm
        )
    }
}
