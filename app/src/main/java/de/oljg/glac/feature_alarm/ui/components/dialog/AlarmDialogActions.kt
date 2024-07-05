package de.oljg.glac.feature_alarm.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults

@Composable
fun AlarmDialogActions(
    scheduleOrUpdateEnabled: Boolean,
    isUpdateAlarmAction: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(
            SettingsDefaults.COLOR_PICKER_BUTTON_SPACE, Alignment.End
        )
    ) {
        TextButton(onClick = onDismiss) {
            Text(text = stringResource(R.string.dismiss).uppercase())
        }
        TextButton(
            onClick = {
                onConfirm()
                onDismiss()
            },
            enabled = scheduleOrUpdateEnabled
        ) {
            Text(
                modifier = Modifier.padding(end = SettingsDefaults.EDGE_PADDING),
                text = when {
                    isUpdateAlarmAction -> stringResource(R.string.update).uppercase()
                    else -> stringResource(R.string.schedule).uppercase()
                }
            )
        }
    }
}

