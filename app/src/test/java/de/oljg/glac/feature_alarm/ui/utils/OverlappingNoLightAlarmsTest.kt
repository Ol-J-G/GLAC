package de.oljg.glac.feature_alarm.ui.utils

import com.google.common.truth.Truth.assertThat
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.util.localDateTimeOf
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.minutes

/**
 * Unit under test: [interferesScheduledAlarms]
 *
 * Note: Sketches in comments are not to scale!
 */
class OverlappingNoLightAlarmsTest {
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
     *                              |
     *                             NLA
     */
    @Test
    fun `(NLA1) Requested alarm does overlap because of not respecting buffer`() {
        val nla = startTime(hour = 5, minute = 59)
        assertThat(
            nla.interferesScheduledAlarms(
                lightAlarmDuration = ZERO, testAlarms))
            .isTrue()
    }

    /**
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>   |<5m>|
     *                         NLA
     *
     */
    @Test
    fun `(NLA2) Requested alarm does not overlap because of respecting buffer`() {
        val nla = startTime(hour = 5, minute = 55)
        assertThat(
            nla.interferesScheduledAlarms(
                lightAlarmDuration = ZERO, testAlarms))
            .isFalse()
    }

    /**
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>    <5m>|
     *          NLA
     *
     */
    @Test
    fun `(NLA3) Requested alarm may not be in range of alarm A`() {
        val nla = startTime(hour = 4, minute = 45)
        assertThat(
            nla.interferesScheduledAlarms(
                lightAlarmDuration = ZERO, testAlarms))
            .isTrue()
    }

    /**
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>    <5m>|
     *                                       NLA
     */
    @Test
    fun `(NLA4) Requested alarm may not be in range of alarm B`() {
        val nla = startTime(hour = 6, minute = 30)
        assertThat(
            nla.interferesScheduledAlarms(
                lightAlarmDuration = ZERO, testAlarms))
            .isTrue()
    }







    /**
     *   |<    30m   >|<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:00         4:30            5:00         6:00            7:00
     *   |            |-------A-------|            |-------B-------|
     *            <5m>|               |<5m>    <5m>|
     *  NLA
     *
     */
    @Test
    fun `(NLA5) Request an alarm to be before alarm A is allowed`() {
        val nla = startTime(hour = 4, minute = 0)
        assertThat(
            nla.interferesScheduledAlarms(
                lightAlarmDuration = ZERO, testAlarms))
            .isFalse()
    }

    /**
     *    |<     30m     >|<    60m   >|<     60m     >|<   30m    >|              
     *
     *   4:30            5:00         6:00            7:00         7:30
     *    |-------A-------|            |-------B-------|            |
     *                    |<5m>    <5m>|               |<5m>
     *                                                             NLA
     *
     */
    @Test
    fun `(NLA6) Request an alarm to be after alarm B is allowed`() {
        val nla = startTime(hour = 7, minute = 30)
        assertThat(
            nla.interferesScheduledAlarms(
                lightAlarmDuration = ZERO, testAlarms))
            .isFalse()
    }
}
