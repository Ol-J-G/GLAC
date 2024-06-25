package de.oljg.glac.feature_alarm.ui.utils

import com.google.common.truth.Truth.assertThat
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.util.localDateTimeOf
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.time.Duration.Companion.minutes

/**
 * Unit under test: [interferesScheduledAlarms]
 *
 * Note: Sketches in comments are not to scale!
 */
class OverlappingAlarmsAdvancedTest {
    private lateinit var testAlarms: List<Alarm>
    private var january2024Tenth = LocalDate.of(2024, 1, 10)

    private fun startTime(hour: Int, minute: Int) = localDateTimeOf(january2024Tenth, hour, minute)

    @Before
    fun init() {
        /**
         *   |<     30m     >|<   60m   >|<     60m     >|
         *
         *  4:30            5:00        6:00            7:00
         *   |-------A-------|           |-------B-------|
         */
        testAlarms = listOf(
            Alarm( // A
                start = startTime(hour = 5, minute = 0),
                isLightAlarm = true,
                lightAlarmDuration = 30.minutes
            ),
            Alarm( // B
                start = startTime(hour = 7, minute = 0),
                isLightAlarm = true,
                lightAlarmDuration = 60.minutes
            )
        )
    }

    /**
     *   |<     30m     >|<   60m   >|<     60m     >|
     *
     *  4:30            5:00        6:00            7:00
     *   |-------A-------|           |-------B-------|
     *                   |<5m>   <5m>|
     *                   |-----T-----|
     */
    @Test
    fun `(T1) Requested alarm does overlap because of not respecting buffer`() {
        val t = startTime(hour = 6, minute = 0)
        assertThat(
            t.interferesScheduledAlarms(
                lightAlarmDuration = 60.minutes, testAlarms))
            .isTrue()
    }

    /**
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>    <5m>|
     *                       |-T-|
     *                       |50m|
     */
    @Test
    fun `(T2) Requested alarm does not overlap because of respecting buffer`() {
        val t = startTime(hour = 5, minute = 55)
        assertThat(
            t.interferesScheduledAlarms(
                lightAlarmDuration = 50.minutes, testAlarms))
            .isFalse()
    }

    /**
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>    <5m>|
     *           |------T------|
     *           |<    45m    >|
     */
    @Test
    fun `(T3) Requested alarm may not overlap alarm A`() {
        val t = startTime(hour = 5, minute = 30)
        assertThat(
            t.interferesScheduledAlarms(
                lightAlarmDuration = 45.minutes, testAlarms))
            .isTrue()
    }

    /**
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>    <5m>|
     *                         |------T------|
     *                         |<    45m    >|
     */
    @Test
    fun `(T4) Requested alarm may not overlap alarm B`() {
        val t = startTime(hour = 6, minute = 30)
        assertThat(
            t.interferesScheduledAlarms(
                lightAlarmDuration = 45.minutes, testAlarms))
            .isTrue()
    }

    /**
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>    <5m>|
     *           |-------------T-------------|
     *           |<           90m           >|
     */
    @Test
    fun `(T5) Requested alarm may not overlap alarm A and B`() {
        val t = startTime(hour = 6, minute = 30)
        assertThat(
            t.interferesScheduledAlarms(
                lightAlarmDuration = 90.minutes, testAlarms))
            .isTrue()
    }

    /**
     * |<15m>|<     30m     >|<    60m   >|<     60m     >|<15m>|
     *
     *      4:30            5:00         6:00            7:00
     *       |-------A-------|            |-------B-------|
     *                       |<5m>    <5m>|
     * |---------------------------T----------------------------|
     * |<                       180m(3h)                       >|
     */
    @Test
    fun `(T6) Requested alarm may not overlap,include,span alarm A (and B)`() {
        val t = startTime(hour = 7, minute = 15)
        assertThat(
            t.interferesScheduledAlarms(
                lightAlarmDuration = 180.minutes, testAlarms))
            .isTrue()
    }

    /**
     * |<15m>|<     30m     >|<    60m   >|<     60m     >|<15m>|
     *
     *      4:30            5:00         6:00            7:00
     *       |-------A-------|            |-------B-------|
     *                       |<5m>    <5m>|
     *                             |-------------T--------------|
     *                             |<          105m            >|
     */
    @Test
    fun `(T7) Requested alarm may not overlap,include,span alarm B`() {
        val t = startTime(hour = 7, minute = 15)
        assertThat(
            t.interferesScheduledAlarms(
                lightAlarmDuration = 105.minutes, testAlarms))
            .isTrue()
    }

    /**
     *                |<    30m   >|<     30m     >|<    60m   >|<     60m     >|
     *
     *               4:00         4:30            5:00         6:00            7:00
     *                |            |-------A-------|            |-------B-------|
     *                                             |<5m>    <5m>|
     *  |------T------|
     *  |<    30m    >|
     */
    @Test
    fun `(T8) Request an alarm to be before alarm A is allowed`() {
        val t = startTime(hour = 4, minute = 0)
        assertThat(
            t.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isFalse()
    }

    /**
     *    |<     30m     >|<    60m   >|<     60m     >|<   30m    >|              
     *
     *   4:30            5:00         6:00            7:00         7:30          8:00
     *    |-------A-------|            |-------B-------|            |             |
     *                    |<5m>    <5m>|
     *                                                              |------T------|
     *                                                              |<    30m    >|
     */
    @Test
    fun `(T9) Request an alarm to be after alarm B is allowed`() {
        val t = startTime(hour = 8, minute = 0)
        assertThat(
            t.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isFalse()
    }

    /**
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>    <5m>|
     *   |-------T-------|
     *   |<     30m     >|
     */
    @Test
    fun `(T10) Requested alarm may not overlap alarm A exactly`() {
        val t = startTime(hour = 5, minute = 0)
        assertThat(
            t.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms))
            .isTrue()
    }
}
