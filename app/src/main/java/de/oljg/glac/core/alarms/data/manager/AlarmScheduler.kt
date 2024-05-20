package de.oljg.glac.core.alarms.data.manager

import de.oljg.glac.core.alarms.data.Alarm

interface AlarmScheduler {
    fun schedule(alarm: Alarm)
    fun cancel(alarm: Alarm)
}
