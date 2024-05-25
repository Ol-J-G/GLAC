package de.oljg.glac.alarms.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.ALARM_START_BUFFER
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.MAX_LIGHT_ALARM_DURATION
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.MIN_LIGHT_ALARM_DURATION
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.minutesSaver
import de.oljg.glac.alarms.ui.utils.isInFuture
import de.oljg.glac.alarms.ui.utils.isValidAlarmStart
import de.oljg.glac.alarms.ui.utils.plus
import de.oljg.glac.clock.digital.ui.utils.ScreenDetails
import de.oljg.glac.clock.digital.ui.utils.screenDetails
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.data.AlarmSettings
import de.oljg.glac.settings.alarms.ui.AlarmSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.common.SettingsSwitch
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_BORDER_WIDTH
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_TONAL_ELEVATION
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.EDGE_PADDING
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.minutes

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmDialog(
    viewModel: AlarmSettingsViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit,
    onNewAlarmAdded: (Alarm) -> Unit
) {
    val scrollState = rememberScrollState()
    val alarmSettings = viewModel.alarmSettingsFlow.collectAsState(
        initial = AlarmSettings()
    ).value

    var isLightAlarm by rememberSaveable {
        mutableStateOf(alarmSettings.isLightAlarm)
    }

    var lightAlarmDuration: Duration by rememberSaveable(stateSaver = minutesSaver) {
        mutableStateOf(alarmSettings.lightAlarmDuration)
    }
    var isValidLightAlarmDuration by rememberSaveable {
        mutableStateOf(true) // default value is valid
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Calendar.getInstance().timeInMillis,
        yearRange = LocalDateTime.now().year..LocalDateTime.now().year,

        /** In case of screenHeightType Compact, users must manually change to [DisplayMode.Input]*/
        initialDisplayMode = DisplayMode.Picker
    )
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState()
    var showTimePicker by rememberSaveable { mutableStateOf(false) }


    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
    var selectedDate: LocalDate? by rememberSaveable {
        mutableStateOf(null)
    }

    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    var selectedTime: LocalTime? by rememberSaveable {
        mutableStateOf(null)
    }
    val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    var isReadyToScheduleAlarm by rememberSaveable {
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


    // Nice to have as tiny hint for users
    fun earliestPossibleAlarmTime() = dateTimeFormatter.format(
        LocalDateTime.now()
            .plus(ALARM_START_BUFFER)
            .plus(if (isLightAlarm) lightAlarmDuration else ZERO)
            .plus(1.minutes) // make current minute exclusive
    )


    Dialog(onDismissRequest = onDismissRequest) { //TODO: extract SettingsDialog comp => dialog + surface + @Composable content
        Surface(
            shape = SettingsDefaults.DIALOG_SHAPE,
            tonalElevation = DIALOG_TONAL_ELEVATION,
            modifier = Modifier
                .clip(SettingsDefaults.DIALOG_SHAPE)
                .background(color = MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
        ) {
            Column {

                // Scrollable inner section
                Column(
                    modifier = Modifier
                        .weight(10f, fill = false)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = DIALOG_DEFAULT_PADDING)
                            .padding(top = DEFAULT_VERTICAL_SPACE)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.start_date))
                        TextButton(onClick = { showDatePicker = true }) {
                            Text(text = selectedDate?.let { dateFormatter.format(it) }
                                ?: stringResource(R.string.select).uppercase()
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = DIALOG_DEFAULT_PADDING)
                            .padding(bottom = DEFAULT_VERTICAL_SPACE)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.start_time))
                        TextButton(onClick = { showTimePicker = true }) {
                            Text(text = selectedTime?.let { timeFormatter.format(it) }
                                ?: stringResource(R.string.select).uppercase()
                            )
                        }
                    }
                    Divider(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(.9f)
                        .padding(vertical = DEFAULT_VERTICAL_SPACE)

                    )
                    //TODO: add "repeat mode" selector (dropdown?..!) AlarmRepeatMode, NONE, DAILY, WEEKLY, MONTHLY?

                    //TODO: use settings section comp. to hide too much (rather unimportant) alarm detail settings
                    SettingsSwitch(
                        label = stringResource(R.string.light_alarm),
                        edgePadding = DIALOG_DEFAULT_PADDING,
                        checked = isLightAlarm,
                        onCheckedChange = {
                            isLightAlarm = !isLightAlarm

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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MinutesDurationSelector(
                                modifier = Modifier
                                    .padding(DIALOG_DEFAULT_PADDING)
                                    .fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))
                    }
                }

                // Dismiss/Schedule section
                Column(modifier = Modifier.weight(4f, fill = false)) {
                    Divider(modifier = Modifier
                        .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.outline,
                        thickness = DIALOG_BORDER_WIDTH
                    )
                    Crossfade(targetState = timeIsNotInFuture(), label = "") { timeIsNotInFuture ->
                        when(timeIsNotInFuture) {
                            true -> DialogMessage(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = DEFAULT_VERTICAL_SPACE / 2)
                                    .weight(1f, fill = false),
                                isErrorMessage = true,
                                message = stringResource(R.string.alarm_time_must_be_in_the_future)
                            )

                            false -> DialogMessage(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = DEFAULT_VERTICAL_SPACE / 2)
                                    .weight(1f, fill = false),
                                message = stringResource(R.string.earliest_possible_alarm_time)
                                        + earliestPossibleAlarmTime()
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(3f, fill = false)
                            .padding(vertical = DEFAULT_VERTICAL_SPACE / 2),
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

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                    )
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
            DatePicker(
                state = datePickerState,

                // Disable all days of the past, excluding today
                dateValidator = { millis ->
                    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        .isAfter(LocalDate.now().minusDays(1L))
                }
            )
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            displayMode = when(screenDetails().screenHeightType) {
                ScreenDetails.DisplayType.Compact -> DisplayMode.Input
                else -> DisplayMode.Picker
            },
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
            },
            picker = {
                TimePicker(state = timePickerState)
            },
            input = {
                TimeInput(state = timePickerState)
            }
        )
    }
}
