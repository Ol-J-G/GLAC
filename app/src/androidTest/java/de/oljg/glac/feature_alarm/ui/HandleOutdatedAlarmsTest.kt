package de.oljg.glac.feature_alarm.ui

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.oljg.glac.di.AppModule
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.domain.use_case.AlarmUseCases
import de.oljg.glac.feature_alarm.domain.utils.scaleToMinutes
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import de.oljg.glac.feature_alarm.ui.utils.minus
import de.oljg.glac.feature_alarm.ui.utils.plus
import de.oljg.glac.test.utils.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Use case under test: [de.oljg.glac.feature_alarm.domain.use_case.HandleOutdatedAlarms]
 *
 * Note that "now" means the time when app will be started (closed before, not just in
 * background) and is used in every test case within this test class.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@UninstallModules(AppModule::class) // Better not to 'mix' with TestAppModule
class HandleOutdatedAlarmsTest {
    @get:Rule(order = 0)
    var testCoroutineRule = TestCoroutineRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var alarmUseCases: AlarmUseCases

    @Before
    fun init() { hiltRule.inject() } // Get a 'fresh infrastructure' for every test case


    /**
     * "Negative" test just to check, that not-outdated alarms (=> valid => are in future
     * => can go off) will not be handled by
     * [de.oljg.glac.feature_alarm.domain.use_case.HandleOutdatedAlarms].
     */
    @Test
    fun doNotHandleValidAlarms() = runTest {
        // Given, user A has been added/scheduled the following valid alarm and starts app
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val validAlarm = Alarm(
            start = LocalDateTime.now().plus(60.minutes),
            isLightAlarm = true,
            lightAlarmDuration = 30.minutes,
            repetition = Repetition.NONE
        )
        assertThat(alarmUseCases.addAlarm.execute(validAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        // Then user A expects, that no valid (=> not outdated) alarms have been updated
        val notUpdatedAlarms = alarmUseCases.getAlarms.execute()
        assertThat(notUpdatedAlarms).hasSize(1)
        assertThat(notUpdatedAlarms[0] == validAlarm)
    }


    /**
     * Example
     *              |<    90m    >|
     * -------------|-------------|------>
     *      outdatedAlarmStart   now
     */
    @Test
    fun handleOutdatedAlarmsRepetitionNoneIsBefore() = runTest {
        // Given, user A has been added/scheduled the following alarm some time ago and starts app
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val outdatedAlarm = Alarm(
            start = LocalDateTime.now().minus(60.minutes),
            isLightAlarm = true,
            lightAlarmDuration = 30.minutes,
            repetition = Repetition.NONE
        )
        assertThat(alarmUseCases.addAlarm.execute(outdatedAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        // Then user A expects, that no outdated alarms are present
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
    }


    /**
     * Rare edge case
     *
     * Example
     * -----------|------>
     *           now
     *            |
     *    outdatedAlarmStart
     */
    @Test
    fun handleOutdatedAlarmsRepetitionNoneIsEqual() = runTest {
        // Given, user A has been added/scheduled the following alarm some time ago and starts app
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val justNotOutdatedYetAlarm = Alarm(
            start = LocalDateTime.now(),
            isLightAlarm = false,
            repetition = Repetition.NONE
        )
        assertThat(alarmUseCases.addAlarm.execute(justNotOutdatedYetAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        /**
         * Then user A expects, that justNotOutdatedYetAlarm is still present (and will go off
         * immediately when app start is completed ... XD )
         */
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)
    }


    /**
     * Example
     * -----------|-------------|---------------|-------------->
     *    outdatedAlarmStart   now    expectedNextAlarmStart
     *            |             |               |
     *         1st_4:00      5th_5:00        6th_4:00
     */
    @Test
    fun handleOutdatedAlarmsRepetitionDailyTimeIsBefore() = runTest {
        /**
         * Given, user A has been added/scheduled the following daily alarm some time ago and
         * didn't use the app since then (=> alarm outdated)
         */
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val now = LocalDateTime.now().scaleToMinutes()
        val outdatedAlarmStart = now.minus(4.days).minus(1.hours)
        val outDatedAlarm = Alarm(
            start = outdatedAlarmStart,
            repetition = Repetition.DAILY
        )
        assertThat(alarmUseCases.addAlarm.execute(outDatedAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        /**
         * Then user A expects, that outdated alarm's repetition is updated correctly
         * => Must be scheduled one day later (from now) at same time (hh:mm) as original
         *    daily alarm
         */
        val updatedAlarms = alarmUseCases.getAlarms.execute()
        assertThat(updatedAlarms).hasSize(1)
        val expectedNextAlarmStart = LocalDateTime.of(
            now.plus(1.days).toLocalDate(),
            LocalTime.of(outdatedAlarmStart.hour, outdatedAlarmStart.minute)
        )
        assertThat(updatedAlarms[0].start == expectedNextAlarmStart)
    }


    /**
     * Rare edge case
     *
     * Example
     * -----------|-------------|------------------->
     *    outdatedAlarmStart   now
     *            |             |
     *         1st_5:00      5th_5:00
     *                          |
     *                 expectedNextAlarmStart (alarm will go immediately off!)
     */
    @Test
    fun handleOutdatedAlarmsRepetitionDailyTimeIsEqual() = runTest {
        /**
         * Given, user A has been added/scheduled the following daily alarm some time ago and
         * didn't use the app since then (=> alarm outdated)
         */
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val now = LocalDateTime.now().scaleToMinutes()
        val outdatedAlarmStart = now.minus(4.days)
        val outDatedAlarm = Alarm(
            start = outdatedAlarmStart,
            repetition = Repetition.DAILY
        )
        assertThat(alarmUseCases.addAlarm.execute(outDatedAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        /**
         * Then user A expects, that outdated alarm's repetition is updated correctly
         * => Must be scheduled this day at same time (hh:mm) as original daily alarm
         */
        val updatedAlarms = alarmUseCases.getAlarms.execute()
        assertThat(updatedAlarms).hasSize(1)
        val expectedNextAlarmStart = LocalDateTime.of(
            now.toLocalDate(),
            LocalTime.of(outdatedAlarmStart.hour, outdatedAlarmStart.minute)
        )
        assertThat(updatedAlarms[0].start == expectedNextAlarmStart)
    }


    /**
     * Example
     * -----------|-------------|---------------|-------------->
     *    outdatedAlarmStart   now    expectedNextAlarmStart
     *            |             |               |
     *         1st_6:00      5th_5:00        5th_6:00
     */
    @Test
    fun handleOutdatedAlarmsRepetitionDailyTimeIsAfter() = runTest {
        /**
         * Given, user A has been added/scheduled the following daily alarm some time ago and
         * didn't use the app since then (=> alarm outdated)
         */
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val now = LocalDateTime.now().scaleToMinutes()
        val outdatedAlarmStart = now.minus(4.days).plus(1.hours)
        val outDatedAlarm = Alarm(
            start = outdatedAlarmStart,
            repetition = Repetition.DAILY
        )
        assertThat(alarmUseCases.addAlarm.execute(outDatedAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        /**
         * Then user A expects, that outdated alarm's repetition is updated correctly
         * => Must be scheduled this day at same time (hh:mm) as original daily alarm
         */
        val updatedAlarms = alarmUseCases.getAlarms.execute()
        assertThat(updatedAlarms).hasSize(1)
        val expectedNextAlarmStart = LocalDateTime.of(
            now.toLocalDate(),
            LocalTime.of(outdatedAlarmStart.hour, outdatedAlarmStart.minute)
        )
        assertThat(updatedAlarms[0].start == expectedNextAlarmStart)
    }


    /**
     * Example
     * -----------|--------------|--------------|-------------->
     *    outdatedAlarmStart    now   expectedNextAlarmStart
     *            |              |              |
     *         5th_6:00      12th_5:00      12th_6:00
     *           Fri            Fri            Fri
     */
    @Test
    fun handleOutdatedAlarmsRepetitionWeeklySameDayOfWeekTimeIsAfter() = runTest {
        /**
         * Given, user A has been added/scheduled the following weekly alarm some time ago and
         * didn't use the app since then (=> alarm outdated)
         */
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val now = LocalDateTime.now().scaleToMinutes()
        val outdatedAlarmStart = now.minus(7.days).plus(1.hours)
        val outDatedAlarm = Alarm(
            start = outdatedAlarmStart,
            repetition = Repetition.WEEKLY
        )
        assertThat(alarmUseCases.addAlarm.execute(outDatedAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        /**
         * Then user A expects, that outdated alarm's repetition is updated correctly
         * => Must be scheduled this day at same time (hh:mm) as original weekly alarm
         */
        val updatedAlarms = alarmUseCases.getAlarms.execute()
        assertThat(updatedAlarms).hasSize(1)
        val expectedNextAlarmStart = LocalDateTime.of(
            now.toLocalDate(),
            LocalTime.of(outdatedAlarmStart.hour, outdatedAlarmStart.minute)
        )
        assertThat(updatedAlarms[0].start == expectedNextAlarmStart)
    }


    /**
     * Rare edge case
     *
     * Example
     * -----------|--------------|----------------------->
     *    outdatedAlarmStart    now
     *            |              |
     *         5th_5:00      12th_5:00
     *           Fri            Fri
     *                           |
     *                 expectedNextAlarmStart
     */
    @Test
    fun handleOutdatedAlarmsRepetitionWeeklySameDayOfWeekTimeIsEqual() = runTest {
        /**
         * Given, user A has been added/scheduled the following weekly alarm some time ago and
         * didn't use the app since then (=> alarm outdated)
         */
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val now = LocalDateTime.now().scaleToMinutes()
        val outdatedAlarmStart = now.minus(7.days)
        val outDatedAlarm = Alarm(
            start = outdatedAlarmStart,
            repetition = Repetition.WEEKLY
        )
        assertThat(alarmUseCases.addAlarm.execute(outDatedAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        /**
         * Then user A expects, that outdated alarm's repetition is updated correctly
         * => Must be scheduled this day at same time (hh:mm) as original weekly alarm
         */
        val updatedAlarms = alarmUseCases.getAlarms.execute()
        assertThat(updatedAlarms).hasSize(1)
        val expectedNextAlarmStart = LocalDateTime.of(
            now.toLocalDate(),
            LocalTime.of(outdatedAlarmStart.hour, outdatedAlarmStart.minute)
        )
        assertThat(updatedAlarms[0].start == expectedNextAlarmStart)
    }


    /**
     * Example
     * -----------|--------------|--------------|-------------->
     *    outdatedAlarmStart    now   expectedNextAlarmStart
     *            |              |              |
     *         5th_4:00      12th_5:00      19th_4:00
     *           Fri            Fri            Fri
     */
    @Test
    fun handleOutdatedAlarmsRepetitionWeeklySameDayOfWeekTimeIsBefore() = runTest {
        /**
         * Given, user A has been added/scheduled the following weekly alarm some time ago and
         * didn't use the app since then (=> alarm outdated)
         */
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val now = LocalDateTime.now().scaleToMinutes()
        val outdatedAlarmStart = now.minus(7.days).minus(1.hours)
        val outDatedAlarm = Alarm(
            start = outdatedAlarmStart,
            repetition = Repetition.WEEKLY
        )
        assertThat(alarmUseCases.addAlarm.execute(outDatedAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        /**
         * Then user A expects, that outdated alarm's repetition is updated correctly
         * => Must be scheduled one week later (from now) at same time (hh:mm) as original
         *    weekly alarm
         */
        val updatedAlarms = alarmUseCases.getAlarms.execute()
        assertThat(updatedAlarms).hasSize(1)

        val expectedNextAlarmStart = LocalDateTime.of(
            now.plus(7.days).toLocalDate(),
            LocalTime.of(outdatedAlarmStart.hour, outdatedAlarmStart.minute)
        )
        assertThat(updatedAlarms[0].start == expectedNextAlarmStart)
    }


    /**
     * Example
     *            |<               1 week               >| (=> can also be n weeks!)
     *            |<       5 days        >|<   2 days   >|
     * -----------|-----------------------|--------------|-------------->
     *    outdatedAlarmStart             now   expectedNextAlarmStart
     *            |                       |              |
     *         5th_5:00               10th_5:00      12th_5:00
     *           Fri                     Wed            Fri
     */
    @Test
    fun handleOutdatedAlarmsRepetitionWeeklyDifferentDayOfWeek() = runTest {
        /**
         * Given, user A has been added/scheduled the following weekly alarm some time ago and
         * didn't use the app since then (=> alarm outdated)
         */
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val now = LocalDateTime.now().scaleToMinutes()
        val outdatedAlarmStart = now.minus(5.days)
        val outDatedAlarm = Alarm(
            start = outdatedAlarmStart,
            repetition = Repetition.WEEKLY
        )
        assertThat(alarmUseCases.addAlarm.execute(outDatedAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        /**
         * Then user A expects, that outdated alarm's repetition is updated correctly
         * => Must be scheduled next same dayOfWeek at same time (hh:mm) as original weekly alarm
         * (original weekly alarm is every Friday => expectedNextAlarmStart must be next
         * Friday calculated from now(=> Wednesday => + 2d))
         */
        val updatedAlarms = alarmUseCases.getAlarms.execute()
        assertThat(updatedAlarms).hasSize(1)

        val expectedNextAlarmStart = LocalDateTime.of(
            now.plus(2.days).toLocalDate(),
            LocalTime.of(outdatedAlarmStart.hour, outdatedAlarmStart.minute)
        )
        assertThat(updatedAlarms[0].start == expectedNextAlarmStart)
    }


    /**
     * Example
     * -----------|--------------|--------------|-------------->
     *    outdatedAlarmStart    now   expectedNextAlarmStart
     *            |              |              |
     *           6:00           5:00           6:00
     *        July_5th     August_5th     August_5th
     */
    @Test
    fun handleOutdatedAlarmsRepetitionMonthlySameDayOfMonthTimeIsAfter() = runTest {
        /**
         * Given, user A has been added/scheduled the following monthly alarm some time ago and
         * didn't use the app since then (=> alarm outdated)
         */
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val now = LocalDateTime.now().scaleToMinutes()
        val outdatedAlarmStart = now.minusMonths(1L).plus(1.hours)
        val outDatedAlarm = Alarm(
            start = outdatedAlarmStart,
            repetition = Repetition.MONTHLY
        )
        assertThat(alarmUseCases.addAlarm.execute(outDatedAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        /**
         * Then user A expects, that outdated alarm's repetition is updated correctly
         * => Must be scheduled this day at same time (hh:mm) as original monthly alarm
         */
        val updatedAlarms = alarmUseCases.getAlarms.execute()
        assertThat(updatedAlarms).hasSize(1)
        val expectedNextAlarmStart = LocalDateTime.of(
            now.toLocalDate(),
            LocalTime.of(outdatedAlarmStart.hour, outdatedAlarmStart.minute)
        )
        assertThat(updatedAlarms[0].start == expectedNextAlarmStart)
    }


    /**
     * Rare edge case
     *
     * Example
     * -----------|--------------|---------------------------->
     *    outdatedAlarmStart    now
     *            |              |
     *           5:00           5:00
     *        July_5th     August_5th
     *                           |
     *                 expectedNextAlarmStart
     */
    @Test
    fun handleOutdatedAlarmsRepetitionMonthlySameDayOfMonthTimeIsEqual() = runTest {
        /**
         * Given, user A has been added/scheduled the following monthly alarm some time ago and
         * didn't use the app since then (=> alarm outdated)
         */
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val now = LocalDateTime.now().scaleToMinutes()
        val outdatedAlarmStart = now.minusMonths(1L)
        val outDatedAlarm = Alarm(
            start = outdatedAlarmStart,
            repetition = Repetition.MONTHLY
        )
        assertThat(alarmUseCases.addAlarm.execute(outDatedAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        /**
         * Then user A expects, that outdated alarm's repetition is updated correctly
         * => Must be scheduled this day at same time (hh:mm) as original monthly alarm
         */
        val updatedAlarms = alarmUseCases.getAlarms.execute()
        assertThat(updatedAlarms).hasSize(1)
        val expectedNextAlarmStart = LocalDateTime.of(
            now.toLocalDate(),
            LocalTime.of(outdatedAlarmStart.hour, outdatedAlarmStart.minute)
        )
        assertThat(updatedAlarms[0].start == expectedNextAlarmStart)
    }


    /**
     * Example
     * -----------|--------------|--------------|-------------->
     *    outdatedAlarmStart    now   expectedNextAlarmStart
     *            |              |              |
     *           4:00           5:00           4:00
     *        July_5th     August_5th  September_5th
     */
    @Test
    fun handleOutdatedAlarmsRepetitionMonthlySameDayOfMonthTimeIsBefore() = runTest {
        /**
         * Given, user A has been added/scheduled the following monthly alarm some time ago and
         * didn't use the app since then (=> alarm outdated)
         */
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val now = LocalDateTime.now().scaleToMinutes()
        val outdatedAlarmStart = now.minusMonths(1L).plus(1.hours)
        val outDatedAlarm = Alarm(
            start = outdatedAlarmStart,
            repetition = Repetition.MONTHLY
        )
        assertThat(alarmUseCases.addAlarm.execute(outDatedAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        /**
         * Then user A expects, that outdated alarm's repetition is updated correctly
         * => Must be scheduled one month later (from now) at same time (hh:mm) as original
         *    monthly alarm
         */
        val updatedAlarms = alarmUseCases.getAlarms.execute()
        assertThat(updatedAlarms).hasSize(1)
        val expectedNextAlarmStart = LocalDateTime.of(
            now.plusMonths(1L).toLocalDate(),
            LocalTime.of(outdatedAlarmStart.hour, outdatedAlarmStart.minute)
        )
        assertThat(updatedAlarms[0].start == expectedNextAlarmStart)
    }


    /**
     * Example
     *            |<               1 month              >| (=> can also be n months!)
     *            |<       5 days        >|              |
     * -----------|-----------------------|--------------|-------------->
     *    outdatedAlarmStart             now   expectedNextAlarmStart
     *            |                       |              |
     *           5:00                    5:00           5:00
     *        July_5th                July_10th    August_5th
     */
    @Test
    fun handleOutdatedAlarmsRepetitionMonthlyDifferentDayOfWeek() = runTest {
        /**
         * Given, user A has been added/scheduled the following monthly alarm some time ago and
         * didn't use the app since then (=> alarm outdated)
         */
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val now = LocalDateTime.now().scaleToMinutes()
        val outdatedAlarmStart = now.minus(5.days)
        val outDatedAlarm = Alarm(
            start = outdatedAlarmStart,
            repetition = Repetition.MONTHLY
        )
        assertThat(alarmUseCases.addAlarm.execute(outDatedAlarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When app is starting, outdated alarms will be handled
        alarmUseCases.handleOutdatedAlarms.execute()

        /**
         * Then user A expects, that outdated alarm's repetition is updated correctly
         * => Must be scheduled next same dayOfMonth at same time (hh:mm) as original monthly alarm
         * (original monthly alarm is every 5th => expectedNextAlarmStart must be next month's 5th
         * calculated from now)
         */
        val updatedAlarms = alarmUseCases.getAlarms.execute()
        assertThat(updatedAlarms).hasSize(1)

        val expectedNextAlarmStart = LocalDateTime.of(
            outdatedAlarmStart.plusMonths(1L).toLocalDate(),
            LocalTime.of(outdatedAlarmStart.hour, outdatedAlarmStart.minute)
        )
        assertThat(updatedAlarms[0].start == expectedNextAlarmStart)
    }
}
