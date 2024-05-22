package de.oljg.glac.alarms.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.ALARM_START_BUFFER
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.MAX_LIGHT_ALARM_DURATION
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.MIN_LIGHT_ALARM_DURATION
import de.oljg.glac.alarms.ui.utils.isInFuture
import de.oljg.glac.alarms.ui.utils.isValidAlarmStart
import de.oljg.glac.alarms.ui.utils.plus
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.data.AlarmSettings
import de.oljg.glac.settings.alarms.ui.AlarmSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.common.SettingsSwitch
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.EDGE_PADDING
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import kotlin.time.Duration.Companion.ZERO

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

    var isLightAlarm by remember {
        mutableStateOf(alarmSettings.isLightAlarm)
    }
    var lightAlarmDuration by remember {
        mutableStateOf(alarmSettings.lightAlarmDuration)
    }
    var isValidLightAlarmDuration by remember {
        mutableStateOf(true) // default value is valid
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Calendar.getInstance().timeInMillis,
        yearRange = LocalDateTime.now().year..LocalDateTime.now().year
    )
    var showDatePicker by remember { mutableStateOf(false) }

    val doubleBuffer = ALARM_START_BUFFER + ALARM_START_BUFFER / 2 //TODO: remove after testing
    val initialtime = LocalTime.now().plus(doubleBuffer).plus(MAX_LIGHT_ALARM_DURATION)
    val timePickerState = rememberTimePickerState(
        initialHour = initialtime.hour,
        initialMinute = initialtime.minute
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

    var isReadyToScheduleAlarm by remember {
        mutableStateOf(false)
    }
    fun checkIfReadyToScheduleAlarm() = isValidAlarmStart(
        date = selectedDate,
        time = selectedTime,
        lightAlarmDuration = if (isLightAlarm) lightAlarmDuration else ZERO
    ) && if (isLightAlarm) isValidLightAlarmDuration else true


    fun timeIsNotInFuture() = selectedDate != null && selectedTime != null
            && !LocalDateTime.of(
        selectedDate, selectedTime
    ).isInFuture(
        lightAlarmDuration = if (isLightAlarm) lightAlarmDuration else ZERO
    )


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
                        .padding(DIALOG_DEFAULT_PADDING),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.start_date))
                    TextButton(onClick = { showDatePicker = true }) {
                        Text(text = selectedDate?.let { dateFormatter.format(it) }
                            ?: stringResource(R.string.select_date)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DIALOG_DEFAULT_PADDING),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.start_time))
                    TextButton(onClick = { showTimePicker = true }) {
                        Text(text = selectedTime?.let { timeFormatter.format(it) }
                            ?: stringResource(R.string.select_time)
                        )
                    }
                }
                //TODO: maybe add hint: Earliest time you can choose: formatted localdatetime + consider lightalarmtime
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))

                SettingsSwitch(
                    label = stringResource(R.string.light_alarm),
                    edgePadding = DIALOG_DEFAULT_PADDING,
                    checked = isLightAlarm,
                    onCheckedChange = {
                        isLightAlarm = !isLightAlarm
                        if (!isLightAlarm)
                            lightAlarmDuration = ZERO else alarmSettings.lightAlarmDuration
                        /**
                         * Actually, it's impossible to enter an invalid duration value into
                         * light alarm duration TF below, so, when hiding it in case an invalid
                         * duration value is entered, the last valid (persisted) value will be
                         * displayed when show TF again... => it's valid, when hidden or unhidden
                         */
                        isValidLightAlarmDuration = true
                        isReadyToScheduleAlarm = checkIfReadyToScheduleAlarm()
                    }
                )

                AnimatedVisibility(visible = isLightAlarm) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(DIALOG_DEFAULT_PADDING),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MinutesDurationSelector(
                            label = stringResource(R.string.light_alarm_duration),
                            duration = lightAlarmDuration,
                            minDuration = MIN_LIGHT_ALARM_DURATION,
                            maxDuration = MAX_LIGHT_ALARM_DURATION,
                            onValueChanged = { isValidDuration ->
                                isValidLightAlarmDuration = isValidDuration
                                isReadyToScheduleAlarm = checkIfReadyToScheduleAlarm()
                            },
                            onDurationChanged = { newLightAlarmDuration ->
                                lightAlarmDuration = newLightAlarmDuration
                            }
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
                        enabled = isReadyToScheduleAlarm
                    ) {
                        Text(
                            modifier = Modifier.padding(end = EDGE_PADDING),
                            text = stringResource(R.string.schedule).uppercase()
                        )
                    }
                }

                AnimatedVisibility(visible = timeIsNotInFuture()) {
                    DialogErrorMessage(
                        message = stringResource(R.string.alarm_time_must_be_in_the_future)
                    )
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DEFAULT_VERTICAL_SPACE / 2)
                )
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
                        isReadyToScheduleAlarm = checkIfReadyToScheduleAlarm()
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
                        isReadyToScheduleAlarm = checkIfReadyToScheduleAlarm()
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
