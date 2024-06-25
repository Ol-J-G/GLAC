package de.oljg.glac.feature_alarm.domain.repository

import android.net.Uri
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.domain.model.AlarmSettings
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface AlarmSettingsRepository {
    fun getAlarmSettingsFlow(): Flow<AlarmSettings>

    suspend fun updateAlarmDefaultsSectionIsExpanded(newValue: Boolean)

    suspend fun updateIsLightAlarm(newValue: Boolean)

    suspend fun updateLightAlarmDuration(newValue: Duration)

    suspend fun updateSnoozeDuration(newValue: Duration)

    suspend fun updateRepetition(newValue: Repetition)

    suspend fun updateAlarmSoundUri(newValue: Uri)

    suspend fun updateAlarmSoundFadeDuration(newValue: Duration)

    suspend fun removeAlarm(alarm: Alarm)

    suspend fun addAlarm(alarm: Alarm)

    suspend fun updateAlarm(alarmtoBeUpdated: Alarm, updatedAlarm: Alarm)

    suspend fun getAlarms(): List<Alarm>
}
