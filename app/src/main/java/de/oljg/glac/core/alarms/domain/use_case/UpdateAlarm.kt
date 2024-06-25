package de.oljg.glac.core.alarms.domain.use_case

import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.domain.repository.AlarmSettingsRepository
import de.oljg.glac.core.alarms.manager.AlarmScheduler

class UpdateAlarm(
    private val repository: AlarmSettingsRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend fun execute(alarmtoBeUpdated: Alarm, updatedAlarm: Alarm) {
        alarmScheduler.cancel(alarmtoBeUpdated)
        repository.updateAlarm(alarmtoBeUpdated, updatedAlarm)
        alarmScheduler.schedule(updatedAlarm)
    }
}
