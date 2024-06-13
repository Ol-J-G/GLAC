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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.ALARM_REACTION_DIALOG_BUTTON_WEIGHT
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.ALARM_REACTION_DIALOG_DISMISS_BUTTON_WEIGHT
import de.oljg.glac.core.ui.components.GlacDialog
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING

@Composable
fun CancelSnoozeAlarmDialog(
    onCancelSnoozeAlarm: () -> Unit,
    onDismiss: () -> Unit,
    onCloseFullscreenClock: () -> Unit
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
        when(LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_PORTRAIT ->
                PortraitLayout(onCancelSnoozeAlarm, onDismiss, onCloseFullscreenClock)
            else -> LandscapeLayout(onCancelSnoozeAlarm, onDismiss, onCloseFullscreenClock)
        }
    }
}

@Composable
private fun PortraitLayout(
    onCancelSnoozeAlarm: () -> Unit,
    onDismiss: () -> Unit,
    onCloseFullscreenClock: () -> Unit
) {
    Column(modifier = Modifier.fillMaxHeight()) {
        AlarmReactionDialogButton( // CANCEL SNOOZE
            modifier = Modifier
                .padding(DIALOG_DEFAULT_PADDING)
                .fillMaxWidth()
                .weight(ALARM_REACTION_DIALOG_BUTTON_WEIGHT),
            label = stringResource(R.string.cancel_snooze).uppercase(),
            onClick = onCancelSnoozeAlarm
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
        AlarmReactionDialogButton( // CLOSE FULLSCREEN CLOCK
            modifier = Modifier
                .padding(DIALOG_DEFAULT_PADDING)
                .fillMaxWidth()
                .weight(ALARM_REACTION_DIALOG_BUTTON_WEIGHT),
            label = stringResource(R.string.close_clock).uppercase(),
            onClick = onCloseFullscreenClock
        )
    }
}


@Composable
private fun LandscapeLayout(
    onCancelSnoozeAlarm: () -> Unit,
    onDismiss: () -> Unit,
    onCloseFullscreenClock: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AlarmReactionDialogButton( // CANCEL SNOOZE
            modifier = Modifier
                .padding(DIALOG_DEFAULT_PADDING)
                .fillMaxHeight()
                .weight(ALARM_REACTION_DIALOG_BUTTON_WEIGHT),
            label = stringResource(R.string.cancel_snooze).uppercase(),
            onClick = onCancelSnoozeAlarm
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
        AlarmReactionDialogButton( // CLOSE FULLSCREEN CLOCK
            modifier = Modifier
                .padding(DIALOG_DEFAULT_PADDING)
                .fillMaxHeight()
                .weight(ALARM_REACTION_DIALOG_BUTTON_WEIGHT),
            label = stringResource(R.string.close_clock).uppercase(),
            onClick = onCloseFullscreenClock
        )
    }
}
