package de.oljg.glac.feature_alarm.domain.manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.ui.utils.minus
import de.oljg.glac.feature_alarm.ui.utils.toMillis

/**
 * AlarmManager log:
 * > adb shell dumpsys alarm > dump.txt
 * (> grep packagename(unique part) dump.txt)
 */
class AndroidAlarmScheduler(
    private val context: Context
): AlarmScheduler {
    private val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(alarm: Alarm): Boolean {
        try {
            val actualAlarmStart = if(alarm.isLightAlarm)
                alarm.start.minus(alarm.lightAlarmDuration) else alarm.start

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                actualAlarmStart.toMillis(),
                PendingIntent.getBroadcast(
                    context,
                    alarm.hashCode(),
                    Intent(context, AlarmReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun cancel(alarm: Alarm): Boolean {
        try {
            alarmManager.cancel(
                PendingIntent.getBroadcast(
                    context,
                    alarm.hashCode(),
                    Intent(context, AlarmReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }
}
