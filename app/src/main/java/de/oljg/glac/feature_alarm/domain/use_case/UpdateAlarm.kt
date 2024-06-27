package de.oljg.glac.feature_alarm.domain.use_case

import de.oljg.glac.feature_alarm.domain.manager.AlarmScheduler
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository

class UpdateAlarm(
    private val repository: AlarmSettingsRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend fun execute(alarmtoBeUpdated: Alarm, updatedAlarm: Alarm): Boolean {

        // 1st, cancel the alarm to be updated
        val canceled = alarmScheduler.cancel(alarmtoBeUpdated)

        // 2nd, remove alarm to be updated and add updated alarm
        repository.updateAlarm(alarmtoBeUpdated, updatedAlarm)

        // 3rd, schedule updated alarm
        val scheduled = alarmScheduler.schedule(updatedAlarm)

        // Finally, return true when update was succesful
        return canceled && scheduled
    }
}
