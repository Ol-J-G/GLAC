package de.oljg.glac.feature_alarm.ui

import android.net.Uri
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.oljg.glac.di.AppModule
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.domain.model.AlarmSettings
import de.oljg.glac.feature_alarm.domain.use_case.AlarmUseCases
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import de.oljg.glac.feature_alarm.ui.utils.plus
import de.oljg.glac.test.utils.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Alarm use cases integration test.
 *
 * Note that [AlarmSettingsViewModel], which makes use of alarm use cases, will be covered in
 * end-to-end tests.
 *
 * Disadvantages:
 * - Longer execution time as an unit test!
 * - Mock/Fake needed for [de.oljg.glac.feature_alarm.domain.manager.AndroidAlarmScheduler]
 *   (but it's at least a very simple one => [de.oljg.glac.test.utils.FakeAlarmScheduler])
 *   => Not necessary (or even easily possible!?) to test [android.app.AlarmManager]
 *
 * Advantages:
 * - No need to write (and also test?!?/maintain :>) a fake repository mock.
 * - It's close to reality (By using Hilt/TestAppModule, the injected alarmUseCases are
 *   constructed with same 'infrastructure' as in real app, except datastore file, which is ofc
 *   wanted and necessary (similar as using in-memory DB in tests instead of real DB, and
 *   alarm scheduler).
 *
 *  TODO_LATER: evaluate and maybe add edge case tests
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@UninstallModules(AppModule::class) // Better not to 'mix' with TestAppModule
class AlarmUseCasesTest {
    /**
     * alarmUseCases will be executed in app's viewModelScope, but here in testing, a special test
     * dispatcher will be used to enable testing them in a test class.
     * See also runTest function below ...
     */
    @get:Rule(order = 0)
    var testCoroutineRule = TestCoroutineRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var alarmUseCases: AlarmUseCases

    @Before
    fun init() { hiltRule.inject() } // Get a 'fresh infrastructure' for every test case

    @Test
    fun alarmSettingsDefaultValues() = runTest {
        val alarmSettings = alarmUseCases.getAlarmSettingsFlow.execute().first()
        assertThat(alarmSettings).isEqualTo(AlarmSettings())
    }

    @Test
    fun updateSimpleAlarmDefaultValues() = runTest {
        // Given, user A wants to update some simple alarm default settings
        // When user A updates the following default alarm settings
        alarmUseCases.updateAlarmDefaultsSectionIsExpanded.execute(true)
        alarmUseCases.updateIsLightAlarm.execute(false)
        alarmUseCases.updateLightAlarmDuration.execute(10.minutes)
        alarmUseCases.updateRepetition.execute(Repetition.WEEKLY)
        alarmUseCases.updateSnoozeDuration.execute(10.minutes)
        alarmUseCases.updateAlarmSoundUri.execute(Uri.EMPTY)
        alarmUseCases.updateAlarmSoundFadeDuration.execute(10.seconds)

        // Then, user A expects, that those settings are persited
        val alarmSettings = alarmUseCases.getAlarmSettingsFlow.execute().first()
        assertThat(alarmSettings.alarmDefaultsSectionIsExpanded).isTrue()
        assertThat(alarmSettings.isLightAlarm).isFalse()
        assertThat(alarmSettings.lightAlarmDuration).isEqualTo(10.minutes)
        assertThat(alarmSettings.repetition).isEqualTo(Repetition.WEEKLY)
        assertThat(alarmSettings.snoozeDuration).isEqualTo(10.minutes)
        assertThat(alarmSettings.alarmSoundUri).isEqualTo(Uri.EMPTY)
        assertThat(alarmSettings.alarmSoundFadeDuration).isEqualTo(10.seconds)
    }

    @Test
    fun addAlarm() = runTest {
        // Given, user A wants to add/schedule the following alarm
        val alarm = buildTestAlarm()

        // And user A has not been scheduled any other alarm before
        var alarms = alarmUseCases.getAlarms.execute()
        assertThat(alarms).isEmpty()

        // When user A adds 'alarm'
        val hasBeenScheduled = alarmUseCases.addAlarm.execute(alarm)

        // Then, user A expects, that 'alarm' has been scheduled and added to alarms list
        assertThat(hasBeenScheduled).isTrue()
        alarms = alarmUseCases.getAlarms.execute()
        assertThat(alarms).hasSize(1)
        assertThat(alarms.first()).isEqualTo(alarm)
    }

    @Test
    fun removeAlarm() = runTest {
        // Given, user A has been added/scheduled the following alarm and wants to remove/cancel it
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val alarm = buildTestAlarm()
        assertThat(alarmUseCases.addAlarm.execute(alarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When user A tries to remove/cancel 'alarm'
        val hasBeenCanceled = alarmUseCases.removeAlarm.execute(alarm)

        // Then, user A expects that 'alarm' has been removed/canceled
        assertThat(hasBeenCanceled).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
    }


    @Test
    fun updateAlarm() = runTest {
        // Given, user A has been added/scheduled the following alarm and wants to update it
        assertThat(alarmUseCases.getAlarms.execute()).isEmpty()
        val alarm = buildTestAlarm()
        assertThat(alarmUseCases.addAlarm.execute(alarm)).isTrue()
        assertThat(alarmUseCases.getAlarms.execute()).hasSize(1)

        // When user A updates 'alarm' as follows
        val updatedAlarm = alarm.copy(
            start = LocalDateTime.now().plus(2.days),
            isLightAlarm = false,
            repetition = Repetition.NONE,
            snoozeDuration = 50.minutes
        )
        val hasBeenUpdated = alarmUseCases.updateAlarm.execute(
            alarmtoBeUpdated = alarm,
            updatedAlarm = updatedAlarm
        )

        //Then, user A expects, that the changes are persisted
        assertThat(hasBeenUpdated).isTrue()
        val alarms = alarmUseCases.getAlarms.execute()
        assertThat(alarms).hasSize(1)
        assertThat(alarms.first()).isEqualTo(updatedAlarm)
    }

    private fun buildTestAlarm() = Alarm(
        start = LocalDateTime.now().plus(1.days),
        isLightAlarm = true,
        lightAlarmDuration = 60.minutes,
        repetition = Repetition.DAILY,
        snoozeDuration = 45.minutes
        // default alarm sound, etc.
    )
}
