package de.oljg.glac.feature_alarm.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.oljg.glac.core.util.ScreenDetails
import de.oljg.glac.core.util.screenDetails
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.ui.utils.isSet
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration

@Composable
fun AlarmDialogBottomBar(
    selectedDate: LocalDate?,
    selectedTime: LocalTime?,
    lightAlarmDuration: Duration,
    scheduledAlarms: List<Alarm>,
    alarmToBeUpdated: Alarm?,
    isValidLightAlarmDuration: Boolean,
    isValidSnoozeDuration: Boolean,
    isReadyToScheduleAlarm: Boolean,
    onDismiss: () -> Unit,
    onAlarmUpdated: () -> Unit,
    onNewAlarmAdded: () -> Unit
) {
    val screenDetails = screenDetails()
    val screenWidthType = screenDetails.screenWidthType
    val screenHeightType = screenDetails.screenHeightType

    fun areTwoColumnsNeeded() = (screenWidthType is ScreenDetails.DisplayType.Medium
            && screenHeightType is ScreenDetails.DisplayType.Compact)
            || screenWidthType is ScreenDetails.DisplayType.Expanded

    when {
        areTwoColumnsNeeded() -> { // TwoColumnsLayout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    AlarmDialogInfoSection( // Hint/Status
                        startDate = selectedDate,
                        startTime = selectedTime,
                        lightAlarmDuration = lightAlarmDuration,
                        isValidLightAlarmDuration = isValidLightAlarmDuration,
                        isValidSnoozeDuration = isValidSnoozeDuration,
                        scheduledAlarms = scheduledAlarms,
                        alarmToBeUpdated = alarmToBeUpdated
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    AlarmDialogActions( // Dismiss/Schedule/Update
                        scheduleOrUpdateEnabled = isReadyToScheduleAlarm,
                        isUpdateAlarmAction = alarmToBeUpdated.isSet(),
                        onDismiss = onDismiss
                    ) {
                        when {
                            alarmToBeUpdated.isSet() -> onAlarmUpdated()
                            else -> onNewAlarmAdded()
                        }
                    }
                }
            }
        }

        else -> { // OneColumnLayout
            AlarmDialogInfoSection( // Hint/Status
                startDate = selectedDate,
                startTime = selectedTime,
                lightAlarmDuration = lightAlarmDuration,
                isValidLightAlarmDuration = isValidLightAlarmDuration,
                isValidSnoozeDuration = isValidSnoozeDuration,
                scheduledAlarms = scheduledAlarms,
                alarmToBeUpdated = alarmToBeUpdated
            )
            AlarmDialogActions( // Dismiss/Schedule/Update
                scheduleOrUpdateEnabled = isReadyToScheduleAlarm,
                isUpdateAlarmAction = alarmToBeUpdated.isSet(),
                onDismiss = onDismiss
            ) {
                when {
                    alarmToBeUpdated.isSet() -> onAlarmUpdated()
                    else -> onNewAlarmAdded()
                }
            }
        }
    }
}
