package de.oljg.glac.feature_alarm.domain.use_case

import android.net.Uri
import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository

class UpdateAlarmSoundUri(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute(uri: Uri) {
        repository.updateAlarmSoundUri(uri)
    }
}
