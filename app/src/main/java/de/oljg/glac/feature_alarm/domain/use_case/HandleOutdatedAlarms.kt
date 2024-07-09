package de.oljg.glac.feature_alarm.domain.use_case

import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository
import de.oljg.glac.feature_alarm.domain.utils.scaleToMinutes
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import de.oljg.glac.feature_alarm.ui.utils.minus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


/**
 * In case a user doesn't use GLAC for some time, but have some alarms scheduled, these alarms
 * might be outdated. This class remove outdated alarms with Repetition.NONE and updates
 * alarm.start for all alarm with repetition != NONE (DAILY, WEEKLY, MONTHLY) accordingly.
 *
 * Note that "now" is the time when GLAC app will be started, and this use case MUST be executed
 * before re-scheduling all alarms! Furthermore,
 * [de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.ALARM_START_BUFFER] will not be taken into
 * account here (only when adding new alarms)...
 *
 * @see [de.oljg.glac.feature_alarm.ui.HandleOutdatedAlarmsTest]
 */
class HandleOutdatedAlarms(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute() {
        val alarms = repository.getAlarms()

        /**
         * Next alarm will go off when starting the app in the same minute as the next
         * alarm's start.minute.
         * This will be most probably a rare case ...
         */
        val now = LocalDateTime.now().scaleToMinutes()
        val nowTime = now.toLocalTime()
        val nowDate = now.toLocalDate()

        alarms.forEach { alarm ->
            val actualAlarmStart = if (alarm.isLightAlarm)
                alarm.start.minus(alarm.lightAlarmDuration) else alarm.start

            val alarmTime = alarm.start.toLocalTime()

            if (actualAlarmStart.isBefore(now)) {
                when (alarm.repetition) {
                    Repetition.NONE -> repository.removeAlarm(alarm)
                    Repetition.DAILY -> handleDaily(alarm, alarmTime, nowTime, nowDate)
                    Repetition.WEEKLY -> handleWeekly(alarm, alarmTime, now, nowTime, nowDate)
                    Repetition.MONTHLY -> handleMonthly(alarm, alarmTime, now, nowTime, nowDate)
                }
            }
        }
    }


    private suspend fun handleDaily(
        alarm: Alarm,
        alarmTime: LocalTime,
        nowTime: LocalTime,
        nowDate: LocalDate
    ) {
        updateOutdatedAlarm(alarm, alarmTime, nowTime, nowDate, Repetition.DAILY)
    }


    private suspend fun handleWeekly(
        alarm: Alarm,
        alarmTime: LocalTime,
        now: LocalDateTime,
        nowTime: LocalTime,
        nowDate: LocalDate
    ) {
        val nowDayOfWeek = now.dayOfWeek
        val alarmStartDayOfWeek = alarm.start.dayOfWeek

        if (nowDayOfWeek == alarmStartDayOfWeek) {
            updateOutdatedAlarm(alarm, alarmTime, nowTime, nowDate, Repetition.WEEKLY)
        } else { // Different day of week

            // nextAlarmStartDate must have same dayOfWeek as outdated alarm
            var nextAlarmStartDate = nowDate.plusDays(1L)
            while (nextAlarmStartDate.dayOfWeek != alarmStartDayOfWeek) {
                nextAlarmStartDate = nextAlarmStartDate.plusDays(1L)
            }
            repository.updateAlarm(
                alarm, alarm.copy(
                    start = LocalDateTime.of(nextAlarmStartDate, alarmTime)
                )
            )
        }
    }


    private suspend fun handleMonthly(
        alarm: Alarm,
        alarmTime: LocalTime,
        now: LocalDateTime,
        nowTime: LocalTime,
        nowDate: LocalDate
    ) {
        val nowDayOfMonth = now.dayOfMonth
        val alarmStartDayOfMonth = alarm.start.dayOfMonth

        if (nowDayOfMonth == alarmStartDayOfMonth) {
            updateOutdatedAlarm(alarm, alarmTime, nowTime, nowDate, Repetition.MONTHLY)
        } else { // Different day of month
            // TODO_LATER: There must be a way to do this better / more elegant ...!?
            // nextAlarmStartDate must have same dayOfMonth as outdated alarm
            var nextAlarmStartDate = nowDate.plusDays(1L)
            while (nextAlarmStartDate.dayOfMonth != alarmStartDayOfMonth) {
                nextAlarmStartDate = nextAlarmStartDate.plusDays(1L)
            }
            repository.updateAlarm(
                alarm, alarm.copy(
                    start = LocalDateTime.of(nextAlarmStartDate, alarmTime)
                )
            )
        }
    }


    private suspend fun updateOutdatedAlarm(
        alarm: Alarm,
        alarmTime: LocalTime,
        nowTime: LocalTime,
        nowDate: LocalDate,
        repetition: Repetition
    ) {
        repository.updateAlarm(
            alarm, alarm.copy(
                start = LocalDateTime.of( // LocalDate, LocalTime
                    when {
                        alarmTime.isAfter(nowTime) || alarmTime == nowTime ->
                            nowDate // today

                        else -> when (repetition) {

                            // Not possible today (alarmTime is in the past!) => tomorrow
                            Repetition.DAILY -> nowDate.plusDays(1L)

                            // Not possible today (alarmTime is in the past!) => next week
                            Repetition.WEEKLY -> nowDate.plusWeeks(1L)

                            // Not possible today (alarmTime is in the past!) => next month
                            Repetition.MONTHLY -> nowDate.plusMonths(1L)

                            else -> throw IllegalStateException()
                        }
                    },
                    alarmTime
                )
            )
        )
    }
}
