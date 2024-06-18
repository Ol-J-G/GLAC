package de.oljg.glac.core.alarms.media.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioAttributes.USAGE_ALARM
import android.media.MediaPlayer
import android.net.Uri
import de.oljg.glac.core.alarms.media.utils.AlarmSoundDefaults.ALARM_SOUND_URI_KEY

class AlarmSoundService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmSoundUriString = intent?.getStringExtra(ALARM_SOUND_URI_KEY)

        /**
         * alarmSoundUriString should be not null, it must be passed in
         * [de.oljg.glac.core.alarms.media.AlarmSoundPlayer.play], so, Uri.parse() should be OK.
         */
        val alarmSoundUri = Uri.parse(alarmSoundUriString)

        mediaPlayer = MediaPlayer.create(this, alarmSoundUri)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(USAGE_ALARM)
            .build()
        mediaPlayer.setAudioAttributes(audioAttributes)
        mediaPlayer.isLooping = true // Alarm sounds must be looped
        mediaPlayer.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}
