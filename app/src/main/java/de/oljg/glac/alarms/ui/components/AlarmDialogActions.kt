package de.oljg.glac.alarms.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults

@Composable
fun ColumnScope.AlarmDialogActions(
    enabled: Boolean,
    isUpdateAlarmAction: Boolean,
    onDissmiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(3f, fill = false)
            .padding(vertical = SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            SettingsDefaults.COLOR_PICKER_BUTTON_SPACE, Alignment.End
        )
    ) {
        TextButton(onClick = onDissmiss) {
            Text(text = stringResource(R.string.dismiss).uppercase())
        }
        TextButton(
            onClick = {
                onConfirm()
                onDissmiss()
            },
            enabled = enabled
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
    Spacer(modifier = Modifier.weight(1f, fill = false))
}

