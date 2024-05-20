package de.oljg.glac.alarms.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import de.oljg.glac.R
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmDialog(
    onDismissRequest: () -> Unit,
    onNewAlarmAdded: (Alarm) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Calendar.getInstance().timeInMillis
    )
    var showDatePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute
    )
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
    var selectedDate: LocalDate? by remember {
        mutableStateOf(null)
    }

    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    var selectedTime: LocalTime? by remember {
        mutableStateOf(null)
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .border(
                    SettingsDefaults.DIALOG_BORDER_WIDTH,
                    MaterialTheme.colorScheme.onTertiaryContainer
                )
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()

                        .padding(SettingsDefaults.DIALOG_DEFAULT_PADDING),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Start Date")
                    TextButton(onClick = { showDatePicker = true }) {
                        Text(
                            if (selectedDate != null)
                                dateFormatter.format(selectedDate) else "Select Date"
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()

                        .padding(SettingsDefaults.DIALOG_DEFAULT_PADDING),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Start Time")
                    TextButton(onClick = { showTimePicker = true }) {
                        Text(
                            if (selectedTime != null)
                                timeFormatter.format(selectedTime) else "Select Time"
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        SettingsDefaults.COLOR_PICKER_BUTTON_SPACE, Alignment.End
                    )
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = stringResource(R.string.dismiss).uppercase())
                    }
                    TextButton(onClick = {
                        //TODO: check date > now && time > now, etc.
                        onNewAlarmAdded(
                            Alarm(
                                start = LocalDateTime.of(selectedDate, selectedTime)
                            )
                        )
                        onDismissRequest.invoke()
                    }) {
                        Text(text = stringResource(R.string.confirm).uppercase()) //TODO: rename to "schedule"
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDate = Instant
                            .ofEpochMilli(datePickerState.selectedDateMillis!!)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        showDatePicker = false
                    }
                ) { Text(text = stringResource(R.string.confirm).uppercase()) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = stringResource(R.string.dismiss).uppercase())
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) { Text(text = stringResource(R.string.confirm).uppercase()) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(text = stringResource(R.string.dismiss).uppercase())
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}
