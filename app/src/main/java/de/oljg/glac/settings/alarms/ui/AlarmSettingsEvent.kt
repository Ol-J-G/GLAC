package de.oljg.glac.settings.alarms.ui

import android.net.Uri
import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.core.alarms.data.Alarm
import kotlin.time.Duration

sealed class AlarmSettingsEvent {
    data class UpdateAlarmDefaultsSectionIsExpanded(val expanded: Boolean): AlarmSettingsEvent()
    data class UpdateAlarmSoundUri(val uri: Uri): AlarmSettingsEvent()
    data class UpdateRepetition(val repetition: Repetition): AlarmSettingsEvent()
    data class UpdateIsLightAlarm(val isLightAlarm: Boolean): AlarmSettingsEvent()
    data class UpdateLightAlarmDuration(val duration: Duration): AlarmSettingsEvent()
    data class UpdateSnoozeDuration(val duration: Duration): AlarmSettingsEvent()
    data class UpdateAlarmSoundFadeDuration(val duration: Duration): AlarmSettingsEvent()
    data class AddAlarm(val alarm: Alarm): AlarmSettingsEvent()
    data class RemoveAlarm(val alarm: Alarm): AlarmSettingsEvent()
    data class UpdateAlarm(
        val alarmtoBeUpdated: Alarm,
        val updatedAlarm: Alarm
    ): AlarmSettingsEvent()
}
