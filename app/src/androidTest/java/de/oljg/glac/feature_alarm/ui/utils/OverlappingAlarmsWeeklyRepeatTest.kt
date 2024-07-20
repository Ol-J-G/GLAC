package de.oljg.glac.feature_alarm.ui.utils

import com.google.common.truth.Truth.assertThat
import de.oljg.glac.feature_alarm.domain.model.Alarm
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.time.Duration
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
class OverlappingAlarmsWeeklyRepeatTest {
    private lateinit var testAlarms: List<Alarm>

    private fun startTime(
        year: Int = 2024,
        month: Int = 1,
        dayOfMonth: Int,
        hour: Int,
        minute: Int
    ) = LocalDateTime.of(year, month, dayOfMonth, hour, minute)

    @Before
    fun init() {
        /**
         * LAD => Light Alarm Duration
         *
         *   |<                               max 1 year                               >|
         *   |                                                                          |
         *   |<      30m     >|< 1w-30m  >|<     30m      >|< nw-LAD  >|<      30m     >|
         *
         *  10th            10th         17th            17th
         *  4:30            5:00         4:30            5:00
         *   |--------A0------|           |--------A1------|    ...    |--------An------|
         *      repetition 0                 repetition 1                 repetition n
         */
        testAlarms = listOf(
            Alarm( // A
                start = startTime(dayOfMonth = 10, hour = 5, minute = 0),
                isLightAlarm = true,
                lightAlarmDuration = 30.minutes,
                repetition = Repetition.WEEKLY
            )
        )
    }


    /**
     * (W1) Requested alarm does overlap because of not respecting buffer of A0
     *
     *   |<                               max 1 year                               >|
     *   |                                                                          |
     *   |<      30m     >|< 1w-30m  >|<     30m      >|< nw-LAD  >|<      30m     >|
     *
     *  10th            10th         17th            17th
     *  4:30            5:00         4:30            5:00
     *   |--------A0------|           |--------A1------|    ...    |--------An------|
     *      repetition 0                 repetition 1                 repetition n
     *                    |<ASB> <ASB>|
     *                    |--W--|
     *                         5:30
     *                         10th
     */
    @Test
    fun w1() {
        val w = startTime(dayOfMonth = 10, hour = 5, minute = 30)
        assertThat(
            w.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isTrue()
    }


    /**
     * (W2) Requested alarm does overlap because of not respecting buffer of A1
     *
     *   |<                               max 1 year                               >|
     *   |                                                                          |
     *   |<      30m     >|< 1w-30m  >|<     30m      >|< nw-LAD  >|<      30m     >|
     *
     *  10th            10th         17th            17th
     *  4:30            5:00         4:30            5:00
     *   |--------A0------|           |--------A1------|    ...    |--------An------|
     *      repetition 0                 repetition 1                 repetition n
     *                    |<ASB> <ASB>|
     *                          |--W--|
     *                         4:00
     *                         17th
     */
    @Test
    fun w2() {
        val w = startTime(dayOfMonth = 17, hour = 4, minute = 30)
        assertThat(
            w.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isTrue()
    }


    /**
     * (W3) Requested alarm does not overlap because of respecting buffer of A1
     *
     *   |<                               max 1 year                               >|
     *   |                                                                          |
     *   |<      30m     >|< 1w-30m  >|<     30m      >|<  nw-LAD >|<      30m     >|
     *
     *  10th            10th         17th            17th
     *  4:30            5:00         4:30            5:00
     *   |--------A0------|           |--------A1------|    ...    |--------An------|
     *      repetition 0                 repetition 1                 repetition n
     *                    |<ASB> <ASB>|
     *                         |W|
     *                          4:25
     *                          17th
     */
    @Test
    fun w3() {
        val w = startTime(dayOfMonth = 11, hour = 4, minute = 25)
        assertThat(
            w.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isFalse()
    }


    /**
     * (W4) Requested alarm does not overlap because of respecting buffer of A0
     *
     *   |<                                  max 1 year                               >|
     *   |                                                                             |
     *   |<      30m     >|< 1w-30m     >|<     30m      >|< nw-LAD  >|<      30m     >|
     *
     *  10th            10th           17th            17th
     *  4:30            5:00           4:30            5:00
     *   |--------A0------|             |--------A1------|    ...    |--------An------|
     *      repetition 0                   repetition 1                 repetition n
     *                    |<ASB>| <ASB>|
     *                          |W|
     *                           5:35
     *                           10th
     */
    @Test
    fun w4() {
        val w = startTime(dayOfMonth = 10, hour = 5, minute = 35)
        assertThat(
            w.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isFalse()
    }


    /**
     * (W5) Requested alarm does not overlap because it's more than 1 year after repetition
     *
     *   |<                               max 1 year                               >|
     *   |                                                                          |
     *   |<      30m     >|< 1w-30m  >|<     30m      >|< nw-LAD  >|<      30m     >|
     *                2024-01                                                   2025-01
     *  10th             10th        17th            17th                          15th
     *  4:30             5:00        4:30            5:00                          5:00
     *   |--------A0------|           |--------A1------|    ...    |--------An------|
     *      repetition 0                 repetition 1                 repetition n
     *                    |<ASB> <ASB>|
     *                                                                                   |----W----|
     *                                                                                          5:00
     *                    |                                                                     22th
     *                2024-01                                                                2025-01
     */
    @Test
    fun w5() {
        val w = startTime(year = 2025, dayOfMonth = 22, hour = 5, minute = 0)
        assertThat(
            w.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isFalse()
    }


    /**
     * (W6) Requested alarm does overlap A1 completely
     *
     *   |<                               max 1 year                               >|
     *   |                                                                          |
     *   |<      30m     >|< 1w-30m  >|<     30m      >|< nw-LAD  >|<      30m     >|
     *
     *  10th            10th         17th            17th
     *  4:30            5:00         4:30            5:00
     *   |--------A0------|           |--------A1------|    ...    |--------An------|
     *      repetition 0                 repetition 1                 repetition n
     *                    |<ASB> <ASB>|
     *                          |-------------W-------------|
     *                         4:00                        5:30
     *                         17th                        17th
     */
    @Test
    fun w6() {
        val w = startTime(dayOfMonth = 17, hour = 5, minute = 30)
        assertThat(
            w.interferesScheduledAlarms(
                lightAlarmDuration = 90.minutes, testAlarms))
            .isTrue()
    }


    /**
     * (W7) Requested alarm does overlap several repetitions
     *
     *   |<                               max 1 year                               >|
     *   |                                                                          |
     *   |<      30m     >|< 1w-30m  >|<     30m      >|< nw-LAD  >|<      30m     >|
     *                                             2024-01
     *  10th            10th         17th            17th
     *  4:30            5:00         4:30            5:00
     *   |--------A0------|           |--------A1------|    ...    |--------An------|
     *      repetition 0                 repetition 1                 repetition n
     *                    |<ASB> <ASB>|
     *                                                   |---W---|
     *                                                  5:00    5:00
     *                                                  22th    22th
     *                                                2024-05  2024-08
     */
    @Test
    fun w7() {
        val w = startTime(month = 8, dayOfMonth = 22, hour = 5, minute = 0)
        val lat = startTime(month = 5, dayOfMonth = 22, hour = 5, minute = 0)
        assertThat(
            w.interferesScheduledAlarms(
                // This is far away from being realistic, just to test overlapping ...
                lightAlarmDuration = Duration.between(lat, w),
                testAlarms))
            .isTrue()
    }
}
