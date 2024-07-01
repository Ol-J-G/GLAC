package de.oljg.glac.feature_alarm.domain.use_case

data class AlarmUseCases(
    val getAlarmSettingsFlow: GetAlarmSettingsFlow,
    val updateAlarmDefaultsSectionIsExpanded: UpdateAlarmDefaultsSectionIsExpanded,
    val updateIsLightAlarm: UpdateIsLightAlarm,
    val updateLightAlarmDuration: UpdateLightAlarmDuration,
    val updateSnoozeDuration: UpdateSnoozeDuration,
    val updateRepetition: UpdateRepetition,
    val updateAlarmSoundUri: UpdateAlarmSoundUri,
    val updateAlarmSoundFadeDuration: UpdateAlarmSoundFadeDuration,
    val removeAlarm: RemoveAlarm,
    val addAlarm: AddAlarm,
    val updateAlarm: UpdateAlarm,
    val reScheduleAllAlarms: ReScheduleAllAlarms,
    val getAlarms: GetAlarms,
    val removeImportedAlarmSoundFile: RemoveImportedAlarmSoundFile = RemoveImportedAlarmSoundFile()
)
