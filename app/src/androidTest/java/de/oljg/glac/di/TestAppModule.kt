package de.oljg.glac.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.oljg.glac.feature_alarm.data.repository.AlarmSettingsRepositoryImpl
import de.oljg.glac.feature_alarm.domain.manager.AlarmScheduler
import de.oljg.glac.feature_alarm.domain.manager.AndroidAlarmScheduler
import de.oljg.glac.feature_alarm.domain.model.AlarmSettings
import de.oljg.glac.feature_alarm.domain.model.serializer.AlarmSettingsSerializer
import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository
import de.oljg.glac.feature_alarm.domain.use_case.AddAlarm
import de.oljg.glac.feature_alarm.domain.use_case.AlarmUseCases
import de.oljg.glac.feature_alarm.domain.use_case.GetAlarmSettingsFlow
import de.oljg.glac.feature_alarm.domain.use_case.GetAlarms
import de.oljg.glac.feature_alarm.domain.use_case.ReScheduleAllAlarms
import de.oljg.glac.feature_alarm.domain.use_case.RemoveAlarm
import de.oljg.glac.feature_alarm.domain.use_case.UpdateAlarm
import de.oljg.glac.feature_alarm.domain.use_case.UpdateAlarmDefaultsSectionIsExpanded
import de.oljg.glac.feature_alarm.domain.use_case.UpdateAlarmSoundFadeDuration
import de.oljg.glac.feature_alarm.domain.use_case.UpdateAlarmSoundUri
import de.oljg.glac.feature_alarm.domain.use_case.UpdateIsLightAlarm
import de.oljg.glac.feature_alarm.domain.use_case.UpdateLightAlarmDuration
import de.oljg.glac.feature_alarm.domain.use_case.UpdateRepetition
import de.oljg.glac.feature_alarm.domain.use_case.UpdateSnoozeDuration
import de.oljg.glac.feature_alarm.ui.utils.toEpochMillis
import de.oljg.glac.feature_clock.data.repository.ClockSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.time.LocalDateTime
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    fun provideClockSettingsRepository(
        @ApplicationContext context: Context,

    ): ClockSettingsRepository {
        return ClockSettingsRepository(context)
    }

    @Provides
    @Singleton
    fun provideAlarmSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<AlarmSettings> {
        return DataStoreFactory.create(
            serializer = AlarmSettingsSerializer,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            /**
             * Generate a different unique file for every test case, to prevent
             * java.lang.IllegalStateException:
             * There are multiple DataStores active for the same file ...
             */
            produceFile = { context.dataStoreFile("test"
                    + LocalDateTime.now().toEpochMillis() + "-alarm-settings.json") }
        )
    }

    @Provides
    @Singleton
    fun provideAlarmSettingsRepository(
        dataStore: DataStore<AlarmSettings>
    ): AlarmSettingsRepository {
        return AlarmSettingsRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideAlarmScheduler(
        @ApplicationContext context: Context
    ): AlarmScheduler {
        return AndroidAlarmScheduler(context)
    }

    @Provides
    @Singleton
    fun provideAlarmUseCases(
        repository: AlarmSettingsRepository,
        scheduler: AlarmScheduler
    ): AlarmUseCases {
        return AlarmUseCases(
            getAlarmSettingsFlow = GetAlarmSettingsFlow(repository),
            updateAlarmDefaultsSectionIsExpanded = UpdateAlarmDefaultsSectionIsExpanded(repository),
            updateIsLightAlarm = UpdateIsLightAlarm(repository),
            updateLightAlarmDuration = UpdateLightAlarmDuration(repository),
            updateSnoozeDuration = UpdateSnoozeDuration(repository),
            updateRepetition = UpdateRepetition(repository),
            updateAlarmSoundUri = UpdateAlarmSoundUri(repository),
            updateAlarmSoundFadeDuration = UpdateAlarmSoundFadeDuration(repository),
            removeAlarm = RemoveAlarm(repository, scheduler),
            addAlarm = AddAlarm(repository, scheduler),
            updateAlarm = UpdateAlarm(repository, scheduler),
            reScheduleAllAlarms = ReScheduleAllAlarms(scheduler),
            getAlarms = GetAlarms(repository)
        )
    }
}
