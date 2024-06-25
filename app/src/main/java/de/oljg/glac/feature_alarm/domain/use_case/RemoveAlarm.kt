package de.oljg.glac.feature_alarm.domain.use_case

import de.oljg.glac.feature_alarm.domain.manager.AlarmScheduler
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository

class RemoveAlarm(
    private val repository: AlarmSettingsRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend fun execute(alarm: Alarm) {
        repository.removeAlarm(alarm)
        alarmScheduler.cancel(alarm)
    }
}
