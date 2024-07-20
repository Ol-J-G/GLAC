package de.oljg.glac.feature_alarm.ui.utils

import com.google.common.truth.Truth.assertThat
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.test.utils.localDateTimeOf
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.time.Duration.Companion.ZERO
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
     * (NLA1) Requested alarm does overlap because of not respecting buffer
     *
     *   |<     30m     >|<   60m   >|<     60m     >|
     *
     *  4:30            5:00        6:00            7:00
     *   |-------A-------|           |-------B-------|
     *                   |<ASB> <ASB>|
     *                              |
     *                             NLA
     */
    @Test
    fun nla1() {
        val nla = startTime(hour = 5, minute = 59)
        assertThat(
            nla.interferesScheduledAlarms(
                lightAlarmDuration = ZERO, testAlarms))
            .isTrue()
    }

    /**
     * (NLA2) Requested alarm does not overlap because of respecting buffer
     *
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<ASB> <ASB>|
     *                         NLA
     *
     */
    @Test
    fun nla2() {
        val nla = startTime(hour = 5, minute = 55)
        assertThat(
            nla.interferesScheduledAlarms(
                lightAlarmDuration = ZERO, testAlarms))
            .isFalse()
    }

    /**
     * (NLA3) Requested alarm may not be in range of alarm A
     *
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<ASB>  <ASB>|
     *          NLA
     *
     */
    @Test
    fun nla3() {
        val nla = startTime(hour = 4, minute = 45)
        assertThat(
            nla.interferesScheduledAlarms(
                lightAlarmDuration = ZERO, testAlarms))
            .isTrue()
    }

    /**
     * (NLA4) Requested alarm may not be in range of alarm B
     *
     *   |<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:30            5:00         6:00            7:00
     *   |-------A-------|            |-------B-------|
     *                   |<ASB>  <ASB>|
     *                                       NLA
     */
    @Test
    fun nla4() {
        val nla = startTime(hour = 6, minute = 30)
        assertThat(
            nla.interferesScheduledAlarms(
                lightAlarmDuration = ZERO, testAlarms))
            .isTrue()
    }

    /**
     * (NLA5) Request an alarm to be before alarm A is allowed
     *
     *   |<    30m   >|<     30m     >|<    60m   >|<     60m     >|
     *
     *  4:00         4:30            5:00         6:00            7:00
     *   |            |-------A-------|            |-------B-------|
     *           <ASB>|               |<ASB> <ASB>|
     *  NLA
     *
     */
    @Test
    fun nla5() {
        val nla = startTime(hour = 4, minute = 0)
        assertThat(
            nla.interferesScheduledAlarms(
                lightAlarmDuration = ZERO, testAlarms))
            .isFalse()
    }

    /**
     * (NLA6) Request an alarm to be after alarm B is allowed
     *
     *    |<     30m     >|<    60m   >|<     60m     >|<   30m    >|              
     *
     *   4:30            5:00         6:00            7:00         7:30
     *    |-------A-------|            |-------B-------|            |
     *                    |<ASB>  <ASB>|               |<ASB>
     *                                                             NLA
     *
     */
    @Test
    fun nla6() {
        val nla = startTime(hour = 7, minute = 30)
        assertThat(
            nla.interferesScheduledAlarms(
                lightAlarmDuration = ZERO, testAlarms))
            .isFalse()
    }
}
