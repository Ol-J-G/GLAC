package de.oljg.glac.feature_alarm.ui.utils

import com.google.common.truth.Truth.assertThat
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.domain.model.utils.AlarmDefaults.DEFAULT_LIGHT_ALARM_DURATION
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.ALARM_START_BUFFER
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.minutes

/**
 * Originally/Actually an unit test, but after the project has grown, e.g. Uri class became
 * part of Alarm, and it's not possible use Uri in unit tests, unless adding mock frameworks
 * and mock it. But this is too much effort for too little benefit imho. So, relocate this test
 * to instrumented test is the easy solution, also since test execution time doesn't matter (for
 * me in this project)
 *
 * Unit under test: [interferesScheduledAlarms]
 *
 * Note: Sketches in comments are not to scale!
 *
 * ASB = [de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.ALARM_START_BUFFER]
 */
class OverlappingAlarmsSimpleTest {
    private lateinit var testAlarms: List<Alarm>
    private lateinit var aStart: LocalDateTime

    @Before
    fun init() {
        /**
         * AS   => Alarm Start
         * LAS  => Light Alarm Start
         * RAS  => Requested Alarm Start
         * RLAS => Requested Light Alarm Start
         * ASB  => ALARM_START_BUFFER = 2m
         * DLAD => DEFAULT_LIGHT_ALARM_DURATION = 30m
         *
         *               4:25 4:30       5:00 5:05
         *           _________A-LAS______A-AS________
         *                     |          |
         *                     |<  DLAD  >|<
         *    |<  DLAD  >|<ASB>|          |<ASB>|<  DLAD  >|
         *    |          |<1m>              <1m>|          |
         *  T1-RLAS    T1-RAS                 T2-RLAS    T2-RAS
         */
        /**      LocalDateTime.of(YEAR, MONTH(1..12), DAY_OF_MONTH(1..31), HOUR, MINUTE) */
        aStart = LocalDateTime.of(2024, 1, 10, 5, 0)
        testAlarms = listOf(
            Alarm( // A
                start = aStart,
                isLightAlarm = true,
                lightAlarmDuration = DEFAULT_LIGHT_ALARM_DURATION
            )
        )
    }

    // (T0) Requested alarm does overlap same existing alarm
    @Test
    fun t0() {
        val sameAlarmTimeAsA = aStart
        assertThat(
            sameAlarmTimeAsA.interferesScheduledAlarms(
                lightAlarmDuration = DEFAULT_LIGHT_ALARM_DURATION, testAlarms))
            .isTrue()
    }

    // (T1-positive-left) Requested alarm does not overlap existing alarm
    @Test
    fun t1_positive_left() {
        val justAboutValidAlarmTime = aStart
            .minus(DEFAULT_LIGHT_ALARM_DURATION)
            .minus(ALARM_START_BUFFER)
        assertThat(
            justAboutValidAlarmTime.interferesScheduledAlarms(
                lightAlarmDuration = DEFAULT_LIGHT_ALARM_DURATION, testAlarms))
            .isFalse()
    }

    // (T1-negative-left) Requested alarm does overlap existing alarm
    @Test
    fun t1_negative_left() {
        val overlappingAlarmTime = aStart
            .minus(DEFAULT_LIGHT_ALARM_DURATION)
            .minus(ALARM_START_BUFFER)
            .plus(1.minutes) // trigger overlap
        assertThat(
            overlappingAlarmTime.interferesScheduledAlarms(
                lightAlarmDuration = DEFAULT_LIGHT_ALARM_DURATION, testAlarms))
            .isTrue()
    }

    // (T2-positive-right) Requested alarm does not overlap existing alarm
    @Test
    fun t2_positive_right() {
        val justAboutValidAlarmTime = aStart
            .plus(DEFAULT_LIGHT_ALARM_DURATION)
            .plus(ALARM_START_BUFFER)
        assertThat(
            justAboutValidAlarmTime.interferesScheduledAlarms(
                lightAlarmDuration = DEFAULT_LIGHT_ALARM_DURATION, testAlarms))
            .isFalse()
    }

    // (T2-negative-right) Requested alarm does overlap existing alarm
    @Test
    fun t2_negative_right() {
        val overlappingAlarmTime = aStart
            .plus(DEFAULT_LIGHT_ALARM_DURATION)
            .plus(ALARM_START_BUFFER)
            .minus(1.minutes) // trigger overlap
        assertThat(
            overlappingAlarmTime.interferesScheduledAlarms(
                lightAlarmDuration = DEFAULT_LIGHT_ALARM_DURATION, testAlarms))
            .isTrue()
    }
}
