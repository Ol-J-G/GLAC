package de.oljg.glac.feature_alarm.ui.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.oljg.glac.R
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.TIME_PICKER_DIALOG_ACTIONS_HEIGHT
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.TIME_PICKER_DIALOG_PADDING
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.TIME_PICKER_DIALOG_TITLE_BOTTOM_PADDING
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.TIME_PICKER_DIALOG_TONAL_ELEVATION

/**
 * Source:
 * https://medium.com/@droidvikas/exploring-date-and-time-pickers-compose-bytes-120e75349797
 *
 * Slightly extended to use a DisplayMode and TimeInput to be able to respect compact screen
 * size height. (TimePicker is distorted and ugly in this edge case!)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String = stringResource(id = R.string.select_time),
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    displayMode: DisplayMode = DisplayMode.Picker,
    picker: @Composable () -> Unit,
    input: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = TIME_PICKER_DIALOG_TONAL_ELEVATION,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = containerColor
                ),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.padding(TIME_PICKER_DIALOG_PADDING),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = TIME_PICKER_DIALOG_TITLE_BOTTOM_PADDING),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                when(displayMode) {
                    DisplayMode.Picker -> picker()
                    else -> input()
                }
                Row(
                    modifier = Modifier
                        .height(TIME_PICKER_DIALOG_ACTIONS_HEIGHT)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}
