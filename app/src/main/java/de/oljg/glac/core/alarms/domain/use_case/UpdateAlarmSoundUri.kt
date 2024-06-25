package de.oljg.glac.core.alarms.domain.use_case

import android.net.Uri
import de.oljg.glac.core.alarms.domain.repository.AlarmSettingsRepository

class UpdateAlarmSoundUri(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute(uri: Uri) {
        repository.updateAlarmSoundUri(uri)
    }
}
