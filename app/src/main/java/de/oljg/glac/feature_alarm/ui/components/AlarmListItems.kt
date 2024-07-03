package de.oljg.glac.feature_alarm.ui.components

import androidx.compose.runtime.Composable
import de.oljg.glac.feature_alarm.domain.model.Alarm
import java.time.LocalDateTime

@Composable
fun AlarmListItems(
    alarms: List<Alarm>,
    selectedAlarmStart: LocalDateTime?,
    onClick: (Alarm) -> Unit,
    onRemoveAlarm: (Alarm) -> Unit,
    onUpdateAlarm: (Alarm) -> Unit
) {
    alarms.forEach { alarm ->
        AlarmListItem(
            alarmStart = alarm.start,
            isLightAlarm = alarm.isLightAlarm,
            lightAlarmDuration = alarm.lightAlarmDuration,
            repetition = alarm.repetition,
            snoozeDuration = alarm.snoozeDuration,
            alarmSoundUri = alarm.alarmSoundUri,
            selected = alarm.start == selectedAlarmStart,
            onClick = { onClick(alarm) },
            onRemoveAlarm = { onRemoveAlarm(alarm) },
            onUpdateAlarm = { onUpdateAlarm(alarm) }
        )
    }
}
