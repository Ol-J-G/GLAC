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
 */
class OverlappingAlarmsDailyRepeatTest {
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
         *   |<      30m     >|< 1d-30m  >|<     30m      >|< nd-LAD  >|<      30m     >|
         *
         *  10th            10th         11th            11th
         *  4:30            5:00         4:30            5:00
         *   |--------A0------|           |--------A1------|    ...    |--------An------|
         *      repetition 0                 repetition 1                 repetition n
         */
        testAlarms = listOf(
            Alarm( // A
                start = startTime(dayOfMonth = 10, hour = 5, minute = 0),
                isLightAlarm = true,
                lightAlarmDuration = 30.minutes,
                repetition = Repetition.DAILY
            )
        )
    }


    /**
     * (D1) Requested alarm does overlap because of not respecting buffer of A0
     *
     *   |<                               max 1 year                               >|
     *   |                                                                          |
     *   |<      30m     >|<  1d-30m >|<     30m      >|< nd-LAD  >|<      30m     >|
     *
     *  10th            10th         11th            11th
     *  4:30            5:00         4:30            5:00
     *   |--------A0------|           |--------A1------|    ...    |--------An------|
     *      repetition 0                 repetition 1                 repetition n
     *                    |<5m>   <5m>|
     *                    |--D--|
     *                         5:30
     *                         10th
     */
    @Test
    fun d1() {
        val d = startTime(dayOfMonth = 10, hour = 5, minute = 30)
        assertThat(
            d.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isTrue()
    }


    /**
     * (D2) Requested alarm does overlap because of not respecting buffer of A1
     *
     *   |<                               max 1 year                               >|
     *   |                                                                          |
     *   |<      30m     >|< 1d-30m  >|<     30m      >|< nd-LAD  >|<      30m     >|
     *
     *  10th            10th         11th            11th
     *  4:30            5:00         4:30            5:00
     *   |--------A0------|           |--------A1------|    ...    |--------An------|
     *      repetition 0                 repetition 1                 repetition n
     *                    |<5m>   <5m>|
     *                          |--D--|
     *                         4:00
     *                         11th
     */
    @Test
    fun d2() {
        val d = startTime(dayOfMonth = 11, hour = 4, minute = 30)
        assertThat(
            d.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isTrue()
    }


    /**
     * (D3) Requested alarm does not overlap because of respecting buffer of A1
     *
     *   |<                               max 1 year                               >|
     *   |                                                                          |
     *   |<      30m     >|< 1d-30m  >|<     30m      >|< nd-LAD  >|<      30m     >|
     *
     *  10th            10th         11th            11th
     *  4:30            5:00         4:30            5:00
     *   |--------A0------|           |--------A1------|    ...    |--------An------|
     *      repetition 0                 repetition 1                 repetition n
     *                    |<5m>  |<5m>|
     *                         |D|
     *                          4:25
     *                          11th
     */
    @Test
    fun d3() {
        val d = startTime(dayOfMonth = 11, hour = 4, minute = 25)
        assertThat(
            d.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isFalse()
    }


    /**
     * (D4) Requested alarm does not overlap because of respecting buffer of A0
     *
     *   |<                                max 1 year                               >|
     *   |                                                                           |
     *   |<      30m     >|< 1d-30m   >|<     30m      >|< nd-LAD  >|<      30m     >|
     *
     *  10th            10th          11th            11th
     *  4:30            5:00          4:30            5:00
     *   |--------A0------|            |--------A1------|    ...    |--------An------|
     *      repetition 0                  repetition 1                 repetition n
     *                    |<5m>|   <5m>|
     *                         |D|
     *                          5:35
     *                          10th
     */
    @Test
    fun d4() {
        val d = startTime(dayOfMonth = 10, hour = 5, minute = 35)
        assertThat(
            d.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isFalse()
    }


    /**
     * (D5) Requested alarm does not overlap because it's more than 1 year after repetition
     *
     *   |<                               max 1 year                               >|
     *   |                                                                          |
     *   |<      30m     >|< 1d-30m  >|<     30m      >|< nd-LAD  >|<      30m     >|
     *                2024-01                                                   2025-01
     *  10th             10th        11th            11th                          10th
     *  4:30             5:00        4:30            5:00                          5:00
     *   |--------A0------|           |--------A1------|    ...    |--------An------|
     *      repetition 0                 repetition 1                 repetition n
     *                    |<5m>   <5m>|
     *                                                                                   |----D----|
     *                                                                                          5:00
     *                    |                                                                     11th
     *                2024-01                                                                2025-01
     */
    @Test
    fun d5() {
        val d = startTime(year = 2025, dayOfMonth = 11, hour = 5, minute = 0)
        assertThat(
            d.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isFalse()
    }


    /**
     * (D6) Requested alarm does overlap A1 completely
     *
     *   |<                               max 1 year                               >|
     *   |                                                                          |
     *   |<      30m     >|<  1d-30m >|<     30m      >|< nd-LAD  >|<      30m     >|
     *
     *  10th            10th         11th            11th
     *  4:30            5:00         4:30            5:00
     *   |--------A0------|           |--------A1------|    ...    |--------An------|
     *      repetition 0                 repetition 1                 repetition n
     *                    |<5m>   <5m>|
     *                          |-------------D-------------|
     *                         4:00                        5:30
     *                         11th                        11th
     */
    @Test
    fun d6() {
        val d = startTime(dayOfMonth = 11, hour = 5, minute = 30)
        assertThat(
            d.interferesScheduledAlarms(
                lightAlarmDuration = 90.minutes, testAlarms))
            .isTrue()
    }


    /**
     * (D7) Requested alarm does overlap several repetitions
     *
     *   |<                               max 1 year                               >|
     *   |                                                                          |
     *   |<      30m     >|< 1d-30m  >|<     30m       >|< nd-LAD >|<      30m     >|
     *                                             2024-01
     *  10th            10th         11th            11th
     *  4:30            5:00         4:30            5:00
     *   |--------A0------|           |--------A1------|    ...    |--------An------|
     *      repetition 0                 repetition 1                 repetition n
     *                    |<5m>   <5m>|
     *                                                   |---D---|
     *                                                  5:00    5:00
     *                                                  17th    17th
     *                                                2024-05  2024-08
     */
    @Test
    fun d7() {
        val d = startTime(month = 8, dayOfMonth = 17, hour = 5, minute = 0)
        val lat = startTime(month = 5, dayOfMonth = 17, hour = 5, minute = 0)
        assertThat(
            d.interferesScheduledAlarms(
                // This is far away from being realistic, just to test overlapping ...
                lightAlarmDuration = Duration.between(lat, d),
                testAlarms))
            .isTrue()
    }
}
