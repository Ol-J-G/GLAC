package de.oljg.glac.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.oljg.glac.core.alarms.data.AlarmSettingsRepository
import de.oljg.glac.core.alarms.data.manager.AndroidAlarmScheduler
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
        return AlarmSettingsRepository(context)
    }

    @Provides
    @Singleton
    fun provideAlarmScheduler(
        @ApplicationContext context: Context
    ): AndroidAlarmScheduler {
        return AndroidAlarmScheduler(context)
    }
}
