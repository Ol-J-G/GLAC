package de.oljg.glac.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.oljg.glac.core.alarms.data.repository.AlarmSettingsRepositoryImpl
import de.oljg.glac.core.alarms.domain.repository.AlarmSettingsRepository
import de.oljg.glac.core.alarms.domain.use_case.AddAlarm
import de.oljg.glac.core.alarms.domain.use_case.AlarmUseCases
import de.oljg.glac.core.alarms.domain.use_case.GetAlarmSettingsFlow
import de.oljg.glac.core.alarms.domain.use_case.GetAlarms
import de.oljg.glac.core.alarms.domain.use_case.ReScheduleAllAlarms
import de.oljg.glac.core.alarms.domain.use_case.RemoveAlarm
import de.oljg.glac.core.alarms.domain.use_case.UpdateAlarm
import de.oljg.glac.core.alarms.domain.use_case.UpdateAlarmDefaultsSectionIsExpanded
import de.oljg.glac.core.alarms.domain.use_case.UpdateAlarmSoundFadeDuration
import de.oljg.glac.core.alarms.domain.use_case.UpdateAlarmSoundUri
import de.oljg.glac.core.alarms.domain.use_case.UpdateIsLightAlarm
import de.oljg.glac.core.alarms.domain.use_case.UpdateLightAlarmDuration
import de.oljg.glac.core.alarms.domain.use_case.UpdateRepetition
import de.oljg.glac.core.alarms.domain.use_case.UpdateSnoozeDuration
import de.oljg.glac.core.alarms.manager.AlarmScheduler
import de.oljg.glac.core.alarms.manager.AndroidAlarmScheduler
import de.oljg.glac.core.clock.data.ClockSettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideClockSettingsRepository(
        @ApplicationContext context: Context
    ): ClockSettingsRepository {
        return ClockSettingsRepository(context)
    }

    @Provides
    @Singleton
    fun provideAlarmSettingsRepository(
        @ApplicationContext context: Context
    ): AlarmSettingsRepository {
        return AlarmSettingsRepositoryImpl(context)
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
