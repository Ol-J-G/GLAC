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
import de.oljg.glac.feature_alarm.domain.model.utils.AlarmDefaults
import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository
import de.oljg.glac.feature_alarm.domain.use_case.AddAlarm
import de.oljg.glac.feature_alarm.domain.use_case.AlarmUseCases
import de.oljg.glac.feature_alarm.domain.use_case.GetAlarmSettingsFlow
import de.oljg.glac.feature_alarm.domain.use_case.GetAlarms
import de.oljg.glac.feature_alarm.domain.use_case.HandleOutdatedAlarms
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
import de.oljg.glac.feature_clock.data.repository.ClockSettingsRepositoryImpl
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.serializer.ClockSettingsSerializer
import de.oljg.glac.feature_clock.domain.model.utils.ClockSettingsDefaults
import de.oljg.glac.feature_clock.domain.repository.ClockSettingsRepository
import de.oljg.glac.feature_clock.domain.use_case.ClockUseCases
import de.oljg.glac.feature_clock.domain.use_case.GetClockSettingsFlow
import de.oljg.glac.feature_clock.domain.use_case.RemoveTheme
import de.oljg.glac.feature_clock.domain.use_case.UpdateClockBrightness
import de.oljg.glac.feature_clock.domain.use_case.UpdateClockSettingsColumnScrollPosition
import de.oljg.glac.feature_clock.domain.use_case.UpdateClockSettingsEndColumnScrollPosition
import de.oljg.glac.feature_clock.domain.use_case.UpdateClockSettingsSectionBrightnessIsExpanded
import de.oljg.glac.feature_clock.domain.use_case.UpdateClockSettingsSectionClockCharIsExpanded
import de.oljg.glac.feature_clock.domain.use_case.UpdateClockSettingsSectionColorsIsExpanded
import de.oljg.glac.feature_clock.domain.use_case.UpdateClockSettingsSectionDisplayIsExpanded
import de.oljg.glac.feature_clock.domain.use_case.UpdateClockSettingsSectionDividerIsExpanded
import de.oljg.glac.feature_clock.domain.use_case.UpdateClockSettingsSectionPreviewIsExpanded
import de.oljg.glac.feature_clock.domain.use_case.UpdateClockSettingsSectionThemeIsExpanded
import de.oljg.glac.feature_clock.domain.use_case.UpdateClockSettingsStartColumnScrollPosition
import de.oljg.glac.feature_clock.domain.use_case.UpdateClockThemeName
import de.oljg.glac.feature_clock.domain.use_case.UpdateOverrideSystemBrightness
import de.oljg.glac.feature_clock.domain.use_case.UpdateThemes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideClockSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<ClockSettings> {
        return DataStoreFactory.create(
            serializer = ClockSettingsSerializer,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.dataStoreFile(ClockSettingsDefaults.DATASTORE_FILE_NAME) }
        )
    }

    @Provides
    @Singleton
    fun provideClockSettingsRepository(
        dataStore: DataStore<ClockSettings>
    ): ClockSettingsRepository {
        return ClockSettingsRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideClockUseCases(
        repository: ClockSettingsRepository
    ): ClockUseCases = ClockUseCases(
        GetClockSettingsFlow(repository),
        UpdateClockThemeName(repository),
        UpdateOverrideSystemBrightness(repository),
        UpdateClockBrightness(repository),
        UpdateClockSettingsSectionPreviewIsExpanded(repository),
        UpdateClockSettingsSectionThemeIsExpanded(repository),
        UpdateClockSettingsSectionDisplayIsExpanded(repository),
        UpdateClockSettingsSectionClockCharIsExpanded(repository),
        UpdateClockSettingsSectionDividerIsExpanded(repository),
        UpdateClockSettingsSectionColorsIsExpanded(repository),
        UpdateClockSettingsSectionBrightnessIsExpanded(repository),
        UpdateClockSettingsColumnScrollPosition(repository),
        UpdateClockSettingsStartColumnScrollPosition(repository),
        UpdateClockSettingsEndColumnScrollPosition(repository),
        UpdateThemes(repository),
        RemoveTheme(repository)
    )

    @Provides
    @Singleton
    fun provideAlarmSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<AlarmSettings> {
        return DataStoreFactory.create(
            serializer = AlarmSettingsSerializer,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.dataStoreFile(AlarmDefaults.DATASTORE_FILE_NAME) }
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
            getAlarms = GetAlarms(repository),
            handleOutdatedAlarms = HandleOutdatedAlarms(repository)
        )
    }
}
