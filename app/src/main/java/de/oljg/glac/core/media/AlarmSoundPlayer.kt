package de.oljg.glac.core.media

import android.content.Context
import android.content.Intent
import android.net.Uri
import de.oljg.glac.core.media.service.AlarmSoundService
import de.oljg.glac.feature_alarm.ui.utils.AlarmSoundDefaults.ALARM_SOUND_URI_KEY

class AlarmSoundPlayer(val context: Context) {
    private var intent: Intent = Intent(context, AlarmSoundService::class.java)

    fun play(uri: Uri) {
        /**
         * Putting an Uri as "ParcelableExtra" would require min API 33
         * => using String instead, and parsing it to Uri in
         * AlarmSoundService.onStartCommand
         */
        intent.putExtra(ALARM_SOUND_URI_KEY, uri.toString())
        context.startService(intent)
    }

    fun stop() {
        context.stopService(intent)
    }
}
