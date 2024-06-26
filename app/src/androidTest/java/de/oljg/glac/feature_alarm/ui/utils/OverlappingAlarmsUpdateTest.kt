package de.oljg.glac.feature_alarm.ui.utils

import com.google.common.truth.Truth.assertThat
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.test.util.localDateTimeOf
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
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
class OverlappingAlarmsUpdateTest {
    private lateinit var testAlarms: List<Alarm>
    private lateinit var alarmToUpdate: Alarm

    private var january2024Tenth = LocalDate.of(2024, 1, 10)
    private fun startTime(hour: Int, minute: Int) = localDateTimeOf(january2024Tenth, hour, minute)

    @Before
    fun init() {
        /**
         * This alarm is going to be updated, but the new requested time range may not interfere
         * with alread scheduled alarms in testAlarms.
         */
        alarmToUpdate = Alarm(
            start = startTime(hour = 7, minute = 0),
            isLightAlarm = true,
            lightAlarmDuration = 60.minutes
        )

        /**
         *   |<     30m     >|<   60m   >|<     60m     >|
         *
         *  4:30            5:00        6:00            7:00
         *   |-------A-------|           |-------B-------|
         *                                 alarmToUpdate
         */
        testAlarms = listOf(
            Alarm( // A
                start = startTime(hour = 5, minute = 0),
                isLightAlarm = true,
                lightAlarmDuration = 30.minutes
            ),
            alarmToUpdate // B
        )
    }

    /**
     * (U1) alarmToUpdate does overlap because of not respecting buffer to alarm A
     *
     *   |<     30m     >|<   60m   >|<     60m     >|
     *
     *  4:30            5:00        6:00            7:00
     *   |-------A-------|           |-------B-------|
     *                   |<5m>   <5m>| alarmToUpdate
     *                   |---------U--------|
     *                   |<       90m      >|
     */
    @Test
    fun u1() {
        val u = startTime(hour = 6, minute = 30)
        assertThat(
            u.interferesScheduledAlarms(
                lightAlarmDuration = 90.minutes, testAlarms, alarmToUpdate))
            .isTrue()
    }

    /**
     * (U2) alarmToUpdate does not overlap because of respecting buffer to alarm A
     *
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>    <5m>| alarmToUpdate
     *                       |-------U-------|
     *                       |<      85m    >|
     */
    @Test
    fun u2() {
        val u = startTime(hour = 6, minute = 30)
        assertThat(
            u.interferesScheduledAlarms(
                lightAlarmDuration = 85.minutes, testAlarms, alarmToUpdate))
            .isFalse()
    }

    /**
     * (U3) alarmToUpdate does overlap alarm A
     *
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>    <5m>| alarmToUpdate
     *           |------U------|
     *           |<    45m    >|
     */
    @Test
    fun u3() {
        val u = startTime(hour = 5, minute = 30)
        assertThat(
            u.interferesScheduledAlarms(
                lightAlarmDuration = 45.minutes, testAlarms, alarmToUpdate))
            .isTrue()
    }

    /**
     * (U4) alarmToUpdate is allowed to overlap,update itself
     *
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>    <5m>| alarmToUpdate
     *                         |------U------|
     *                         |<    45m    >|
     */
    @Test
    fun u4() {
        val u = startTime(hour = 6, minute = 30)
        assertThat(
            u.interferesScheduledAlarms(
                lightAlarmDuration = 45.minutes, testAlarms, alarmToUpdate))
            .isFalse()
    }

    /**
     * (U5) alarmToUpdate may overlap itself, but not alarm A
     *
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>    <5m>| alarmToUpdate
     *           |-------------U-------------|
     *           |<           90m           >|
     */
    @Test
    fun u5() {
        val u = startTime(hour = 6, minute = 30)
        assertThat(
            u.interferesScheduledAlarms(
                lightAlarmDuration = 90.minutes, testAlarms, alarmToUpdate))
            .isTrue()
    }

    /**
     * (U6) alarmToUpdate may overlap,include,span itself but not alarm A
     *
     * |<15m>|<     30m     >|<    60m   >|<     60m     >|<15m>|
     *
     *      4:30            5:00         6:00            7:00
     *       |-------A-------|            |-------B-------|
     *                       |<5m>    <5m>| alarmToUpdate
     * |---------------------------U----------------------------|
     * |<                       180m(3h)                       >|
     */
    @Test
    fun u6() {
        val u = startTime(hour = 7, minute = 15)
        assertThat(
            u.interferesScheduledAlarms(
                lightAlarmDuration = 180.minutes, testAlarms, alarmToUpdate))
            .isTrue()
    }

    /**
     * (U7) alarmToUpdate may overlap,include,span itself
     *
     * |<15m>|<     30m     >|<    60m   >|<     60m     >|<15m>|
     *
     *      4:30            5:00         6:00            7:00
     *       |-------A-------|            |-------B-------|
     *                       |<5m>    <5m>| alarmToUpdate
     *                             |-------------U--------------|
     *                             |<          105m            >|
     */
    @Test
    fun u7() {
        val u = startTime(hour = 7, minute = 15)
        assertThat(
            u.interferesScheduledAlarms(
                lightAlarmDuration = 105.minutes, testAlarms, alarmToUpdate))
            .isFalse()
    }

    /**
     * (U8) Update alarm B to be before alarm A is allowed
     *
     *                |<    30m   >|<     30m     >|<    60m   >|<     60m     >|
     *
     *               4:00         4:30            5:00         6:00            7:00
     *                |            |-------A-------|            |-------B-------|
     *                                             |<5m>    <5m>| alarmToUpdate
     *  |------U------|
     *  |<    30m    >|
     */
    @Test
    fun u8() {
        val u = startTime(hour = 4, minute = 0)
        assertThat(
            u.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms, alarmToUpdate))
            .isFalse()
    }

    /**
     * (U9) Update alarm B to be after alarm B is allowed
     *
     *    |<     30m     >|<    60m   >|<     60m     >|<   30m    >|              
     *
     *   4:30            5:00         6:00            7:00         7:30          8:00
     *    |-------A-------|            |-------B-------|            |             |
     *                    |<5m>    <5m>| alarmToUpdate
     *                                                              |------U------|
     *                                                              |<    30m    >|
     */
    @Test
    fun u9() {
        val u = startTime(hour = 8, minute = 0)
        assertThat(
            u.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms, alarmToUpdate))
            .isFalse()
    }

    /**
     * (U10) alarmToUpdate may not overlap alarm A exactly
     *
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<5m>    <5m>| alarmToUpdate
     *   |-------U-------|
     *   |<     30m     >|
     */
    @Test
    fun u10() {
        val u = startTime(hour = 5, minute = 0)
        assertThat(
            u.interferesScheduledAlarms(
                lightAlarmDuration = 30.minutes, testAlarms, alarmToUpdate))
            .isTrue()
    }
}
