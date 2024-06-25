package de.oljg.glac.feature_alarm.domain.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import de.oljg.glac.AlarmActivity

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmActivityIntent = Intent(context, AlarmActivity::class.java)
        alarmActivityIntent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(alarmActivityIntent)
    }
}

