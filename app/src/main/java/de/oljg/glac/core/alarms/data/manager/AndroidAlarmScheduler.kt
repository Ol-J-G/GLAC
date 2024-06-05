package de.oljg.glac.core.alarms.data.manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import de.oljg.glac.alarms.ui.utils.minus
import de.oljg.glac.alarms.ui.utils.toMillis
import de.oljg.glac.core.alarms.data.Alarm

/**
 * AlarmManager log:
 * > adb shell dumpsys alarm > dump.txt
 * (> grep packagename(unique part) dump.txt)
 */
class AndroidAlarmScheduler(
    private val context: Context
): AlarmScheduler {
    private val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun schedule(alarm: Alarm) {
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
    }

    override fun cancel(alarm: Alarm) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarm.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
