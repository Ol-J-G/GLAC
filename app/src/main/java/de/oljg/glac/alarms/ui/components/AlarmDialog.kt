package de.oljg.glac.alarms.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.MAX_LIGHT_ALARM_DURATION
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.MIN_LIGHT_ALARM_DURATION
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.minutesSaver
import de.oljg.glac.alarms.ui.utils.isSet
import de.oljg.glac.alarms.ui.utils.toEpochMillis
import de.oljg.glac.clock.digital.ui.utils.ScreenDetails
import de.oljg.glac.clock.digital.ui.utils.screenDetails
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.data.AlarmSettings
import de.oljg.glac.core.ui.components.SettingsDialog
import de.oljg.glac.core.ui.components.SettingsSection
import de.oljg.glac.settings.alarms.ui.AlarmSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.common.SettingsSwitch
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDialog(
    viewModel: AlarmSettingsViewModel = hiltViewModel(),
    alarmToUpdate: Alarm? = null,
    onDismissRequest: () -> Unit,
    onAlarmUpdated: (Alarm) -> Unit,
    onNewAlarmAdded: (Alarm) -> Unit
) {
    val scrollState = rememberScrollState()
    val alarmSettings = viewModel.alarmSettingsFlow.collectAsState(
        initial = AlarmSettings()
    ).value

    var moreAlarmDetailsIsExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var isLightAlarm by rememberSaveable {
        mutableStateOf(alarmToUpdate?.isLightAlarm ?: alarmSettings.isLightAlarm)
    }
    var lightAlarmDuration: Duration by rememberSaveable(stateSaver = minutesSaver) {
        mutableStateOf(alarmToUpdate?.lightAlarmDuration ?: alarmSettings.lightAlarmDuration)
    }
    fun lightAlarmDuration() = if (isLightAlarm) lightAlarmDuration else ZERO
    var isValidLightAlarmDuration by rememberSaveable {
        mutableStateOf(true) // default value is valid
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = alarmToUpdate?.start?.toEpochMillis()
            ?: LocalDateTime.now().toEpochMillis(),
        yearRange = LocalDateTime.now().year..LocalDateTime.now().year + 1,

        /** In case of screenHeightType Compact, users must manually change to [DisplayMode.Input]*/
        initialDisplayMode = DisplayMode.Picker
    )
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = alarmToUpdate?.start?.toLocalTime()?.hour ?: 0,
        initialMinute = alarmToUpdate?.start?.toLocalTime()?.minute ?: 0,
    )
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    var selectedDate: LocalDate? by rememberSaveable {
        mutableStateOf(alarmToUpdate?.start?.toLocalDate())
    }
    var selectedTime: LocalTime? by rememberSaveable {
        mutableStateOf(alarmToUpdate?.start?.toLocalTime())
    }

    fun checkIfReadyToScheduleAlarm() = de.oljg.glac.alarms.ui.utils.checkIfReadyToScheduleAlarm(
        selectedDate, selectedTime, lightAlarmDuration(), alarmSettings.alarms, alarmToUpdate,
        isLightAlarm, isValidLightAlarmDuration || alarmToUpdate.isSet()
    )
    var isReadyToScheduleAlarm by rememberSaveable {
        mutableStateOf(checkIfReadyToScheduleAlarm())
    }

    fun buildAlarm() = Alarm(
        start = LocalDateTime.of(selectedDate, selectedTime),
        isLightAlarm = isLightAlarm,
        lightAlarmDuration = lightAlarmDuration
    )

    SettingsDialog(onDismissRequest = onDismissRequest) { //TODO: care about adaptive design => row+2col for expanded screen width class...
        Column {

            // Scrollable inner section
            Column(
                modifier = Modifier
                    .weight(10f, fill = false)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top
            ) {
                MomentSelector(
                    label = stringResource(R.string.start_date),
                    dateMoment = selectedDate
                ) {
                    showDatePicker = true
                }
                MomentSelector(
                    label = stringResource(R.string.start_time),
                    timeMoment = selectedTime
                ) {
                    showTimePicker = true
                }
                Divider(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(.9f)
                        .padding(vertical = DEFAULT_VERTICAL_SPACE)

                )
                //TODO: add "repeat mode" selector (dropdown?..!) AlarmRepeatMode, NONE, DAILY, WEEKLY, MONTHLY?

                SettingsSection(
                    sectionTitle = stringResource(R.string.more_alarm_details),
                    sectionTitleStyle = MaterialTheme.typography.titleMedium,
                    expanded = moreAlarmDetailsIsExpanded,
                    horizontalPadding = DIALOG_DEFAULT_PADDING,
                    backgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
                    expandedBackgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
                    onExpandedChange = { moreAlarmDetailsIsExpanded = it }
                ) {
                    SettingsSwitch(
                        label = stringResource(R.string.light_alarm),
                        edgePadding = DIALOG_DEFAULT_PADDING / 2,
                        checked = isLightAlarm,
                        onCheckedChange = {
                            isLightAlarm = !isLightAlarm

                            /**
                             * Actually, it's impossible to enter an invalid duration value into
                             * light alarm duration TF below, so, when hiding it in case an invalid
                             * duration value is entered, the last valid (persisted) value will be
                             * displayed when show TF again... => it's valid, doesn't matter if
                             * it's hidden or unhidden.
                             */
                            isValidLightAlarmDuration = true
                            isReadyToScheduleAlarm = checkIfReadyToScheduleAlarm()
                        }
                    )

                    AnimatedVisibility(visible = isLightAlarm) {
                        MinutesDurationSelector(
                            modifier = Modifier
                                .padding(DIALOG_DEFAULT_PADDING / 2)
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
                }
            }
            Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))

            // Fixed bottom section
            Column(modifier = Modifier.weight(4f, fill = false)) {
                AlarmDialogInfoSection( // Hint/Status
                    date = selectedDate,
                    time = selectedTime,
                    lightAlarmDuration = lightAlarmDuration(),
                    scheduledAlarms = alarmSettings.alarms,
                    alarmToUpdate = alarmToUpdate
                )
                AlarmDialogActions( // Dismiss/Schedule/Update
                    enabled = isReadyToScheduleAlarm,
                    isUpdateAlarmAction = alarmToUpdate.isSet(),
                    onDissmiss = onDismissRequest
                ) {
                    when {
                        alarmToUpdate.isSet() -> onAlarmUpdated(buildAlarm())
                        else -> onNewAlarmAdded(buildAlarm())
                    }
                }
            }
        }
    }

    AnimatedVisibility(visible = showDatePicker) {
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

    AnimatedVisibility(visible = showTimePicker) {
        TimePickerDialog(
            displayMode = when (screenDetails().screenHeightType) {
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
            picker = { TimePicker(state = timePickerState) },
            input = { TimeInput(state = timePickerState) }
        )
    }
}