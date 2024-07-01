package de.oljg.glac.feature_alarm.domain.use_case

import de.oljg.glac.core.util.removeLocalFile

class RemoveImportedAlarmSoundFile {
    fun execute(importedAlarmSoundFileUriStringToRemove: String) {
        removeLocalFile(importedAlarmSoundFileUriStringToRemove)
    }
}
