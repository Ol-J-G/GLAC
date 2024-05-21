package de.oljg.glac.alarms.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.Defaults.MAX_LIGHT_ALARM_DURATION
import de.oljg.glac.alarms.ui.utils.Defaults.MIN_LIGHT_ALARM_DURATION
import de.oljg.glac.alarms.ui.utils.isInFuture
import de.oljg.glac.alarms.ui.utils.isInt
import de.oljg.glac.alarms.ui.utils.isIntIn
import de.oljg.glac.alarms.ui.utils.isValidAlarmStart
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.data.AlarmSettings
import de.oljg.glac.settings.alarms.ui.AlarmSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.common.SettingsSwitch
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmDialog(
    viewModel: AlarmSettingsViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit,
    onNewAlarmAdded: (Alarm) -> Unit
) {
    val alarmSettings = viewModel.alarmSettingsFlow.collectAsState(
        initial = AlarmSettings()
    ).value

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Calendar.getInstance().timeInMillis,
        yearRange = LocalDateTime.now().year..LocalDateTime.now().year
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

    var isValidAlarmTime by remember {
        mutableStateOf(false)
    }

    var isLightAlarm by remember {
        mutableStateOf(alarmSettings.isLightAlarm)
    }

    var lightAlarmDuration by remember {
        mutableStateOf(alarmSettings.lightAlarmDuration)
    }
    var lightAlarmDurationValue by remember {
        mutableStateOf(lightAlarmDuration.toInt(unit = DurationUnit.MINUTES).toString())
    }
    var isValidLightAlarmDuration by remember {
        mutableStateOf(true)
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
                        Text(selectedDate?.let { dateFormatter.format(it) } ?: "Select Date")
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
                        Text(selectedTime?.let { timeFormatter.format(it) } ?: "Select Time")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))

                SettingsSwitch(
                    label = "Light Alarm",
                    checked = isLightAlarm,
                    onCheckedChange = {
                        isLightAlarm = !isLightAlarm
                    }
                )

                //TODO: animatedVis, after below extraction
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SettingsDefaults.DIALOG_DEFAULT_PADDING),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField( //TODO: extract component => will be needed more than once
                        modifier = Modifier.fillMaxWidth(),
                        value = lightAlarmDurationValue,
                        label = { Text(text = "Light Alarm Duration [Minutes]") },
                        onValueChange = { newTextValue ->
                            lightAlarmDurationValue = newTextValue
                            isValidLightAlarmDuration = lightAlarmDurationValue.isIntIn(
                                range = MIN_LIGHT_ALARM_DURATION.toInt(DurationUnit.MINUTES)
                                        ..MAX_LIGHT_ALARM_DURATION.toInt(DurationUnit.MINUTES)
                            )

                            if (isValidLightAlarmDuration)
                                lightAlarmDuration = lightAlarmDurationValue.toInt().minutes
                        },
                        singleLine = true,
//                    textStyle = MaterialTheme.typography.titleMedium,
                        supportingText = {
                            if (!isValidLightAlarmDuration)
                                Text(
                                    text = when {
                                        lightAlarmDurationValue.isBlank() -> "Please Enter a Number!"
                                        !lightAlarmDurationValue.isInt() -> "Please Enter a Whole Number!"
                                        lightAlarmDurationValue.toInt() !in
                                                MIN_LIGHT_ALARM_DURATION.toInt(DurationUnit.MINUTES)
                                                ..MAX_LIGHT_ALARM_DURATION.toInt(DurationUnit.MINUTES) ->
                                            "Valid Amounts: $MIN_LIGHT_ALARM_DURATION - $MAX_LIGHT_ALARM_DURATION"

                                        else -> ""
                                    }, color = MaterialTheme.colorScheme.error
                                )
                        },
                        trailingIcon = {
                            if (!isValidLightAlarmDuration)
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.error
                                )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )

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
                    TextButton(
                        onClick = {
                            onNewAlarmAdded(
                                Alarm(
                                    start = LocalDateTime.of(selectedDate, selectedTime),
                                    isLightAlarm = isLightAlarm,
                                    lightAlarmDuration = lightAlarmDuration
                                )
                            )
                            onDismissRequest.invoke()
                        },
                        enabled = isValidAlarmTime && isValidLightAlarmDuration
                    ) {
                        Text(text = stringResource(R.string.schedule).uppercase())
                    }
                }

                AnimatedVisibility(
                    visible =
                    selectedDate != null
                            && selectedTime != null
                            && !LocalDateTime.of(
                        selectedDate, selectedTime
                    ).isInFuture(
                        lightAlarmDuration = if (isLightAlarm) lightAlarmDuration else Duration.ZERO
                    )
                ) {
                    DialogErrorMessage(message = "Alarm Time" + " " + "must be in the future!")
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
                        selectedDate = datePickerState.selectedDateMillis?.let { millis ->
                            Instant
                                .ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        isValidAlarmTime = isValidAlarmStart(
                            selectedDate,
                            selectedTime,
                            if (isLightAlarm) lightAlarmDuration else Duration.ZERO
                        )
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
                        isValidAlarmTime = isValidAlarmStart(
                            selectedDate,
                            selectedTime,
                            if (isLightAlarm) lightAlarmDuration else Duration.ZERO
                        )
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
