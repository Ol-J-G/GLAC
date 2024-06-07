package de.oljg.glac.alarms.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.MAX_SNOOZE_DURATION
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.MIN_LIGHT_ALARM_DURATION
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.MIN_SNOOZE_DURATION
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.minutesSaver
import de.oljg.glac.alarms.ui.utils.Repetition
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
    alarmToBeUpdated: Alarm? = null,
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

    var selectedRepetition by rememberSaveable {
        mutableStateOf(Repetition.NONE)
    }

    var isLightAlarm by rememberSaveable {
        mutableStateOf(alarmToBeUpdated?.isLightAlarm ?: alarmSettings.isLightAlarm)
    }
    var lightAlarmDuration: Duration by rememberSaveable(stateSaver = minutesSaver) {
        mutableStateOf(alarmToBeUpdated?.lightAlarmDuration ?: alarmSettings.lightAlarmDuration)
    }
    fun lightAlarmDuration() = if (isLightAlarm) lightAlarmDuration else ZERO
    var isValidLightAlarmDuration by rememberSaveable {
        mutableStateOf(true) // default value is valid
    }

    var snoozeDuration: Duration by rememberSaveable(stateSaver = minutesSaver) {
        mutableStateOf(alarmToBeUpdated?.snoozeDuration ?: alarmSettings.snoozeDuration)
    }
    var isValidSnoozeDuration by rememberSaveable {
        mutableStateOf(true) // default value is valid
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = alarmToBeUpdated?.start?.toEpochMillis()
            ?: LocalDateTime.now().toEpochMillis(),
        yearRange = LocalDateTime.now().year..LocalDateTime.now().year + 1,

        /** In case of screenHeightType Compact, users must manually change to [DisplayMode.Input]*/
        initialDisplayMode = DisplayMode.Picker
    )
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = alarmToBeUpdated?.start?.toLocalTime()?.hour ?: 0,
        initialMinute = alarmToBeUpdated?.start?.toLocalTime()?.minute ?: 0,
    )
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    var selectedDate: LocalDate? by rememberSaveable {
        mutableStateOf(alarmToBeUpdated?.start?.toLocalDate())
    }
    var selectedTime: LocalTime? by rememberSaveable {
        mutableStateOf(alarmToBeUpdated?.start?.toLocalTime())
    }

    // Just a shortcut to improve readability a tiny bit^^ (there must be a better solution oO)
    fun checkIfReadyToScheduleAlarm() = de.oljg.glac.alarms.ui.utils.checkIfReadyToScheduleAlarm(
        selectedDate, selectedTime, lightAlarmDuration(), alarmSettings.alarms, alarmToBeUpdated,
        isLightAlarm, isValidLightAlarmDuration, isValidSnoozeDuration
    )

    var isReadyToScheduleAlarm by rememberSaveable {
        mutableStateOf(checkIfReadyToScheduleAlarm())
    }

    fun buildAlarm() = Alarm(
        start = LocalDateTime.of(selectedDate, selectedTime),
        isLightAlarm = isLightAlarm,
        lightAlarmDuration = lightAlarmDuration,
        repetition = selectedRepetition,
        snoozeDuration = snoozeDuration
    )

    SettingsDialog(onDismissRequest = onDismissRequest) { //TODO: care about adaptive design => row+2col for expanded screen width class... => when dialog is completed
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
                RepetitionSelector(
                    label = stringResource(R.string.repetition),
                    selectedRepetition = selectedRepetition,
                    onNewRepeatModeSelected = { newRepeatMode ->
                        selectedRepetition = Repetition.valueOf(newRepeatMode)
                    }
                )

                Divider(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(.9f)
                        .padding(bottom = DEFAULT_VERTICAL_SPACE)

                )

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
                    MinutesDurationSelector(
                        modifier = Modifier
                            .padding(DIALOG_DEFAULT_PADDING / 2)
                            .fillMaxWidth(),
                        label = stringResource(R.string.snooze_duration),
                        duration = snoozeDuration,
                        minDuration = MIN_SNOOZE_DURATION,
                        maxDuration = MAX_SNOOZE_DURATION,
                        onValueChanged = { isValidDuration ->
                            isValidSnoozeDuration = isValidDuration
                            isReadyToScheduleAlarm = checkIfReadyToScheduleAlarm()
                        },
                        onDurationChanged = { newSnoozeDuration ->
                            snoozeDuration = newSnoozeDuration
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE))

            // Fixed bottom section
            Column(modifier = Modifier.weight(4f, fill = false)) {
                AlarmDialogInfoSection( // Hint/Status
                    date = selectedDate,
                    time = selectedTime,
                    lightAlarmDuration = lightAlarmDuration(),
                    isValidLightAlarmDuration = isValidLightAlarmDuration,
                    isValidSnoozeDuration = isValidSnoozeDuration,
                    scheduledAlarms = alarmSettings.alarms,
                    alarmToBeUpdated = alarmToBeUpdated
                )
                AlarmDialogActions( // Dismiss/Schedule/Update
                    enabled = isReadyToScheduleAlarm,
                    isUpdateAlarmAction = alarmToBeUpdated.isSet(),
                    onDissmiss = onDismissRequest
                ) {
                    when {
                        alarmToBeUpdated.isSet() -> onAlarmUpdated(buildAlarm())
                        else -> onNewAlarmAdded(buildAlarm())
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showDatePicker,
        enter = fadeIn(TweenSpec(durationMillis = 100)),
        exit = fadeOut(TweenSpec(durationMillis = 100))
    ) {
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

                // Disable days in the past, excluding today
                dateValidator = { millis ->
                    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        .isAfter(LocalDate.now().minusDays(1L))
                }
            )
        }
    }

    AnimatedVisibility(
        visible = showTimePicker,
        enter = fadeIn(TweenSpec(durationMillis = 100)),
        exit = fadeOut(TweenSpec(durationMillis = 100))
    ) {
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
