package de.oljg.glac.feature_alarm.domain.manager

import de.oljg.glac.feature_alarm.domain.model.Alarm

interface AlarmScheduler {
    fun schedule(alarm: Alarm)
    fun cancel(alarm: Alarm)
}
