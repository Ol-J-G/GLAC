package de.oljg.glac.core.alarms.media.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioAttributes.USAGE_ALARM
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import de.oljg.glac.core.alarms.media.utils.AlarmSoundDefaults.ALARM_SOUND_URI_KEY

class AlarmSoundService : Service() {

    /**
     * In this case it's imho better to use nullable MediaPlayer.
     * As lateinit var, MediaPlayer.create will crash this service, when
     * the sound file behind alarmSoundUri is unplayable/corrupt (noticed this by chance
     * during manual testing)
     */
    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        val alarmSoundUriString = intent?.getStringExtra(ALARM_SOUND_URI_KEY)
        /**
         * alarmSoundUriString should be not null, it must be passed in
         * [de.oljg.glac.core.alarms.media.AlarmSoundPlayer.play], so, Uri.parse() should be OK.
         */
        val alarmSoundUri = Uri.parse(alarmSoundUriString)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(USAGE_ALARM) // use "alarm volume channel"
            .build()

        mediaPlayer = MediaPlayer.create(
            this, // context
            alarmSoundUri,
            null, // no holder necessary
            audioAttributes,
            audioManager.generateAudioSessionId()
        )

        mediaPlayer?.isLooping = true // Alarm sounds must be looped
        mediaPlayer?.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}
