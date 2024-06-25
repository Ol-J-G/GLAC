package de.oljg.glac.core.alarms.domain.use_case

import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.domain.repository.AlarmSettingsRepository
import de.oljg.glac.core.alarms.manager.AlarmScheduler

class AddAlarm(
    private val repository: AlarmSettingsRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend fun execute(alarm: Alarm) {
        repository.addAlarm(alarm)
        alarmScheduler.schedule(alarm)
    }
}
