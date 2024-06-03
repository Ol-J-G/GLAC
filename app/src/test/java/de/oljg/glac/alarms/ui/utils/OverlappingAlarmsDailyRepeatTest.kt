package de.oljg.glac.alarms.ui.utils

import com.google.common.truth.Truth.assertThat
import de.oljg.glac.core.alarms.data.Alarm
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes


/**
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
                repeat = RepeatMode.DAILY
            )
        )
    }


    /**
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
    fun `(D1) Requested alarm does overlap because of not respecting buffer of A0`() {
        val d = startTime(dayOfMonth = 10, hour = 5, minute = 30)
        assertThat(
            d.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isTrue()
    }


    /**
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
    fun `(D2) Requested alarm does overlap because of not respecting buffer of A1`() {
        val d = startTime(dayOfMonth = 11, hour = 4, minute = 30)
        assertThat(
            d.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isTrue()
    }


    /**
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
    fun `(D3) Requested alarm does not overlap because of respecting buffer of A1`() {
        val d = startTime(dayOfMonth = 11, hour = 4, minute = 25)
        assertThat(
            d.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isFalse()
    }


    /**
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
    fun `(D4) Requested alarm does not overlap because of respecting buffer of A0`() {
        val d = startTime(dayOfMonth = 10, hour = 5, minute = 35)
        assertThat(
            d.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isFalse()
    }


    /**
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
    fun `(D5) Requested alarm does not overlap because it's more than 1 year after repetition`() {
        val d = startTime(year = 2025, dayOfMonth = 11, hour = 5, minute = 0)
        assertThat(
            d.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isFalse()
    }


    /**
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
    fun `(D6) Requested alarm does overlap A1 completely`() {
        val d = startTime(dayOfMonth = 11, hour = 5, minute = 30)
        assertThat(
            d.interferesScheduledAlarms(
                lightAlarmDuration = 90.minutes, testAlarms))
            .isTrue()
    }


    /**
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
    fun `(D7) Requested alarm does overlap several repetitions`() {
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
