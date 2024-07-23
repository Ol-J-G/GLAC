package de.oljg.glac.feature_alarm.ui.components.dialog

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.GlacDialog
import de.oljg.glac.core.util.ScreenDetails
import de.oljg.glac.core.util.screenDetails
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.domain.model.AlarmSettings
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.minutesSaver
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.uriSaver
import de.oljg.glac.feature_alarm.ui.utils.PresentSelectableDates
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import de.oljg.glac.feature_alarm.ui.utils.toEpochMillisUTC
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDialog(
    alarmSettings: AlarmSettings,
    alarmToBeUpdated: Alarm? = null,
    onDismissRequest: () -> Unit,
    onAlarmUpdated: (Alarm) -> Unit,
    onNewAlarmAdded: (Alarm) -> Unit
) {
    var moreAlarmDetailsIsExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedRepetition by rememberSaveable {
        mutableStateOf(alarmToBeUpdated?.repetition ?: alarmSettings.repetition)
    }

    var selectedIsLightAlarm by rememberSaveable {
        mutableStateOf(alarmToBeUpdated?.isLightAlarm ?: alarmSettings.isLightAlarm)
    }
    var selectedLightAlarmDuration: Duration by rememberSaveable(stateSaver = minutesSaver) {
        mutableStateOf(alarmToBeUpdated?.lightAlarmDuration
            ?: alarmSettings.lightAlarmDuration)
    }

    fun lightAlarmDuration() = if (selectedIsLightAlarm) selectedLightAlarmDuration else ZERO
    var isValidLightAlarmDuration by rememberSaveable {
        mutableStateOf(true) // default value is valid
    }

    var selectedSnoozeDuration: Duration by rememberSaveable(stateSaver = minutesSaver) {
        mutableStateOf(alarmToBeUpdated?.snoozeDuration ?: alarmSettings.snoozeDuration)
    }
    var isValidSnoozeDuration by rememberSaveable {
        mutableStateOf(true) // default value is valid
    }

    val datePickerState = rememberDatePickerState(
        /**
         * alarmToBeUpdated?.start is already persisted in local/user's time zone (see
         * DatePickerDialog...onClick), so, here it's necessary to use UTC to convert back
         * to millis to get the correct initial selected date.
         *
         * Example:
         * Let's assume a user's time zone offset would be +02:00 hours and the user has been
         * scheduled/add an alarm at 2024-02-02 01:00 AM.
         * When the user wants to update this alarm's date,
         * initialSelectedDateMillis(which is UTC!) would be
         * => 2024-02-01 11:00 PM in case of using toEpochMillis() => wrong initial date!
         * => 2024-02-02 01:00 AM in case of using toEpochMillisUTC() => OK
         */
        initialSelectedDateMillis = alarmToBeUpdated?.start?.toEpochMillisUTC()
            ?: LocalDateTime.now().toEpochMillisUTC(),
        yearRange = LocalDateTime.now().year..LocalDateTime.now().year + 1,

        /** In case of screenHeightType Compact, users must manually change to [DisplayMode.Input]*/
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = PresentSelectableDates
    )
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val initialTime = LocalTime.now()
    val timePickerState = rememberTimePickerState(
        initialHour = alarmToBeUpdated?.start?.toLocalTime()?.hour ?: initialTime.hour,
        initialMinute = alarmToBeUpdated?.start?.toLocalTime()?.minute ?: initialTime.minute,
    )
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    var selectedDate: LocalDate? by rememberSaveable {
        mutableStateOf(alarmToBeUpdated?.start?.toLocalDate())
    }
    var selectedTime: LocalTime? by rememberSaveable {
        mutableStateOf(alarmToBeUpdated?.start?.toLocalTime())
    }

    var selectedAlarmSoundUri: Uri by rememberSaveable(
        key = alarmToBeUpdated?.alarmSoundUri.toString(),
        stateSaver = uriSaver
    ) {
        mutableStateOf(alarmToBeUpdated?.alarmSoundUri ?: alarmSettings.alarmSoundUri)
    }

    // Just a shortcut to improve readability a tiny bit^^ (there must be a better solution ... oO)
    fun checkIfReadyToScheduleAlarm() =
            de.oljg.glac.feature_alarm.ui.utils.checkIfReadyToScheduleAlarm(
                selectedDate,
                selectedTime,
                lightAlarmDuration(),
                alarmSettings.alarms,
                alarmToBeUpdated,
                selectedIsLightAlarm,
                isValidLightAlarmDuration,
                isValidSnoozeDuration
            )

    var isReadyToScheduleAlarm by rememberSaveable {
        mutableStateOf(checkIfReadyToScheduleAlarm())
    }

    fun buildAlarm() = Alarm(
        start = LocalDateTime.of(selectedDate, selectedTime),
        isLightAlarm = selectedIsLightAlarm,
        lightAlarmDuration = selectedLightAlarmDuration,
        repetition = selectedRepetition,
        snoozeDuration = selectedSnoozeDuration,
        alarmSoundUri = selectedAlarmSoundUri
    )

    val screenDetails = screenDetails()
    val screenWidthType = screenDetails.screenWidthType
    val screenHeightType = screenDetails.screenHeightType

    // E.g. tablet landscape => Needed to let this dialog not looking too 'ugly' on tablets ...
    fun isWidthExpandedAndHeightMedium() = screenWidthType is ScreenDetails.DisplayType.Expanded
            && screenHeightType is ScreenDetails.DisplayType.Medium

    fun areTwoColumnsNeeded() = (screenWidthType is ScreenDetails.DisplayType.Medium
            && screenHeightType is ScreenDetails.DisplayType.Compact)
            || screenWidthType is ScreenDetails.DisplayType.Expanded


    GlacDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = when {
                isWidthExpandedAndHeightMedium() -> true
                areTwoColumnsNeeded() -> false
                else -> true
            }
        ),
        maxWidthFraction = when {
            isWidthExpandedAndHeightMedium() -> null
            areTwoColumnsNeeded() -> .9f
            else -> null
        },
        maxHeightFraction = when {
            isWidthExpandedAndHeightMedium() -> null
            areTwoColumnsNeeded() -> .9f
            else -> null
        }
    ) {
        Column {
            Column(
                modifier = Modifier
                    .weight(
                        weight = when {
                            isWidthExpandedAndHeightMedium() -> 1f
                            areTwoColumnsNeeded() -> 3f
                            else -> 6.5f
                        },
                        fill = true
                    )
            ) { // Scrollable sections inside
                when {
                    areTwoColumnsNeeded() -> AlarmDialogTwoColumnsLayout(
                        selectedDate = selectedDate,
                        selectedTime = selectedTime,
                        onSelectDateClicked = { showDatePicker = true },
                        onSelectTimeClicked = { showTimePicker = true },
                        selectedAlarmSoundUri = selectedAlarmSoundUri,
                        onNewAlarmSoundSelected = { newAlarmSound ->
                            selectedAlarmSoundUri = Uri.parse(newAlarmSound)
                        },
                        selectedRepetition = selectedRepetition,
                        onNewRepeatModeSelected = { newRepetition ->
                            selectedRepetition = Repetition.valueOf(newRepetition)
                        },
                        moreAlarmDetailsIsExpanded = moreAlarmDetailsIsExpanded,
                        onMoreAlarmDetailsExpandedChanged = { moreAlarmDetailsIsExpanded = it },
                        isLightAlarm = selectedIsLightAlarm,
                        onIsLightAlarmChanged = {
                            selectedIsLightAlarm = !selectedIsLightAlarm

                            /**
                             * Actually, it's impossible to enter an invalid duration value into
                             * light alarm duration TF, so, when hiding it in case an invalid
                             * duration value is entered, the last valid (persisted) value will be
                             * displayed when show TF again... => it's valid, doesn't matter if
                             * it's hidden or unhidden.
                             */
                            isValidLightAlarmDuration = true
                            isReadyToScheduleAlarm = checkIfReadyToScheduleAlarm()
                        },
                        lightAlarmDuration = selectedLightAlarmDuration,
                        onLightAlarmDurationValueChanged = { isValidDuration ->
                            isValidLightAlarmDuration = isValidDuration
                            isReadyToScheduleAlarm = checkIfReadyToScheduleAlarm()
                        },
                        onLightAlarmDurationChanged = { newLightAlarmDuration ->
                            selectedLightAlarmDuration = newLightAlarmDuration
                        },
                        snoozeDuration = selectedSnoozeDuration,
                        onSnoozeDurationValueChanged = { isValidDuration ->
                            isValidSnoozeDuration = isValidDuration
                            isReadyToScheduleAlarm = checkIfReadyToScheduleAlarm()
                        },
                        onSnoozeDurationChanged = { newSnoozeDuration ->
                            selectedSnoozeDuration = newSnoozeDuration
                        }
                    )

                    else -> AlarmDialogOneColumnLayout(
                        selectedDate = selectedDate,
                        selectedTime = selectedTime,
                        onSelectDateClicked = { showDatePicker = true },
                        onSelectTimeClicked = { showTimePicker = true },
                        selectedAlarmSoundUri = selectedAlarmSoundUri,
                        onNewAlarmSoundSelected = { newAlarmSound ->
                            selectedAlarmSoundUri = Uri.parse(newAlarmSound)
                        },
                        selectedRepetition = selectedRepetition,
                        onNewRepeatModeSelected = { newRepetition ->
                            selectedRepetition = Repetition.valueOf(newRepetition)
                        },
                        moreAlarmDetailsIsExpanded = moreAlarmDetailsIsExpanded,
                        onMoreAlarmDetailsExpandedChanged = { moreAlarmDetailsIsExpanded = it },
                        isLightAlarm = selectedIsLightAlarm,
                        onIsLightAlarmChanged = {
                            selectedIsLightAlarm = !selectedIsLightAlarm
                            isValidLightAlarmDuration = true
                            isReadyToScheduleAlarm = checkIfReadyToScheduleAlarm()
                        },
                        lightAlarmDuration = selectedLightAlarmDuration,
                        onLightAlarmDurationValueChanged = { isValidDuration ->
                            isValidLightAlarmDuration = isValidDuration
                            isReadyToScheduleAlarm = checkIfReadyToScheduleAlarm()
                        },
                        onLightAlarmDurationChanged = { newLightAlarmDuration ->
                            selectedLightAlarmDuration = newLightAlarmDuration
                        },
                        snoozeDuration = selectedSnoozeDuration,
                        onSnoozeDurationValueChanged = { isValidDuration ->
                            isValidSnoozeDuration = isValidDuration
                            isReadyToScheduleAlarm = checkIfReadyToScheduleAlarm()
                        },
                        onSnoozeDurationChanged = { newSnoozeDuration ->
                            selectedSnoozeDuration = newSnoozeDuration
                        }
                    )
                }
            }

            // Fixed bottom section
            Column(
                modifier = Modifier
                    .weight(
                        weight = when {
                            isWidthExpandedAndHeightMedium() -> 1f
                            areTwoColumnsNeeded() -> 1f
                            else -> 4f
                        },
                        fill = false
                    )
            ) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = ClockSettingsDefaults.DIALOG_BORDER_WIDTH,
                    color = MaterialTheme.colorScheme.outline
                )
                AlarmDialogBottomBar( // Hint/Status .. // Dismiss/Schedule/Update
                    selectedDate = selectedDate,
                    selectedTime = selectedTime,
                    lightAlarmDuration = lightAlarmDuration(),
                    scheduledAlarms = alarmSettings.alarms,
                    alarmToBeUpdated = alarmToBeUpdated,
                    isValidLightAlarmDuration = isValidLightAlarmDuration,
                    isValidSnoozeDuration = isValidSnoozeDuration,
                    isReadyToScheduleAlarm = isReadyToScheduleAlarm,
                    onDismiss = onDismissRequest,
                    onAlarmUpdated = { onAlarmUpdated(buildAlarm()) },
                    onNewAlarmAdded = { onNewAlarmAdded(buildAlarm()) }
                )
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
            DatePicker(state = datePickerState)
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
