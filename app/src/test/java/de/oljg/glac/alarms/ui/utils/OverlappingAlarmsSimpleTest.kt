package de.oljg.glac.alarms.ui.utils

import com.google.common.truth.Truth.assertThat
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.ALARM_START_BUFFER
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.data.utils.AlarmDefaults.DEFAULT_LIGHT_ALARM_DURATION
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.minutes

/**
 * Unit under test: [interferesScheduledAlarms]
 *
 * Note: Sketches in comments are not to scale!
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
         * ASB  => ALARM_START_BUFFER = 5m
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
                lightAlarmDuration = DEFAULT_LIGHT_ALARM_DURATION)
        )
    }

    @Test
    fun `(T0) Requested alarm does overlap same existing alarm`() {
        val sameAlarmTimeAsA = aStart
        assertThat(
            sameAlarmTimeAsA.interferesScheduledAlarms(
                lightAlarmDuration = DEFAULT_LIGHT_ALARM_DURATION, testAlarms))
            .isTrue()
    }

    @Test
    fun `(T1-positive-left) Requested alarm does not overlap existing alarm`() {
        val justAboutValidAlarmTime = aStart
            .minus(DEFAULT_LIGHT_ALARM_DURATION)
            .minus(ALARM_START_BUFFER)
        assertThat(
            justAboutValidAlarmTime.interferesScheduledAlarms(
                lightAlarmDuration = DEFAULT_LIGHT_ALARM_DURATION, testAlarms))
            .isFalse()
    }

    @Test
    fun `(T1-negative-left) Requested alarm does overlap existing alarm`() {
        val overlappingAlarmTime = aStart
            .minus(DEFAULT_LIGHT_ALARM_DURATION)
            .minus(ALARM_START_BUFFER)
            .plus(1.minutes) // trigger overlap
        assertThat(
            overlappingAlarmTime.interferesScheduledAlarms(
                lightAlarmDuration = DEFAULT_LIGHT_ALARM_DURATION, testAlarms))
            .isTrue()
    }

    @Test
    fun `(T2-positive-right) Requested alarm does not overlap existing alarm`() {
        val justAboutValidAlarmTime = aStart
            .plus(DEFAULT_LIGHT_ALARM_DURATION)
            .plus(ALARM_START_BUFFER)
        assertThat(
            justAboutValidAlarmTime.interferesScheduledAlarms(
                lightAlarmDuration = DEFAULT_LIGHT_ALARM_DURATION, testAlarms))
            .isFalse()
    }

    @Test
    fun `(T2-negative-right) Requested alarm does overlap existing alarm`() {
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
